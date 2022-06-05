package de.nierhain.powertool.data;

import de.nierhain.powertool.PowerTool;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class PowerToolTags extends BlockTagsProvider {
    public static final TagKey<Block> MINEABLE_WITH_POWERTOOL = BlockTags.create(new ResourceLocation("forge", "mineable/powertool"));

    public PowerToolTags(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, PowerTool.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(MINEABLE_WITH_POWERTOOL).addTags(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_HOE, BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public @NotNull String getName() {
        return "Mineable with Powertool";
    }
}
