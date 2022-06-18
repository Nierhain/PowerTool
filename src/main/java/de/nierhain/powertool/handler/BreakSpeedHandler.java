package de.nierhain.powertool.handler;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.setup.Registration;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreakSpeedHandler {
    private static float TICKS_TO_BREAK = 3;
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event){
        boolean isTool = event.getPlayer().getMainHandItem().is(Registration.POWER_TOOL.get());
        if(isTool){
            float blockHardness = event.getState().getDestroySpeed(event.getPlayer().getLevel(), event.getPos());
            float newSpeed = 1 / TICKS_TO_BREAK * 30 * blockHardness; // see: https://minecraft.fandom.com/wiki/Breaking#Calculation
            event.setNewSpeed(newSpeed);
        }
    }
}
