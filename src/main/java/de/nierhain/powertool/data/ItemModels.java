package de.nierhain.powertool.data;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(ForgeRegistries.ITEMS.getKey(Registration.POWER_TOOL.get()).toString(), mcLoc("item/generated"), "layer0", modLoc("item/powertool"));
        singleTexture(ForgeRegistries.ITEMS.getKey(Registration.UPGRADE_ITEM.get()).toString(),mcLoc("item/generated"), "layer0", modLoc("item/upgrade"));
    }
}
