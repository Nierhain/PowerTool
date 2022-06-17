package de.nierhain.powertool.items;

import de.nierhain.powertool.data.PowerToolTags;
import de.nierhain.powertool.data.tiers.PowerToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static de.nierhain.powertool.data.PowerToolTags.MINEABLE_WITH_POWERTOOL;

public class PowerToolItem extends DiggerItem{

    private static String extendedTag = "isExtended";
    private static String upgradedTag = "isUpgraded";
    private static final int THREE_BY_THREE = 1;
    private static final int FIVE_BY_FIVE = 2;
    public PowerToolItem() {
        super(0, 0, PowerToolTier.POWERTOOL, MINEABLE_WITH_POWERTOOL, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag tag = pStack.getTag();
        if(!tag.contains(extendedTag)) tag.putInt(extendedTag, 0);
        if(!tag.contains(upgradedTag)) tag.putBoolean(upgradedTag, false);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isExtended(stack);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return     state.is(BlockTags.LEAVES)
                || state.is(Blocks.COBWEB)
                || state.is(Blocks.GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.DEAD_BUSH)
                || state.is(Blocks.HANGING_ROOTS)
                || state.is(Blocks.VINE)
                || state.is(Blocks.TRIPWIRE)
                || state.is(BlockTags.WOOL)
                || state.is(MINEABLE_WITH_POWERTOOL);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof net.minecraftforge.common.IForgeShearable target) {
            if (entity.level.isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
            if (target.isShearable(stack, entity.level, pos)) {
                java.util.List<ItemStack> drops = target.onSheared(player, stack, entity.level, pos,
                        net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, stack));
                java.util.Random rand = new java.util.Random();
                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });
                stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if(player.isShiftKeyDown()){
            int extension = toggleExtended(item);
            TextComponent state = getModeTextComponent(extension);
            player.displayClientMessage(new TextComponent("Mode: ").append(state), true);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if(!isExtended(stack)) {
            return super.mineBlock(stack, level, state, pos, entity);
        }

        List<BlockPos> blocks = getExtraBlocks(level, pos, entity, getMode(stack));
        for(BlockPos block : blocks){
            level.getBlockState(block);
        }
        if(!level.isClientSide) {
            for (BlockPos extra:
                 blocks) {
                BlockState extraState = level.getBlockState(extra);
                Block block = extraState.getBlock();
                if(!extraState.isAir() && extraState.is(MINEABLE_WITH_POWERTOOL)){
                    block.playerDestroy(level, (Player) entity,  extra, extraState, level.getBlockEntity(extra), stack);
                    level.removeBlock(extra, false);
                }
            }
        }

        return super.mineBlock(stack, level, state, pos, entity);
    }

    @NotNull
    public static List<BlockPos> getExtraBlocks(Level level, BlockPos pos, LivingEntity entity, int mode) {
        if(mode == 0) return new ArrayList<BlockPos>();
        BlockHitResult lookingAt = getPlayerPOVHitResult(level, (Player) entity, ClipContext.Fluid.NONE);
        Direction side = lookingAt.getDirection();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? entity.getDirection() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.getClockWise() : side.getCounterClockWise();
        Direction left = right.getOpposite();

        List<BlockPos> blocks = new ArrayList<>();

        int size = getModeSize(mode);
        BlockPos start = pos.relative(down).relative(right, size / 2);
        BlockPos currentLayer = start;
        for(int i = 1; i <= size; i++){
            for(int j = 1; j < size; j++){
                blocks.add(currentLayer.relative(left, j));
            }
            blocks.add(currentLayer);
            currentLayer = start.relative(up, i);
        }
        return blocks;
    }

    private static int getModeSize(int mode){
        if(mode == THREE_BY_THREE) return 3;
        if(mode == FIVE_BY_FIVE) return 5;
        return 0;
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if(pState.is(Tags.Blocks.OBSIDIAN)) return 300.0f;
        if(pState.is(BlockTags.DIRT)) return 8.0f;
        return super.getDestroySpeed(pStack, pState);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CompoundTag tag = pStack.getTag();
        if(tag != null && !tag.contains(extendedTag)) {
            pTooltipComponents.add(new TextComponent("Mode: ").append(getModeTextComponent(tag.getInt(extendedTag))));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    public int toggleExtended(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.contains(extendedTag)){
            tag.putInt(extendedTag, 0);
            return 0;
        }
        int value = tag.getInt(extendedTag) + 1;
        if(value >= 3 || (value > 1 && !isUpgraded(tag))) value = 0;
        tag.putInt(extendedTag, value);
        return tag.getInt(extendedTag);
    }

    public static boolean isExtended(ItemStack stack){
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(extendedTag) && tag.getInt(extendedTag) > 0;
    }

    public static int getMode(ItemStack stack){
        CompoundTag tag = stack.getTag();
        return tag.getInt(extendedTag);
    }

    public TextComponent getModeTextComponent(int mode){
        if(mode == THREE_BY_THREE) return new TextComponent("\u00A7b3x3");
        if(mode == FIVE_BY_FIVE) return new TextComponent("\u00A755x5");
        return new TextComponent("1x1");
    }

    public boolean isUpgraded(CompoundTag tag){
        if(!tag.contains(upgradedTag)) {
            tag.putBoolean(upgradedTag, false);
            return false;
        }
        return tag.getBoolean(upgradedTag);
    }
}
