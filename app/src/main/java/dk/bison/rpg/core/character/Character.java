package dk.bison.rpg.core.character;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.ai.AIFactory;
import dk.bison.rpg.core.armor.Armor;
import dk.bison.rpg.core.armor.ArmorFactory;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.BaseCombatant;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.faction.FactionFactory;
import dk.bison.rpg.core.grammar.Grammar;
import dk.bison.rpg.core.grammar.GrammarFactory;
import dk.bison.rpg.core.weapon.Weapon;
import dk.bison.rpg.core.weapon.WeaponFactory;
import dk.bison.rpg.core.weapon.WeaponTemplate;

/**
 * Created by bison on 07-08-2016.
 */
public class Character extends BaseCombatant {
    public static final String TAG = Character.class.getSimpleName();

    CharacterStats stats;
    CharacterClass charClass;
    int money;

    List<Attack> attacks;


    private Armor armor;
    private Armor shield;
    private Weapon mainHandWeapon;
    private Weapon offHandWeapon;

    public Character() {
        setGrammar(GrammarFactory.make("MaleGrammar"));
        setFaction(FactionFactory.makeFaction("Player"));
        //AI ai = AIFactory.makeAI("PlayerControlAI");
        AI ai = AIFactory.makeAI("ClosestEnemyAI");
        setAI(ai);
        ai.setCombatant(this);
        attacks = new ArrayList<>();
        charClass = CharacterClassFactory.makeClass("FighterClass");
        stats = new CharacterStats();
        rerollStats();
        //equipMainHand(WeaponFactory.makeWeapon("Longsword"));
        //equipOffHand(WeaponFactory.makeWeapon("Longsword"));
    }

    public void rerollStats()
    {
        setLevel(1);
        setXP(0);
        stats.roll();
        rollStartingMaxHP();
        rollStartingMoney();
    }

    private void buildWeaponAttacks()
    {
        attacks.clear();
        /*
            If unarmed, character only has a weak fist attack
         */
        if(offHandWeapon == null && mainHandWeapon == null) {
            Attack a = new Attack();
            a.name = "fists";
            a.damage = "1d3";
            a.type = Attack.MAIN_HAND;
            a.isRanged = false;
            a.range = 0;
            a.combatTextCategory = "unarmed";
            attacks.add(a);
            return;
        }

        // if we have a main hand add attack
        if(mainHandWeapon != null) {
            Attack a = new Attack();
            a.name = mainHandWeapon.getName();
            a.weapon = mainHandWeapon;
            a.damage = mainHandWeapon.getDamageDice();
            a.combatTextCategory = mainHandWeapon.getTemplate().getCategory();
            if(mainHandWeapon.getSize() == WeaponTemplate.SIZE_L)
                a.type = Attack.TWO_HAND;
            else
                a.type = Attack.MAIN_HAND;
            if(mainHandWeapon.isRanged()) {
                a.isRanged = true;
                a.range = mainHandWeapon.getRange();
            }
            else
                a.isRanged = false;
            attacks.add(a);
        }

        // same with offhand
        if(offHandWeapon != null) {
            Attack a = new Attack();
            a.name = offHandWeapon.getName();
            a.weapon = offHandWeapon;
            a.damage = offHandWeapon.getDamageDice();
            a.type = Attack.OFF_HAND;
            a.combatTextCategory = offHandWeapon.getTemplate().getCategory();
            if(offHandWeapon.isRanged()) {
                a.isRanged = true;
                a.range = offHandWeapon.getRange();
            }
            else
                a.isRanged = false;
            attacks.add(a);
        }
    }

    private void rollStartingMaxHP()
    {
        Dice d = new Dice();
        String hit_dice = charClass.getHitDice(getLevel());
        int con_bonus = CharacterStats.calcStatBonusPenalty(stats.getCON());
        int max_hp = d.roll(hit_dice);
        max_hp += con_bonus;
        if(max_hp < 1)
            max_hp = 1;
        max_hp += 8;
        setMaxHP(max_hp);
        setHP(max_hp);
    }

    @Override
    public boolean awardXp(int xp)
    {
        int n_xp = getXP();
        n_xp += xp;
        int lvl = charClass.calcLevel(n_xp);
        setXP(n_xp);
        if(lvl > getLevel())
        {
            setLevel(getLevel()+1);
            rollLevelUpMaxHPBonus();
            return true;
        }
        return false;
    }

    @Override
    public int getXPAward() {
        return 0;
    }

    public void giveLevel()
    {
        setLevel(getLevel()+1);
        setXP(charClass.getXPForLevel(getLevel()));
        Log.i(TAG, getName() + " is now level " + getLevel() + " (XP = " + getXP() + ")");
        rollLevelUpMaxHPBonus();
        Log.i(TAG, getName() + " has " + getHP() + " hitpoints");
    }

