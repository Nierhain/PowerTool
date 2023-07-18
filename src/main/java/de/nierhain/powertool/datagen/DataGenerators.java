package de.nierhain.powertool.datagen;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = PowerTool.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        var blockTags = new PowerToolTags(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper());

        generator.addProvider(event.includeServer(),blockTags);
        generator.addProvider(event.includeServer(),new PowerToolTags.PowerToolItemTag(generator.getPackOutput(), event.getLookupProvider(), blockTags, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(),new PowerToolRecipe(generator));
        generator.addProvider(event.includeServer(),new PowerToolAdvancementProvider(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper(), List.of(new PowerToolAdvancementProvider.Generator())));
        generator.addProvider(event.includeClient(),new ItemModels(generator.getPackOutput(), PowerTool.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(),new PowerToolLanguageProvider(generator, PowerTool.MODID, "en_us"));
    }
}
