package de.nierhain.powertool.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PowerToolAdvancementProvider extends ForgeAdvancementProvider {

    public PowerToolAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper fileHelper, List<AdvancementGenerator> generators) {
        super(output, provider, fileHelper, generators);
    }

    public static class Generator implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
            Advancement.Builder.advancement()
                    .parent(ResourceLocation.tryParse("nether/obtain_ancient_debris"))
                    .addCriterion("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                    .save(saver, ResourceLocation.tryParse("powertool"),existingFileHelper);
        }
    }
}
