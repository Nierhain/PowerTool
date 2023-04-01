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
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
            BlockTagsProvider blockTagsProvider = new PowerToolTags(generator, event.getExistingFileHelper());
            generator.addProvider(event.includeServer(),blockTagsProvider);
            generator.addProvider(event.includeServer(),new PowerToolTags.PowerToolItemTag(generator, blockTagsProvider, event.getExistingFileHelper()));
            generator.addProvider(event.includeServer(),new PowerToolRecipe(generator));
            generator.addProvider(event.includeClient(),new ItemModels(generator, PowerTool.MODID, event.getExistingFileHelper()));
            generator.addProvider(event.includeClient(),new PowerToolLanguageProvider(generator, PowerTool.MODID, "en_us"));
    }
}
