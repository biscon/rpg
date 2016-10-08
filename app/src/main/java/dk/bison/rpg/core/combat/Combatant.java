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
    void setName(String name);
    int getLastInitiativeRolled();
    int rollInitiative();
    int getAC();
    int getHP();
    void setHP(int hp);
    int getMaxHP();
    void setMaxHP(int max_hp);
    void decreaseHP(int amount);
    void increaseHP(int amount);
    boolean isDead();
    int getSTRBonus();
    int getDEXBonus();
    int getAttackBonus();
    //String getDamageDice();
    List<Attack> getAttacks();
    Faction getFaction();
    void setFaction(Faction faction);
    AI getAI();
    void setAI(AI ai);
    int getLevel();
    void setLevel(int level);
    void resetHealth();
    String getNameWithTemplateName();
    int getPosition();
    void setPosition(int pos);
    void setDistanceToCurrentTarget(int distance);
    int getDistanceToCurrentTarget();
    List<Attack> getRangedAttacks(int distance);
    List<Attack> getMeleeAttacks();
    Grammar getGrammar();
    void setGrammar(Grammar grammar);
    char getGender();
    void setGender(char gender);
    boolean awardXp(int xp);
    void setXP(int xp);
    int getXP();
}
