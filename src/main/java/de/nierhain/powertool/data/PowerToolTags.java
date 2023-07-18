package de.nierhain.powertool.data;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.items.PowerToolItem;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PowerToolTags extends BlockTagsProvider {
    public static final TagKey<Block> MINEABLE_WITH_POWERTOOL = BlockTags.create(new ResourceLocation("forge", "mineable/powertool"));


    public PowerToolTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider,  PowerTool.MODID, existingFileHelper);
    }

    @Override
    public @NotNull String getName() {
        return "Mineable with Powertool";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MINEABLE_WITH_POWERTOOL).addTags(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_HOE, BlockTags.MINEABLE_WITH_SHOVEL
        );
    }

    public static class PowerToolItemTag extends ItemTagsProvider {

        public PowerToolItemTag(PackOutput output,CompletableFuture<HolderLookup.Provider> provider,BlockTagsProvider blockTags, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, blockTags.contentsGetter(), PowerTool.MODID, existingFileHelper);
        }

        public static final TagKey<Item> POWER_TOOL = ItemTags.create(new ResourceLocation("forge", "powertool"));


        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(POWER_TOOL).add(Registration.POWER_TOOL.get());
        }
    }
}
