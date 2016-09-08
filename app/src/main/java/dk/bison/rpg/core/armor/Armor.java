package dk.bison.rpg.core.armor;

/**
 * Created by bison on 16-08-2016.
 */
public class Armor {
    ArmorTemplate template;

    public Armor(ArmorTemplate template) {
        this.template = template;
    }

    public int getAC()
    {
        return template.AC;
    }

    public String getName()
    {
        return template.name;
    }

    public ArmorTemplate getTemplate() {
        return template;
    }
}
