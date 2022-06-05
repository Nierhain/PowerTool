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
    }
}
