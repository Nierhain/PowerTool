package de.nierhain.powertool.data.tiers;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

import static de.nierhain.powertool.data.PowerToolTags.MINEABLE_WITH_POWERTOOL;

public class PowerToolTier {

    public static final ForgeTier POWERTOOL = new ForgeTier(Integer.MAX_VALUE, -1, 15, 3, Integer.MAX_VALUE, MINEABLE_WITH_POWERTOOL, () -> Ingredient.EMPTY);

}
