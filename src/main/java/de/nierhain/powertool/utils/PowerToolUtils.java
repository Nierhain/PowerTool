package de.nierhain.powertool.utils;

import de.nierhain.powertool.setup.NBTTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class PowerToolUtils {

    private static boolean tagExists(CompoundTag tag, String key, TagType type){
        if(!tag.contains(key)){
            if(type == TagType.Integer)tag.putInt(key, 0);
            if(type == TagType.String)tag.putString(key, "");
            if(type == TagType.Boolean)tag.putBoolean(key, false);
        }
        return true;
    }

    private static boolean checkTag(String key, ItemStack stack, TagType type){
        CompoundTag tag = stack.getOrCreateTag();
        if(tagExists(tag, key, type)) {
            return tag.getBoolean(key);
        }
        return false;
    }

    public static PowerToolMode getMode(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        if(tagExists(tag, NBTTags.EXTENDED, TagType.Integer)){
            if(tag.getInt(NBTTags.EXTENDED) == 1){
                return PowerToolMode.TRIPLE;
            }
            if(tag.getInt(NBTTags.EXTENDED) == 2){
                return PowerToolMode.QUINTUPLE;
            }
        }
        return PowerToolMode.SINGLE;
    }

    public static boolean isUpgraded(ItemStack stack){
        return checkTag(NBTTags.UPGRADED, stack, TagType.Boolean);
    }


    public static boolean isToolMagnetic(ItemStack stack){
        return checkTag(NBTTags.MAGNETIC, stack, TagType.Boolean);
    }

    public static boolean isToolLucky(ItemStack stack){
        return checkTag(NBTTags.LUCKY, stack, TagType.Boolean);
    }

    static enum TagType{
        String,
        Integer,
        Boolean,
    }
}
