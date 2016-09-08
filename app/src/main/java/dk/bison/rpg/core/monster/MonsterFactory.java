package dk.bison.rpg.core.monster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bison on 16-08-2016.
 */
public class MonsterFactory {
    static Map<String, MonsterTemplate> templates = new HashMap<String, MonsterTemplate>();

    public static void init()
    {
        addTemplate("Wolf", 13, "1 bite / 1 scratch", "1d6 / 1d4", 75, null, "Wildlife", "SearchDestroyAI");
        addTemplate("Dire Wolf", 14, "1 bite / 1 scratch", "2d4 / 1d6", 240, null, "Wildlife", "SearchDestroyAI");
    }

    private static void addTemplate(String name, int AC, String noAttacks, String damage, int XP, String weaponTemplate,
                                    String factionTemplate, String aiClass)
    {
        templates.put(name, new MonsterTemplate(name, AC, noAttacks, damage, XP, weaponTemplate, factionTemplate, aiClass));
    }

    public static Monster makeMonster(String name, int level)
    {
        if(!templates.containsKey(name))
            return null;
        Monster monster = new Monster(templates.get(name), level);

        return monster;
    }
}
