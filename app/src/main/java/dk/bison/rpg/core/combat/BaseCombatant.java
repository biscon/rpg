package dk.bison.rpg.core.combat;

import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.grammar.Grammar;
import dk.bison.rpg.core.grammar.GrammarFactory;

/**
 * Created by bison on 08-10-2016.
 */

public abstract class BaseCombatant implements Combatant {
    public static final String TAG = BaseCombatant.class.getSimpleName();
    String name = "";
    int maxHP;
    int HP;
    int level = 1;
    int XP = 0;
    Faction faction;
    AI ai;

    int initiative;
    int position = CombatPosition.CENTER;
    int lane = 0;
    int distanceToCurrentTarget;
    Grammar grammar;
    private char gender = Gender.MALE;

    @Override
    public void setGrammar(Grammar grammar)
    {
        this.grammar = grammar;
    }

    @Override
    public void setFaction(Faction faction)
    {
        this.faction = faction;
    }

    @Override
    public void setAI(AI ai)
    {
        this.ai = ai;
    }

    @Override
    public void setLevel(int level)
    {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setXP(int xp)
    {
        XP = xp;
    }

    @Override
    public int getXP()
    {
        return XP;
    }


    @Override
    public void setHP(int hp)
    {
        this.HP = hp;
    }

    @Override
    public void setMaxHP(int max_hp)
    {
        maxHP = max_hp;
    }

    @Override
    public int getHP()
    {
        return HP;
    }

    @Override
    public int getMaxHP() {
        return maxHP;
    }

    @Override
    public Grammar getGrammar() {
        return grammar;
    }

    @Override
    public char getGender() {
        return gender;
    }

    @Override
    public void setGender(char gender) {
        this.gender = gender;
        switch(gender)
        {
            case Gender.MALE:
                grammar = GrammarFactory.make("MaleGrammar");
                break;
            case Gender.FEMALE:
                grammar = GrammarFactory.make("FemaleGrammar");
                break;
            case Gender.NEUTRAL:
                grammar = GrammarFactory.make("CreatureGrammar");
                break;
        }
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public AI getAI() {
        return ai;
    }

    public void decreaseHP(int amount)
    {
        HP -= amount;
        if(HP < 0)
            HP = 0;
    }

    @Override
    public void increaseHP(int amount) {
        HP += amount;
    }

    @Override
    public boolean isDead()
    {
        if(HP < 1)
            return true;
        else
            return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getLastInitiativeRolled() {
        return initiative;
    }

    public int rollInitiative()
    {
        Dice d = new Dice();
        initiative = d.roll("1d6",0);
        initiative += getDEXBonus();
        return initiative;
    }

    @Override
    public void resetHealth() {
        HP = maxHP;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int pos) {
        position = pos;
    }

    @Override
    public void setDistanceToCurrentTarget(int distance) {
        distanceToCurrentTarget = distance;
    }

    @Override
    public int getDistanceToCurrentTarget() {
        return distanceToCurrentTarget;
    }

    @Override
    public int getLane() {
        return lane;
    }

    @Override
    public void setLane(int lane) {
        this.lane = lane;
    }
}
