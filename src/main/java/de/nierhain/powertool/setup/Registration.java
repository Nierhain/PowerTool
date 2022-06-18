package de.nierhain.powertool.setup;

import de.nierhain.powertool.PowerTool;
import de.nierhain.powertool.data.recipe.NBTRecipe;
import de.nierhain.powertool.items.PowerToolItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static de.nierhain.powertool.PowerTool.MODID;

public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static void init(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SERIALIZERS.register(bus);
    }
    public static final String TAB_NAME = "powertool";
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(POWER_TOOL.get());
        }
    };

    public static final RegistryObject<RecipeSerializer<NBTRecipe>> NBT_RECIPE_SERIALIZER = SERIALIZERS.register("nbt_crafting", () -> NBTRecipe.Serializer.INSTANCE);
    public static final RegistryObject<Item> POWER_TOOL = ITEMS.register("powertool", PowerToolItem::new);
    public static final RegistryObject<Item> UPGRADE_ITEM = registerItem("upgrade");
    public static final RegistryObject<Item> MAGNET_UPGRADE = registerItem("magnet_upgrade");
    public static final RegistryObject<Item> FORTUNE_UPGRADE = registerItem("fortune_upgrade");

    public static RegistryObject<Item> registerItem(String name){
        return ITEMS.register(name, () -> new Item(new Item.Properties().tab(ITEM_GROUP).fireResistant()));
    }
}
