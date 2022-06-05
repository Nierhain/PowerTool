package de.nierhain.powertool.items;

import de.nierhain.powertool.data.tiers.PowerToolTier;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import static de.nierhain.powertool.data.PowerToolTags.MINEABLE_WITH_POWERTOOL;

public class PowerToolItem extends DiggerItem{


    public PowerToolItem() {
        super(1, 1, PowerToolTier.POWERTOOL, MINEABLE_WITH_POWERTOOL, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }
}
