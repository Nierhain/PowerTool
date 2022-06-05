package de.nierhain.powertool.datagen;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.ItemModels;
import de.nierhain.powertool.data.PowerToolLanguageProvider;
import de.nierhain.powertool.data.PowerToolTags;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        if(event.includeServer()){
            generator.addProvider(new PowerToolTags(generator, event.getExistingFileHelper()));
        }
        if(event.includeClient()){
            generator.addProvider(new ItemModels(generator, PowerTool.MODID, event.getExistingFileHelper()));
            generator.addProvider(new PowerToolLanguageProvider(generator, PowerTool.MODID, "en_us"));
        }
    }
}
