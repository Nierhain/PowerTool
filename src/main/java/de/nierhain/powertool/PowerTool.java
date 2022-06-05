package de.nierhain.powertool;

import com.mojang.logging.LogUtils;
import de.nierhain.powertool.items.PowerToolItem;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod("powertool")
public class PowerTool
{
    public static final String MODID = "powertool";
    private static final Logger LOGGER = LogUtils.getLogger();


    public PowerTool()
    {
        Registration.init();
        MinecraftForge.EVENT_BUS.register(this);
    }


}
