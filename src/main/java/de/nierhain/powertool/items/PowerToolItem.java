package de.nierhain.powertool.items;

import de.nierhain.powertool.data.tiers.PowerToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.tags.BlockTagsProvider;
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

import java.util.ArrayList;
import java.util.List;

import static de.nierhain.powertool.data.PowerToolTags.MINEABLE_WITH_POWERTOOL;

public class PowerToolItem extends DiggerItem{

    private boolean isExtendedRange = true;
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
    public boolean isFoil(ItemStack pStack) {
        return isExtendedRange;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.isShiftKeyDown()){
            toggleExtended();
            player.displayClientMessage(new TextComponent("Toggled 3x3"), true);
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
        }

        player.startUsingItem(hand);
        return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        state
        double range = 3;
        Vec3 look = entity.getLookAngle();
        Vec3 start = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        Vec3 end = new Vec3(start.x + look.x * range, start.y + look.y * range, start.z + look.z * range);
        BlockHitResult lookingAt = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
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



    public void toggleExtended(){
        isExtendedRange = !isExtendedRange;
    }
}
