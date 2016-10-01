package dk.bison.rpg.ui.encounter.enemy_status;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 01-10-2016.
 */

public class EnemySelectedEvent implements MvpEvent {
    public Combatant combatant;

    public EnemySelectedEvent(Combatant combatant) {
        this.combatant = combatant;
    }
}
