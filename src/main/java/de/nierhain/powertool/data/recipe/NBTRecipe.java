package de.nierhain.powertool.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.utils.DefaultTags;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;

public class NBTRecipe extends ShapelessRecipe {
    private final ResourceLocation id;
    private final ItemStack output;
    private final String group;
    private final NonNullList<Ingredient> items;

    public NBTRecipe(ResourceLocation id,String group, ItemStack output, NonNullList<Ingredient> items) {
        super(id, group, output, items);
        this.id = id;
        this.output = output;
        this.items = items;
        this.group = group;
    }

    @Override
    public boolean matches(CraftingContainer container, Level pLevel) {
        NonNullList<Ingredient> ingredients = items;
        ArrayList<ItemStack> inputs = new ArrayList<>();
        for(int i = 0; i < container.getContainerSize(); i++){
            ItemStack stack = container.getItem(i);
            if(!stack.isEmpty()) inputs.add(stack);
        }

        return RecipeMatcher.findMatches(inputs, this.items) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack result = output.copy();
        for(int i = 0; i < container.getContainerSize(); i++){
            ItemStack stack = container.getItem(i);
            if(!stack.isEmpty()){
                if(stack.is(result.getItem()) && stack.hasTag()){
                    CompoundTag resultTag = result.getOrCreateTag();
                    CompoundTag inputTag = stack.getTag();
                    Set<String> keys = inputTag.getAllKeys();
                    for(String key: keys) {
                        if (DefaultTags.isBoolean(key) && inputTag.getBoolean(key) != DefaultTags.getDefaultBoolean(key)) {
                            resultTag.putBoolean(key, inputTag.getBoolean(key));
                            continue;
                        }
                        if (DefaultTags.isString(key) && !inputTag.getString(key).equals(DefaultTags.getDefaultString(key))) {
                            resultTag.putString(key, inputTag.getString(key));
                            continue;
                        }
                        if (DefaultTags.isInt(key) && inputTag.getInt(key) != DefaultTags.getDefaultInt(key)) {
                            resultTag.putInt(key, inputTag.getInt(key));
                        }
                    }
                }
            }
        }
        return result;
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Serializer implements RecipeSerializer<NBTRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(PowerTool.MODID, "nbt_crafting");

        @Override
        public NBTRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            CompoundTag tag = output.getTag();
            JsonObject nbt = GsonHelper.getAsJsonObject(json, "result").getAsJsonObject("nbt");
            String group = GsonHelper.getAsString(json, "group");
            Set<String> keys = nbt.keySet();
            for (String key: keys) {
                JsonElement element = nbt.get(key);
                if(!element.isJsonPrimitive()) continue;
                JsonPrimitive primitive = element.getAsJsonPrimitive();

                if(primitive.isBoolean()) tag.putBoolean(key, primitive.getAsBoolean());
                if(primitive.isNumber()) tag.putInt(key, primitive.getAsInt());
                if(primitive.isString()) tag.putString(key, primitive.getAsString());
            }
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++){
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            return new NBTRecipe(id,group, output, inputs);
        }

        @Nullable
        @Override
        public NBTRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            for(int i = 0; i < inputs.size(); i++){
                inputs.set(i, Ingredient.fromNetwork(buf));
            }


            ItemStack output = buf.readItem();
            return new NBTRecipe(recipeId, group, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, NBTRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeInt(recipe.items.size());
            for(Ingredient ing : recipe.getIngredients()){
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        private static <G> Class<G> castClass(Class<?> cls){
            return (Class<G>)cls;
        }
    }
}
