package dk.bison.rpg.core.faction;

/**
 * Created by bison on 18-08-2016.
 */
public class Faction {
    private FactionTemplate template;

    public Faction(FactionTemplate template) {
        this.template = template;
    }

    public boolean sameAs(Faction other_faction)
    {
        if(template == other_faction.template)
            return true;
        else
            return false;
    }

    public String getName()
    {
        return template.name;
    }
}
