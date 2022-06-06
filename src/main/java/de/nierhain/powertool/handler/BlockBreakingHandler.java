package de.nierhain.powertool.handler;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.PowerToolTags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakingHandler {
    @SubscribeEvent
    public static void OnBlockBreakingStart(PlayerInteractEvent.LeftClickBlock event){
        boolean isHoldingPowerTool = event.getItemStack().is(PowerToolTags.PowerToolItemTag.POWER_TOOL);

    }
}
