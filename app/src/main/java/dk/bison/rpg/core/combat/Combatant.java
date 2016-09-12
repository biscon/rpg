package dk.bison.rpg.core.combat;

import java.util.List;

import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.faction.Faction;

/**
 * Created by bison on 17-08-2016.
 */
public interface Combatant {
    String getName();
    int getLastInitiativeRolled();
    int rollInitiative();
    int getAC();
    int getHP();
    void decreaseHP(int amount);
    void increaseHP(int amount);
    boolean isDead();
    int getSTRBonus();
    int getDEXBonus();
    int getAttackBonus();
    //String getDamageDice();
    List<Attack> getAttacks();
    Faction getFaction();
    AI getAI();
    int getLevel();
}
