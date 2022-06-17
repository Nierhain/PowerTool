package de.nierhain.powertool.setup;

import de.nierhain.powertool.items.PowerToolItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static de.nierhain.powertool.PowerTool.MODID;

public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
    }
    public static final RegistryObject<Item> POWER_TOOL = ITEMS.register("powertool", PowerToolItem::new);
    public static final RegistryObject<Item> UPGRADE_ITEM = ITEMS.register("upgrade",() -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).fireResistant()));
}
