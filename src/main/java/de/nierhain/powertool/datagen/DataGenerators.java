package de.nierhain.powertool.datagen;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.ItemModels;
import de.nierhain.powertool.data.PowerToolLanguageProvider;
import de.nierhain.powertool.data.PowerToolRecipe;
import de.nierhain.powertool.data.PowerToolTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        if(event.includeServer()){
            BlockTagsProvider blockTagsProvider = new PowerToolTags(generator, event.getExistingFileHelper());
            generator.addProvider(blockTagsProvider);
            generator.addProvider(new PowerToolTags.PowerToolItemTag(generator, blockTagsProvider, event.getExistingFileHelper()));
            generator.addProvider(new PowerToolRecipe(generator));
        }
        if(event.includeClient()){
            generator.addProvider(new ItemModels(generator, PowerTool.MODID, event.getExistingFileHelper()));
            generator.addProvider(new PowerToolLanguageProvider(generator, PowerTool.MODID, "en_us"));
        }
    }
}
