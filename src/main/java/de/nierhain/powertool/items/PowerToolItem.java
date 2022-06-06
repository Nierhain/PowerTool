package de.nierhain.powertool.items;

import de.nierhain.powertool.data.PowerToolTags;
import de.nierhain.powertool.data.tiers.PowerToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.nierhain.powertool.data.PowerToolTags.MINEABLE_WITH_POWERTOOL;

public class PowerToolItem extends DiggerItem{

    private boolean isExtendedRange = true;
    private static String extendedTag = "isExtended";
    public PowerToolItem() {
        super(0, 0, PowerToolTier.POWERTOOL, MINEABLE_WITH_POWERTOOL, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if(player.isShiftKeyDown()){
            boolean isExtended = toggleExtended(item);
            TextComponent state = new TextComponent(isExtended ? "\u00A7a on" : "\u00A7c off");
            player.displayClientMessage(new TextComponent("3x3 mode: ").append(state), true);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if(!isExtended(stack)) {
            return super.mineBlock(stack, level, state, pos, entity);
        }

        List<BlockPos> blocks = getExtraBlocks(level, pos, entity);
        for(BlockPos block : blocks){
            level.getBlockState(block);
        }
        if(!level.isClientSide) {
            for (BlockPos extra:
                 blocks) {
                BlockState extraState = level.getBlockState(extra);
                Block block = extraState.getBlock();
                if(!extraState.isAir()){
                    block.playerDestroy(level, (Player) entity,  extra, extraState, level.getBlockEntity(extra), stack);
                    level.removeBlock(extra, false);
                }
            }
        }

        return super.mineBlock(stack, level, state, pos, entity);
    }

    @NotNull
    public static List<BlockPos> getExtraBlocks(Level level, BlockPos pos, LivingEntity entity) {
        BlockHitResult lookingAt = getPlayerPOVHitResult(level, (Player) entity, ClipContext.Fluid.NONE);
        Direction side = lookingAt.getDirection();
        boolean vertical = side.getAxis().isVertical();
        Direction up = vertical ? entity.getDirection() : Direction.UP;
        Direction down = up.getOpposite();
        Direction right = vertical ? up.getClockWise() : side.getCounterClockWise();
        Direction left = right.getOpposite();

        List<BlockPos> blocks = new ArrayList<>();
        blocks.add(pos.relative(up).relative(left));
        blocks.add(pos.relative(up));
        blocks.add(pos.relative(up).relative(right));
        blocks.add(pos.relative(left));
        blocks.add(pos.relative(right));
        blocks.add(pos.relative(down).relative(left));
        blocks.add(pos.relative(down).relative(right));
        blocks.add(pos.relative(down));
        return blocks;
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if(pState.is(Tags.Blocks.OBSIDIAN)) return 300.0f;
        if(pState.is(BlockTags.DIRT)) return 8.0f;
        return super.getDestroySpeed(pStack, pState);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    public boolean toggleExtended(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();

        if(!tag.contains(extendedTag)){
            tag.putBoolean(extendedTag, true);
            return true;
        }

        tag.putBoolean(extendedTag, !tag.getBoolean(extendedTag));
        return tag.getBoolean(extendedTag);
    }

    public static boolean isExtended(ItemStack stack){
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(extendedTag) && tag.getBoolean(extendedTag);
    }
}
