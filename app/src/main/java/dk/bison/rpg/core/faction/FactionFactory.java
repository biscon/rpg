package dk.bison.rpg.core.faction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bison on 16-08-2016.
 */
public class FactionFactory {
    static Map<String, FactionTemplate> templates = new HashMap<String, FactionTemplate>();

    public static void init()
    {
        addTemplate("Player", true);
        addTemplate("Wildlife", false);
    }

    private static void addTemplate(String name, boolean player_faction)
    {
        templates.put(name, new FactionTemplate(name, player_faction));
    }

    public static Faction makeFaction(String name)
    {
        if(!templates.containsKey(name))
            return null;
        return new Faction(templates.get(name));
    }
}
