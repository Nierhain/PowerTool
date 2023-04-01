package de.nierhain.powertool.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.recipe.NBTRecipe;
import de.nierhain.powertool.setup.Registration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
                .unlockedBy("root", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
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
        ShapedRecipeBuilder.shaped(Registration.MAGNET_UPGRADE.get())
                .pattern("  i")
                .pattern(" r ")
                .pattern("i  ")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe);
        ShapedRecipeBuilder.shaped(Registration.FORTUNE_UPGRADE.get())
                .pattern("rlr")
                .pattern("lal")
                .pattern("rlr")
                .define('a', Tags.Items.GEMS_AMETHYST)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('l', Tags.Items.GEMS_LAPIS)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe);
        NBTRecipeBuilder.build(Registration.POWER_TOOL.get())
                .addIngredient(Registration.POWER_TOOL.get())
                .addIngredient(Registration.UPGRADE_ITEM.get())
                .addNBT("isUpgraded", true)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe, new ResourceLocation(PowerTool.MODID,"upgraded_powertool"));
        NBTRecipeBuilder.build(Registration.POWER_TOOL.get())
                .addIngredient(Registration.POWER_TOOL.get())
                .addIngredient(Registration.MAGNET_UPGRADE.get())
                .addNBT("isMagnetic", true)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe, new ResourceLocation(PowerTool.MODID,"magnetic_powertool"));
        NBTRecipeBuilder.build(Registration.POWER_TOOL.get())
                .addIngredient(Registration.POWER_TOOL.get())
                .addIngredient(Registration.FORTUNE_UPGRADE.get())
                .addNBT("isLucky", true)
                .group("powertool")
                .unlockedBy("powertool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_INGOT))
                .save(finishedRecipe, new ResourceLocation(PowerTool.MODID,"lucky_powertool"));
    }

    public static class NBTRecipeBuilder implements RecipeBuilder {
        private final Item result;
        private final int count;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final Advancement.Builder advancement = Advancement.Builder.advancement();
        private final JsonObject nbt = new JsonObject();
        @javax.annotation.Nullable
        private String group;

        public NBTRecipeBuilder(ItemLike pResult, int pCount) {
            this.result = pResult.asItem();
            this.count = pCount;
        }

        public static NBTRecipeBuilder build(ItemLike result, int count){
            return new NBTRecipeBuilder(result, count);
        }

        public static NBTRecipeBuilder build(ItemLike result){
            return new NBTRecipeBuilder(result, 1);
        }

        public NBTRecipeBuilder addIngredient(Ingredient item, int count){
            for (int i = 0; i < count; i++){
                ingredients.add(item);
            }
            return this;
        }

        public NBTRecipeBuilder addIngredient(ItemLike item){
            return this.addIngredient(Ingredient.of(item), 1);
        }

        public NBTRecipeBuilder addIngredient(ItemLike item, int count){
            return this.addIngredient(Ingredient.of(item), count);
        }

        public NBTRecipeBuilder addIngredient(TagKey<Item> item){
            return this.addIngredient(Ingredient.of(item), 1);
        }
        public NBTRecipeBuilder addIngredient(TagKey<Item> item, int count){
            return this.addIngredient(Ingredient.of(item), count);
        }

        public NBTRecipeBuilder addNBT(String name, String data){
            nbt.addProperty(name, data);
            return this;
        }

        public NBTRecipeBuilder addNBT(String name, int data){
            nbt.addProperty(name, data);
            return this;
        }

        public NBTRecipeBuilder addNBT(String name, boolean data){
            nbt.addProperty(name, data);
            return this;
        }

        public NBTRecipeBuilder addNBT(String name, Character data){
            nbt.addProperty(name, data);
            return this;
        }

        @Override
        public NBTRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
            this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String group) {
            this.group = group;
            return this;
        }

        @Override
        public Item getResult() {
            return result;
        }

        @Override
        public void save(Consumer<FinishedRecipe> finishedRecipes, ResourceLocation recipeId) {
            this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
            finishedRecipes.accept(new FinishedNBTRecipe(recipeId, this.group, this.result, this.count,this.ingredients, this.nbt));
        }
    }

    public static class FinishedNBTRecipe implements FinishedRecipe{
        private final ResourceLocation recipeId;
        private final Advancement.Builder advancement = Advancement.Builder.advancement();
        private final ResourceLocation advancementId;
        private final Item result;
        private final int count;
        private JsonObject nbt;
        private final String group;
        private final List<Ingredient> ingredients;

        public FinishedNBTRecipe(ResourceLocation recipeId, String group, Item result,int count, List<Ingredient> ingredients, JsonObject nbt){
            this.recipeId = recipeId;
            this.nbt = nbt;
            this.group = group;
            this.ingredients = ingredients;
            this.result = result;
            this.count = count;
            this.advancementId = new ResourceLocation(this.recipeId.getNamespace(), "recipes/" + this.recipeId.getPath());
        }
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("group", this.group);
            JsonArray jsonArray = new JsonArray();
            for (Ingredient ing:
                 this.ingredients) {
                jsonArray.add(ing.toJson());
            }
            json.add("ingredients", jsonArray);
            JsonObject result = new JsonObject();
            result.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            result.add("nbt", this.nbt);

            if(this.count > 1){
                result.addProperty("count", this.count);
            }

            json.add("result", result);
        }

        @Override
        public ResourceLocation getId() {
            return this.recipeId;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return NBTRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
