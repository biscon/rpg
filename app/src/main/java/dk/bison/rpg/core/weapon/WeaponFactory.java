package dk.bison.rpg.core.weapon;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.bison.rpg.core.armor.ArmorTemplate;

/**
 * Created by bison on 14-08-2016.
 */
public class WeaponFactory {
    static Map<String, WeaponTemplate> templates = new HashMap<String, WeaponTemplate>();

    public static void init()
    {
        addTemplate("Dagger", 2, WeaponTemplate.SIZE_S, 1, "1D4", 0);
        addTemplate("Battle Axe", 7, WeaponTemplate.SIZE_M, 7, "1D8", 0);
        addTemplate("Shortsword", 6, WeaponTemplate.SIZE_S, 3, "1D6", 0);
        addTemplate("Longsword", 10, WeaponTemplate.SIZE_M, 4, "1D8", 0);
        addTemplate("Two-handed Sword", 18, WeaponTemplate.SIZE_L, 10, "1D10", 0);
        addTemplate("Shortbow", 25, WeaponTemplate.SIZE_M, 2, "1D6", 4);
        addTemplate("Longbow", 60, WeaponTemplate.SIZE_L, 3, "1D8", 6);
    }

    private static void addTemplate(String name, int cost, int size, int weight, String damage, int range)
    {
        templates.put(name, new WeaponTemplate(name, cost, size, weight, damage, range));
    }

    public static Weapon makeWeapon(String name)
    {
        if(!templates.containsKey(name))
            return null;
        return new Weapon(templates.get(name));
    }

    public static List<WeaponTemplate> getTemplates()
    {
        List<WeaponTemplate> list = new ArrayList<>();
        list.addAll(templates.values());
        //Log.d(TAG, "Added " + list.size() + " templates to list");
        return list;
    }
}
