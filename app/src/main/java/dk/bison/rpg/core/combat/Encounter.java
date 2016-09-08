package dk.bison.rpg.core.combat;

import java.util.List;

/**
 * Created by bison on 18-08-2016.
 */
public interface Encounter {
    List<Combatant> getCombatants();
    void attack(Combatant c, Combatant opponent);
}
