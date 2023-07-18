package de.nierhain.powertool.data;


import de.nierhain.powertool.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemModels extends ItemModelProvider {

    public ItemModels(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(ForgeRegistries.ITEMS.getKey(Registration.POWER_TOOL.get()).getPath(),mcLoc("item/generated"), "layer0", modLoc("item/powertool"));
        singleTexture(ForgeRegistries.ITEMS.getKey(Registration.UPGRADE_ITEM.get()).getPath(),mcLoc("item/generated"), "layer0", modLoc("item/upgrade"));
        singleTexture(ForgeRegistries.ITEMS.getKey(Registration.MAGNET_UPGRADE.get()).getPath(),mcLoc("item/generated"), "layer0", modLoc("item/magnet_upgrade"));
        singleTexture(ForgeRegistries.ITEMS.getKey(Registration.FORTUNE_UPGRADE.get()).getPath(),mcLoc("item/generated"), "layer0", modLoc("item/fortune_upgrade"));
    }
}
