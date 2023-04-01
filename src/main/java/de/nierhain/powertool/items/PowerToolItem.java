package de.nierhain.powertool.items;

import de.nierhain.powertool.data.tiers.PowerToolTier;
import de.nierhain.powertool.setup.NBTTags;
import de.nierhain.powertool.utils.PowerToolMode;
import de.nierhain.powertool.utils.PowerToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.nierhain.powertool.data.PowerToolTags.MINEABLE_WITH_POWERTOOL;
import static de.nierhain.powertool.setup.Registration.ITEM_GROUP;
import static de.nierhain.powertool.utils.PowerToolUtils.*;

public class PowerToolItem extends DiggerItem{
    
    private static final int THREE_BY_THREE = 1;
    private static final int FIVE_BY_FIVE = 2;
    public PowerToolItem() {
        super(0, 0, PowerToolTier.POWERTOOL, MINEABLE_WITH_POWERTOOL, new Item.Properties().tab(ITEM_GROUP));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(NBTTags.EXTENDED, 0);
        tag.putBoolean(NBTTags.UPGRADED, false);
        tag.putBoolean(NBTTags.LUCKY, false);
        tag.putBoolean(NBTTags.MAGNETIC, false);
        return stack;
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
        return false;
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
        if(player.isShiftKeyDown()) {
            PowerToolMode extension = toggleExtended(item);
            Component state = getModeTextComponent(extension);
            player.displayClientMessage(Component.literal("Mode: ").append(state), true);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState block) {
        if(block.is(Tags.Blocks.OBSIDIAN) || block.is(Tags.Blocks.STORAGE_BLOCKS_NETHERITE) || block.is(Tags.Blocks.ORES_NETHERITE_SCRAP)){
            return 150.0f;
        }
        return super.getDestroySpeed(stack, block);
    }


    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos originalPos, LivingEntity entity) {
        Player player = (Player) entity;
        List<BlockPos> blocks = getAllBlocks(level, originalPos, entity, getMode(stack));
        if(!level.isClientSide) {
            ItemStack tempTool = stack.copy();
            int fortune = 0;
            if(isToolLucky(stack)){
                fortune = Enchantments.BLOCK_FORTUNE.getMaxLevel();
                tempTool.enchant(Enchantments.BLOCK_FORTUNE, fortune);
            }
            for (BlockPos pos: blocks) {
                BlockState extraState = level.getBlockState(pos);
                Block block = extraState.getBlock();
                if(!extraState.isAir() && extraState.is(MINEABLE_WITH_POWERTOOL) && !extraState.hasBlockEntity()){
                    List<ItemStack> drops = Block.getDrops(extraState, (ServerLevel) level, pos,null, entity, tempTool);
                    for(ItemStack drop: drops){
                        if(drop != null){
                            if(isToolMagnetic(stack)){
                                int wasPickedUp = ForgeEventFactory.onItemPickup(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop), player);
                                if(wasPickedUp == 0){
                                    if(!player.addItem(drop)){
                                        Block.popResource(level, pos, drop);
                                    }
                                }
                            } else {
                                Block.popResource(level, pos, drop);
                            }
                        }
                    }

                    int exp = block.getExpDrop(extraState, level, RandomSource.create(), pos, fortune, 0);
                    if(isToolMagnetic(stack)) {
                        player.giveExperiencePoints(exp);
                    } else {
                        block.popExperience((ServerLevel) level, pos, exp);
                    }

                    level.removeBlock(pos, false);
                    BlockEvent.BreakEvent event = fixForgeEventBreakBlock(extraState, player, level, pos, tempTool);
                    MinecraftForge.EVENT_BUS.post(event);
                }
            }
        }
        return true;
    }

    private static BlockEvent.BreakEvent fixForgeEventBreakBlock(BlockState state, Player player, Level world, BlockPos pos, ItemStack tool) {
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
        // Handle empty block or player unable to break block scenario
        if (state != null && ForgeHooks.isCorrectToolForDrops(state, player)) {
            int bonusLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
            int silklevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool);
            event.setExpToDrop(state.getExpDrop(world, RandomSource.create(), pos, bonusLevel, silklevel));
        }

        return event;
    }

    @NotNull
    public static List<BlockPos> getAllBlocks(Level level, BlockPos pos, LivingEntity entity, PowerToolMode mode) {
        BlockHitResult lookingAt = getPlayerPOVHitResult(level, (Player) entity, ClipContext.Fluid.NONE);
        Direction side = lookingAt.getDirection();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? entity.getDirection() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.getClockWise() : side.getCounterClockWise();
        Direction left = right.getOpposite();

        List<BlockPos> blocks = new ArrayList<>();
        if(mode == PowerToolMode.SINGLE) {
            blocks.add(pos);
            return blocks;
        }
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

    private static int getModeSize(PowerToolMode mode){
        if(mode == PowerToolMode.TRIPLE) return 3;
        if(mode == PowerToolMode.QUINTUPLE) return 5;
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CompoundTag tag = pStack.getTag();
        if(tag != null) {
            pTooltipComponents.add(Component.literal("Mode: ").append(getModeTextComponent(getMode(pStack))));
            pTooltipComponents.add(Component.literal("Magnetic: ").append(getBooleanComponent(tag.getBoolean(NBTTags.MAGNETIC))));
            pTooltipComponents.add(Component.literal("Lucky: ").append(getBooleanComponent(tag.getBoolean(NBTTags.LUCKY))));
            pTooltipComponents.add(Component.literal("Upgraded: ").append(getBooleanComponent(tag.getBoolean(NBTTags.UPGRADED))));
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

    public PowerToolMode toggleExtended(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        if(getMode(stack) == PowerToolMode.SINGLE) {
            tag.putInt(NBTTags.EXTENDED, 1);
            return PowerToolMode.TRIPLE;
        }
        if(getMode(stack) == PowerToolMode.TRIPLE && isUpgraded(stack)){
            tag.putInt(NBTTags.EXTENDED, 2);
            return PowerToolMode.QUINTUPLE;
        }
        tag.putInt(NBTTags.EXTENDED, 0);
        return PowerToolMode.SINGLE;
    }



    public Component getModeTextComponent(PowerToolMode mode){
        if(mode == PowerToolMode.TRIPLE) return Component.literal("\u00A7b3x3");
        if(mode == PowerToolMode.QUINTUPLE) return Component.literal("\u00A755x5");
        return Component.literal("1x1");
    }

    public Component getBooleanComponent(boolean value){
        return Component.literal(value ? "yes" : "no");
    }
}
