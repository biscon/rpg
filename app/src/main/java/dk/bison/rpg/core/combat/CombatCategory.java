package dk.bison.rpg.core.combat;

import java.util.List;

/**
 * Created by bison on 28-09-2016.
 */

public class CombatCategory {
    String name;
    List<CombatText> hit;
    List<CombatText> miss;
    List<CombatText> crit;
    List<CombatText> fail;

    public String getName() {
        return name;
    }

    public List<CombatText> getHit() {
        return hit;
    }

    public List<CombatText> getMiss() {
        return miss;
    }

    public List<CombatText> getCrit() {
        return crit;
    }

    public List<CombatText> getFail() {
        return fail;
    }
}