    private void rollLevelUpMaxHPBonus()
    {
        Dice d = new Dice();
        String hit_dice = charClass.getHitDice(getLevel());
        int con_bonus = CharacterStats.calcStatBonusPenalty(stats.getCON());
        int bonus = d.roll(hit_dice);
        bonus+= con_bonus;
        if(bonus < 1)
            bonus = 1;
        int max_hp = getMaxHP() + bonus;
        setMaxHP(max_hp);
        Log.i(TAG, getName() + " gains " + bonus + " points to maximum hitpoints");
        setHP(max_hp);
    }

    private void rollStartingMoney()
    {
        Dice d = new Dice();
        money = 10 * d.roll("3d6",0);
    }

    @Override
    public int getAttackBonus()
    {
        return charClass.getAttackBonus(getLevel());
    }

    @Override
    public List<Attack> getAttacks() {
        if(attacks.isEmpty())
            buildWeaponAttacks();
        return attacks;
    }

    @Override
    public List<Attack> getRangedAttacks(int distance)
    {
        getAttacks();
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
        getAttacks();
        List<Attack> melee_attacks = new ArrayList<>();
        for(Attack attack : attacks)
        {
            if(!attack.isRanged)
                melee_attacks.add(attack);
        }
        return melee_attacks;
    }



    @Override
    public int getAC()
    {
        int ac = CharacterClass.BASE_AC + getDEXBonus();
        if(armor != null)
            ac = armor.getAC() + getDEXBonus();
        if(shield != null)
            ac += shield.getAC();
        return ac;
    }

    public int getDEXBonus()
    {
        return CharacterStats.calcStatBonusPenalty(stats.getDEX());
    }
    public int getSTRBonus()
    {
        return CharacterStats.calcStatBonusPenalty(stats.getSTR());
    }



    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public void equipMainHand(Weapon weapon) {
        this.mainHandWeapon = weapon;
        buildWeaponAttacks();
    }

    public void equipOffHand(Weapon weapon) {
        this.offHandWeapon = weapon;
        buildWeaponAttacks();
    }

    public Weapon getMainHandWeapon() {
        return mainHandWeapon;
    }

    public Weapon getOffHandWeapon() {
        return offHandWeapon;
    }

    public void setShield(Armor shield) {
        this.shield = shield;
    }

    public Armor getShield() {
        return shield;
    }



    public CharacterStats getStats() {
        return stats;
    }

    public CharacterClass getCharClass() {
        return charClass;
    }


    @Override
    public String getNameWithTemplateName() {
        return String.format(Locale.US, "%s (%s)", getName(), charClass.getName());
    }

    public int getMoney() {
        return money;
    }

    @Override
    public String toString() {
        return "Character{" +
                "name='" + getName() + '\'' +
                ", stats=" + stats +
                ", charClass=" + charClass.getName() +
                ", maxHP=" + getMaxHP() +
                ", HP=" + getHP() +
                ", level=" + getLevel() +
                ", XP=" + getXP() +
                ", money=" + money +
                ", atk bonus=" + getAttackBonus() +
                ", AC=" + getAC() +
                '}';
    }

    public JSONObject toJson() throws JSONException {
        JSONObject chr = new JSONObject();
        chr.put("name", getName());
        String str_g = ""+getGender();
        chr.put("gender", str_g);
        chr.put("maxHP", getMaxHP());
        chr.put("HP", getHP());
        chr.put("level", getLevel());
        chr.put("XP", getXP());
        chr.put("money", money);
        chr.put("charClass", charClass.getClassName());
        chr.put("stats", stats.toJson());
        if(armor != null)
            chr.put("armor", armor.getTemplate().getName());
        if(shield != null)
            chr.put("shield", shield.getTemplate().getName());
        if(mainHandWeapon != null)
            chr.put("mainHandWeapon", mainHandWeapon.getTemplate().getName());
        if(offHandWeapon != null)
            chr.put("offHandWeapon", offHandWeapon.getTemplate().getName());
        return chr;
    }

    public static Character fromJson(JSONObject chr) throws JSONException {
        Character c = new Character();
        c.setName(chr.getString("name"));
        char g = chr.getString("gender").charAt(0);
        Log.e(TAG, "Read gender as " + g + " from json");
        c.setGender(g);
        c.setMaxHP(chr.getInt("maxHP"));
        c.setHP(chr.getInt("HP"));
        c.setLevel(chr.getInt("level"));
        c.setXP(chr.getInt("XP"));
        c.money = chr.getInt("money");
        c.stats = CharacterStats.fromJson(chr.getJSONObject("stats"));
        String char_class = chr.getString("charClass");
        c.charClass = CharacterClassFactory.makeClass(char_class);
        if(chr.has("armor"))
        {
            c.armor = ArmorFactory.makeArmor(chr.getString("armor"));
        }
        if(chr.has("shield"))
        {
            c.shield = ArmorFactory.makeArmor(chr.getString("shield"));
        }
        if(chr.has("mainHandWeapon"))
        {
            c.mainHandWeapon = WeaponFactory.makeWeapon(chr.getString("mainHandWeapon"));
        }
        if(chr.has("offHandWeapon"))
        {
            c.offHandWeapon = WeaponFactory.makeWeapon(chr.getString("offHandWeapon"));
        }
        return c;
    }
}
