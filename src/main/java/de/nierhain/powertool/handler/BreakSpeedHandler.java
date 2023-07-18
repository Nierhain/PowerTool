package de.nierhain.powertool.handler;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreakSpeedHandler {
    private static float TICKS_TO_BREAK = 3;
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event){

        boolean isTool = event.getEntity().getMainHandItem().is(Registration.POWER_TOOL.get());
        Level level = event.getEntity().getLevel();
        Optional<BlockPos> pos = event.getPosition();
        float blockHardness = event.getState().getDestroySpeed(level, pos.get());
        float hardnessCoefficient = 30f; // see: https://minecraft.fandom.com/wiki/Breaking#Calculation
        if(isTool && pos.isPresent() && !level.isClientSide()){
            if(blockHardness < 1) {
                return;
            }
            float newSpeed = 1 / TICKS_TO_BREAK * hardnessCoefficient * blockHardness;
            PowerTool.LOGGER.debug("newSpeed: "  + newSpeed + " - blockHardness:" + blockHardness);


            event.setNewSpeed(newSpeed);
        }
    }
}
