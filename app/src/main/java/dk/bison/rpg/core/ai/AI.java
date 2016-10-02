package dk.bison.rpg.core.ai;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;

/**
 * Created by bison on 18-08-2016.
 */
public abstract class AI {
    protected Combatant combatant;

    public Combatant getCombatant() {
        return combatant;
    }

    public void setCombatant(Combatant combatant) {
        this.combatant = combatant;
    }

    public abstract void performAction(Encounter encounter);
    public abstract void performMove(int distance);
    public abstract void attack(Combatant c, Combatant opponent, Attack attack);

    public abstract void reset();
    public abstract int getSpeed();
}
