package dk.bison.rpg.core.monster;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.faction.FactionFactory;
import dk.bison.rpg.core.StringUtil;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.ai.AIFactory;
import dk.bison.rpg.core.weapon.Weapon;
import dk.bison.rpg.core.weapon.WeaponFactory;

/**
 * Created by bison on 16-08-2016.
 */
public class Monster implements Combatant {
    public static final String TAG = Monster.class.getSimpleName();
    MonsterTemplate template;
    int initiative;
    int maxHP;
    int HP;
    int level = 1;
    int position = CombatPosition.CENTER;
    int distanceToCurrentTarget;
    Weapon weapon;
    List<Attack> attacks;
    Dice dice = new Dice();
    Faction faction;
    AI ai;
    String name;


    public Monster(MonsterTemplate template, int level, String name) {
        this.template = template;
        this.level = level;
        this.name = name;
        faction = FactionFactory.makeFaction(template.factionTemplate);
        parseAttacks();
        rollHP();
        ai = AIFactory.makeAI(template.aiClass);
        ai.setCombatant(this);
        if(template.weaponTemplate != null)
            weapon = WeaponFactory.makeWeapon(template.weaponTemplate);
    }

    private void rollHP()
    {
        maxHP = 0;
        for(int i = 1; i <= level; i++)
        {
            int roll = dice.roll(template.getHitDice(level));
            maxHP += roll;
        }
        HP = maxHP;
        Log.i(TAG, getName() + " rolled " + HP + " hitpoints");
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
                    attack.type = Attack.MONSTER;
                    attacks.add(attack);
                }
                else {
                    attack.name = name;
                    attack.damage = damage[i].trim();
                    attack.type = Attack.MONSTER;
                    attacks.add(attack);
                }
            }
        }
        Log.i(TAG, "Monster has the following attacks:");
        for(Attack a : attacks)
        {
            Log.i(TAG, "  " + a.name + " - " + a.damage);
        }
    }

    @Override
    public String getName() {
        if(name == null)
            return template.getName();
        return name;
    }

    @Override
    public int getLastInitiativeRolled() {
        return initiative;
    }

    @Override
    public int rollInitiative() {

        initiative = dice.roll("1d6");
        initiative += getDEXBonus();
        return initiative;
    }

    public int getAC()
    {
        return template.AC;
    }

    @Override
    public int getHP() {
        return HP;
    }

    @Override
    public int getMaxHP() {
        return maxHP;
    }

    @Override
    public void decreaseHP(int amount) {
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
    public int getSTRBonus() {
        return 0;
    }

    @Override
    public int getDEXBonus() {
        return 0;
    }

    @Override
    public int getAttackBonus() {
        return template.getAttackBonus(level);
    }

    @Override
    public List<Attack> getAttacks() {
        return attacks;
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public AI getAI() {
        return ai;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void resetHealth() {
        HP = maxHP;
    }

    @Override
    public String getNameWithTemplateName() {
        if(name == null) {
            return template.getName();
        }
        else {
            return String.format(Locale.US, "%s (%s)", name, template.getName());
        }
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
}
