package de.nierhain.powertool.events.handlers;

import de.nierhain.powertool.PowerTool;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreakBlockEventHandler {
    private static final float newSpeed = 300.0f;
    @SubscribeEvent
    public static void OnPlayerBreakingBlock(PlayerEvent.BreakSpeed event){
        if(event.getState().is(Tags.Blocks.OBSIDIAN) && event.getOriginalSpeed() < newSpeed){
            event.setNewSpeed(newSpeed);
            return;
        }
        if(event.getState().is(BlockTags.DIRT)) event.setNewSpeed(10.0f);
    }
}
