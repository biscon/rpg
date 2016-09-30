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
        addTemplate("Wolf", 13, "1 bite / 1 scratch", "1d6 / 1d4", 75, null, "Wildlife", "ClosestEnemyAI", "CreatureGrammar");
        addTemplate("Swearwolf", 13, "2 bite", "1d6", 120, null, "Wildlife", "ClosestEnemyAI", "CreatureGrammar");
        addTemplate("Dire Wolf", 14, "1 bite / 2 scratch", "2d4 / 1d6", 240, null, "Wildlife", "SearchDestroyAI", "CreatureGrammar");
    }

    private static void addTemplate(String name, int AC, String noAttacks, String damage, int XP, String weaponTemplate,
                                    String factionTemplate, String aiClass, String grammarClass)
    {
        templates.put(name, new MonsterTemplate(name, AC, noAttacks, damage, XP, weaponTemplate, factionTemplate, aiClass, grammarClass));
    }

    public static Monster makeMonster(String name, String template_name, int level)
    {
        if(!templates.containsKey(template_name))
            return null;
        Monster monster = new Monster(templates.get(template_name), level, name);

        return monster;
    }
}
