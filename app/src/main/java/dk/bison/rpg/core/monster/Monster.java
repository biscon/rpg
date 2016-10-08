package dk.bison.rpg.core.monster;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.BaseCombatant;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.faction.FactionFactory;
import dk.bison.rpg.core.StringUtil;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.ai.AIFactory;
import dk.bison.rpg.core.grammar.Grammar;
import dk.bison.rpg.core.grammar.GrammarFactory;
import dk.bison.rpg.core.weapon.Weapon;
import dk.bison.rpg.core.weapon.WeaponFactory;

/**
 * Created by bison on 16-08-2016.
 */
public class Monster extends BaseCombatant {
    public static final String TAG = Monster.class.getSimpleName();
    MonsterTemplate template;
    Weapon weapon;
    List<Attack> attacks;
    Dice dice = new Dice();


    public Monster(MonsterTemplate template, int level, String name) {
        this.template = template;
        setLevel(level);
        setName(name);
        setFaction(FactionFactory.makeFaction(template.factionTemplate));
        parseAttacks();
        rollHP();
        setGrammar(GrammarFactory.make(template.grammarClass));
        AI ai = AIFactory.makeAI(template.aiClass);
        setAI(ai);
        ai.setCombatant(this);
        if(template.weaponTemplate != null)
            weapon = WeaponFactory.makeWeapon(template.weaponTemplate);
    }

    private void rollHP()
    {
        int max_hp = 0;
        for(int i = 1; i <= getLevel(); i++)
        {
            int roll = dice.roll(template.getHitDice(i));
            max_hp += roll;
        }
        setMaxHP(max_hp);
        setHP(max_hp);
        Log.i(TAG, getName() + " rolled " + getHP() + " hitpoints");
    }

    private void parseAttacks()
    {
        attacks = new ArrayList<>();
        String[] no_attacks = template.noAttacks.split("/");
        String[] damage = template.damage.split("/");
        for(int i = 0; i < no_attacks.length; i++)
        {
            int times = StringUtil.parseFirstInteger(no_attacks[i]);
            String name = StringUtil.parseFirstWord(no_attacks[i]);
            for(int j = 0; j < times; j++) {
                Attack attack = new Attack();
                if(name.toLowerCase().contentEquals("weapon") && weapon != null)
                {
                    attack.name = weapon.getName();
                    attack.damage = weapon.getDamageDice();
                    attack.range = weapon.getRange();
                    attack.weapon = weapon;
                    attack.combatTextCategory = weapon.getTemplate().getCategory();
                    if(weapon.isRanged())
                        attack.isRanged = true;
                    attack.type = Attack.MONSTER;
                    attacks.add(attack);
                }
                else {
                    attack.name = name;
                    attack.damage = damage[i].trim();
                    attack.type = Attack.MONSTER;
                    attack.isRanged = false;
                    attack.combatTextCategory = name;
                    attacks.add(attack);
                }
            }
        }
        Log.i(TAG, "Monster has the following attacks:");
        for(Attack a : attacks)
        {
            Log.e(TAG, "  " + a.name + " - " + a.damage + " (txt cat=" + a.combatTextCategory + ")");
        }
    }

    @Override
    public String getName() {
        if(super.getName() == null)
            return template.getName();
        return super.getName();
    }

    @Override
    public int getAC()
    {
        return template.AC;
    }

    @Override
    public boolean awardXp(int xp)
    {
        return false;
    }

    @Override
    public int getSTRBonus() {
        return 0;
    }

    @Override
    public int getDEXBonus() {
        return 0;
    }

    @Override
    public int getAttackBonus() {
        return template.getAttackBonus(getLevel());
    }

    @Override
    public List<Attack> getAttacks() {
        return attacks;
    }

    @Override
    public List<Attack> getRangedAttacks(int distance)
    {
        List<Attack> ranged_attacks = new ArrayList<>();
        for(Attack attack : attacks)
        {
            if(attack.isRanged) {
                if(attack.range >= distance)
                    ranged_attacks.add(attack);
            }
        }
        return ranged_attacks;
    }

    @Override
    public List<Attack> getMeleeAttacks()
    {
        List<Attack> melee_attacks = new ArrayList<>();
        for(Attack attack : attacks)
        {
            if(!attack.isRanged)
                melee_attacks.add(attack);
        }
        return melee_attacks;
    }

    @Override
    public String getNameWithTemplateName() {
        if(super.getName() == null) {
            return template.getName();
        }
        else {
            return String.format(Locale.US, "%s (%s)", super.getName(), template.getName());
        }
    }

    @Override
    public int getXPAward() {
        return template.XP;
    }
}
