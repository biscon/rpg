package dk.bison.rpg.core.combat;

import java.util.List;

import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.grammar.Grammar;

/**
 * Created by bison on 17-08-2016.
 */
public interface Combatant {
    String getName();
    int getLastInitiativeRolled();
    int rollInitiative();
    int getAC();
    int getHP();
    int getMaxHP();
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
    void resetHealth();
    String getNameWithTemplateName();
    int getPosition();
    void setPosition(int pos);
    void setDistanceToCurrentTarget(int distance);
    int getDistanceToCurrentTarget();
    List<Attack> getRangedAttacks(int distance);
    List<Attack> getMeleeAttacks();
    Grammar getGrammar();
    char getGender();
    void setGender(char gender);
}
