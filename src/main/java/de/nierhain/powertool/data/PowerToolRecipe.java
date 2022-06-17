package de.nierhain.powertool.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PowerToolRecipe extends RecipeProvider {
    public PowerToolRecipe(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipe) {
        ShapedRecipeBuilder.shaped(Registration.POWER_TOOL.get())
                .pattern("igg")
                .pattern("gng")
                .pattern("ggd")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('n', Tags.Items.INGOTS_NETHERITE)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe);
        ShapedRecipeBuilder.shaped(Registration.UPGRADE_ITEM.get())
                .pattern("gag")
                .pattern("ada")
                .pattern("gag")
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('a', Tags.Items.GEMS_AMETHYST)
                .define('g', Tags.Items.INGOTS_GOLD)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe);
//        ShapelessRecipeBuilder.shapeless(Registration.UPGRADED_POWER_TOOL.get())
//                .requires(Registration.POWER_TOOL.get())
//                .requires(Registration.UPGRADE_ITEM.get())
//                .group("powertool")
//                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
//                .save(finishedRecipe);
        finishedRecipe.accept(new UpgradeRecipe());
    }

    public static class UpgradeRecipe implements FinishedRecipe {

        private final ResourceLocation recipeId = new ResourceLocation(PowerTool.MODID, "upgraded_powertool");
        private final Advancement.Builder advancement = Advancement.Builder.advancement();
        private final ResourceLocation advancementId;

        public UpgradeRecipe(){
            advancement.addCriterion("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT));
            advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
            Item result = Registration.POWER_TOOL.get();
            advancementId = new ResourceLocation(recipeId.getNamespace(), "recipes/" + result.getItemCategory().getRecipeFolderName() + "/" + recipeId.getPath());
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("group","powertool");
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(Ingredient.of(Registration.POWER_TOOL.get()).toJson());
            jsonArray.add(Ingredient.of(Registration.UPGRADE_ITEM.get()).toJson());
            json.add("ingredients", jsonArray);
            JsonObject result = new JsonObject();
            JsonObject nbt = new JsonObject();
            nbt.addProperty("isExtended", 0);
            nbt.addProperty("isUpgraded", true);
            result.addProperty("item", Registration.POWER_TOOL.getId().toString());
            result.add("nbt", nbt);
            json.add("result", result);
        }

        @Override
        public ResourceLocation getId() {
            return recipeId;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {

            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }

}
