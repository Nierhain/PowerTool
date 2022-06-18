package de.nierhain.powertool.data;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(Registration.POWER_TOOL.get().getRegistryName().getPath(),mcLoc("item/generated"), "layer0", modLoc("item/powertool"));
        singleTexture(Registration.UPGRADE_ITEM.get().getRegistryName().getPath(),mcLoc("item/generated"), "layer0", modLoc("item/upgrade"));
        singleTexture(Registration.MAGNET_UPGRADE.get().getRegistryName().getPath(),mcLoc("item/generated"), "layer0", modLoc("item/magnet_upgrade"));
        singleTexture(Registration.FORTUNE_UPGRADE.get().getRegistryName().getPath(),mcLoc("item/generated"), "layer0", modLoc("item/fortune_upgrade"));
    }
}
