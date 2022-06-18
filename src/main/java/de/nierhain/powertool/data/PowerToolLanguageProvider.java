package de.nierhain.powertool.data;

import de.nierhain.powertool.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import static de.nierhain.powertool.setup.Registration.TAB_NAME;

public class PowerToolLanguageProvider extends LanguageProvider {

    public PowerToolLanguageProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + TAB_NAME, "PowerTool");
        add(Registration.POWER_TOOL.get(), "Power Tool");
        add(Registration.UPGRADE_ITEM.get(), "Power Tool Upgrade");
        add(Registration.FORTUNE_UPGRADE.get(), "Fortune Tool Upgrade");
        add(Registration.MAGNET_UPGRADE.get(), "Magnet Tool Upgrade");
    }
}
