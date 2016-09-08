package dk.bison.rpg.core.armor;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bison on 16-08-2016.
 */
public class ArmorFactory {
    public static final String TAG = ArmorFactory.class.getSimpleName();
    static Map<String, ArmorTemplate> templates = new HashMap<String, ArmorTemplate>();

    public static void init()
    {
        // Name, cost, weight, ArmorClass, ArmorType
        addTemplate("Leather Armor", 20, 15, 13, ArmorTemplate.CHEST);
        addTemplate("Chain Mail", 60, 40, 15, ArmorTemplate.CHEST);
        addTemplate("Plate Mail", 300, 50, 17, ArmorTemplate.CHEST);
        addTemplate("Shield", 7, 5, 1, ArmorTemplate.SHIELD);
    }

    private static void addTemplate(String name, int cost, int weight, int AC, int type)
    {
        templates.put(name, new ArmorTemplate(name, cost, weight, AC, type));
    }

    public static Armor makeArmor(String name)
    {
        if(!templates.containsKey(name))
            return null;
        return new Armor(templates.get(name));
    }

    public static List<ArmorTemplate> getTemplates()
    {
        List<ArmorTemplate> list = new ArrayList<>();
        list.addAll(templates.values());
        Log.d(TAG, "Added " + list.size() + " templates to list");
        return list;
    }
}
