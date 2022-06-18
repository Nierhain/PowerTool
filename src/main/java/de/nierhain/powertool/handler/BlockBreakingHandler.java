package de.nierhain.powertool.handler;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.PowerToolTags;
import de.nierhain.powertool.utils.PowerToolUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakingHandler {
    @SubscribeEvent
    public static void onBlockBreaking(BlockEvent.BreakEvent event){
        int wasPickedUp = ForgeEventFactory.onItemPickup(new ItemEntity(event.getPlayer().getLevel(), (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), ItemStack.EMPTY), event.getPlayer());
        if(wasPickedUp == 0){

        }

    }
}
