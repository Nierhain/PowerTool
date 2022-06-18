package de.nierhain.powertool.data.recipe;

import de.nierhain.powertool.setup.NBTTags;

import java.util.HashMap;
import java.util.Map;

public class DefaultTags {
    private static HashMap<String, Boolean> DefaultBooleans = new HashMap<>();
    private static HashMap<String, String> DefaultStrings = new HashMap<>();
    private static HashMap<String, Integer> DefaultIntegers = new HashMap<>();

    static {
        //Booleans
        DefaultBooleans.put(NBTTags.LUCKY, false);
        DefaultBooleans.put(NBTTags.MAGNETIC, false);
        DefaultBooleans.put(NBTTags.UPGRADED, false);

        //Strings

        //Integer
        DefaultIntegers.put(NBTTags.EXTENDED, 0);
    }

    public static boolean isBoolean(String key){
        return DefaultBooleans.containsKey(key);
    }

    public static boolean getDefaultBoolean(String key){
        return DefaultBooleans.get(key);
    }

    public static boolean isInt(String key){
        return DefaultIntegers.containsKey(key);
    }

    public static int getDefaultInt(String key){
        return DefaultIntegers.get(key);
    }

    public static boolean isString(String key){
        return DefaultStrings.containsKey(key);
    }

    public static String getDefaultString(String key){
        return DefaultStrings.get(key);
    }
}
