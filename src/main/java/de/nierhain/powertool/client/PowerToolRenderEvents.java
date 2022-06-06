package de.nierhain.powertool.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.PowerToolTags;
import de.nierhain.powertool.items.PowerToolItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PowerToolRenderEvents {
    @SubscribeEvent
    public static void OnBlockHighlight(DrawSelectionEvent.HighlightBlock event){
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;

        if(level == null || player == null) return;

        if(!isHoldingExtendedPowerTool(player) || !isHittingBlock(Minecraft.getInstance().hitResult)) return;

        BlockHitResult blockTrace = event.getTarget();
        BlockPos origin = blockTrace.getBlockPos();
        BlockState state = level.getBlockState(origin);

        List<BlockPos> extraBlocks = PowerToolItem.getExtraBlocks(level, origin, player);
        // set up renderer
        LevelRenderer levelRenderer = event.getLevelRenderer();
        PoseStack matrices = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.lines());
        matrices.pushPose();

        // start drawing
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        Entity viewEntity = renderInfo.getEntity();
        Vec3 vector3d = renderInfo.getPosition();
        double x = vector3d.x();
        double y = vector3d.y();
        double z = vector3d.z();
        for(BlockPos extra : extraBlocks){
            levelRenderer.renderHitOutline(matrices, vertexBuilder, viewEntity, x, y, z, extra, level.getBlockState(extra));
        }
        matrices.popPose();
        buffers.endBatch();
    }

    @SubscribeEvent
    public static void OnExtraBlockDamage(RenderLevelLastEvent event){
        MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
        if(controller == null || !controller.isDestroying()) return;

        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if(level == null || player == null || Minecraft.getInstance().getCameraEntity() == null) return;
        if(isHoldingExtendedPowerTool(player) || !isHittingBlock(Minecraft.getInstance().hitResult)) return;

        BlockHitResult blockTrace = (BlockHitResult) Minecraft.getInstance().hitResult;
        BlockPos target = blockTrace.getBlockPos();
        BlockDestructionProgress progress = null;
        for (Int2ObjectMap.Entry<BlockDestructionProgress> entry: Minecraft.getInstance().levelRenderer.destroyingBlocks.int2ObjectEntrySet()){
            if(entry.getValue().getPos().equals(target)){
                progress = entry.getValue();
                break;
            }
        }
        if(progress == null) return;

        BlockState state = level.getBlockState(target);
        List<BlockPos> extraBlocks = PowerToolItem.getExtraBlocks(level, target, player);
        // set up buffers
        PoseStack matrices = event.getPoseStack();
        matrices.pushPose();
        MultiBufferSource.BufferSource vertices = event.getLevelRenderer().renderBuffers.crumblingBufferSource();
        VertexConsumer vertexBuilder = vertices.getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress()));

        // finally, render the blocks
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        double x = renderInfo.getPosition().x;
        double y = renderInfo.getPosition().y;
        double z = renderInfo.getPosition().z;
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        for(BlockPos pos : extraBlocks) {
            matrices.pushPose();
            matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
            PoseStack.Pose entry = matrices.last();
            VertexConsumer blockBuilder = new SheetedDecalTextureGenerator(vertexBuilder, entry.pose(), entry.normal());
            dispatcher.renderBreakingTexture(level.getBlockState(pos), pos, level, matrices, blockBuilder);
            matrices.popPose();
        }
        // finish rendering
        matrices.popPose();
        vertices.endBatch();
    }

    private static boolean isHoldingExtendedPowerTool(Player player){
        ItemStack stack = player.getMainHandItem();
        return !stack.isEmpty() && stack.is(PowerToolTags.PowerToolItemTag.POWER_TOOL) && PowerToolItem.isExtended(stack);
    }

    private static boolean isHittingBlock(HitResult result){
        return result != null && result.getType() == HitResult.Type.BLOCK;
    }
}
