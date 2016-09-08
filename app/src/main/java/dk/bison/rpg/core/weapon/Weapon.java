package dk.bison.rpg.core.weapon;

/**
 * Created by bison on 16-08-2016.
 */
public class Weapon {
    WeaponTemplate template;

    public Weapon(WeaponTemplate template) {
        this.template = template;
    }
    public String getDamageDice()
    {
        return template.damage;
    }

    public String getName()
    {
        return template.name;
    }

    public int getSize()
    {
        return template.size;
    }

    public boolean isRanged()
    {
        if(template.range > 0)
            return true;
        return false;
    }

    public WeaponTemplate getTemplate() {
        return template;
    }
}
