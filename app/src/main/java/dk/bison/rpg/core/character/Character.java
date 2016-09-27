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
public class Character implements Combatant {
    public static final String TAG = Character.class.getSimpleName();

    String name = "";
    CharacterStats stats;
    CharacterClass charClass;
    int maxHP;
    int HP;
    int level = 1;
    int XP = 0;
    int money;
    int initiative;
    int position = CombatPosition.CENTER;
    int distanceToCurrentTarget;
    List<Attack> attacks;
    Faction faction;
    AI ai;

    private Armor armor;
    private Armor shield;
    private Weapon mainHandWeapon;
    private Weapon offHandWeapon;
    Grammar grammar;

    private char gender = Gender.MALE;

    public Character() {
        grammar = GrammarFactory.make("MaleGrammar");
        faction = FactionFactory.makeFaction("Player");
        ai = AIFactory.makeAI("ClosestEnemyAI");
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
        level = 1;
        XP = 0;
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
            attacks.add(a);
            return;
        }

        // if we have a main hand add attack
        if(mainHandWeapon != null) {
            Attack a = new Attack();
            a.name = mainHandWeapon.getName();
            a.damage = mainHandWeapon.getDamageDice();
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
            a.damage = offHandWeapon.getDamageDice();
            a.type = Attack.OFF_HAND;
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
        String hit_dice = charClass.getHitDice(level);
        int con_bonus = CharacterStats.calcStatBonusPenalty(stats.getCON());
        maxHP = d.roll(hit_dice);
        maxHP += con_bonus;
        if(maxHP < 1)
            maxHP = 1;
        maxHP += 8;
        HP = maxHP;
    }

    public void giveLevel()
    {
        level++;
        XP = charClass.getXPForLevel(level);
        Log.i(TAG, name + " is now level " + level + " (XP = " + XP + ")");
        rollLevelUpMaxHPBonus();
        Log.i(TAG, name + " has " + HP + " hitpoints");
    }

    private void rollLevelUpMaxHPBonus()
    {
        Dice d = new Dice();
        String hit_dice = charClass.getHitDice(level);
        int con_bonus = CharacterStats.calcStatBonusPenalty(stats.getCON());
        int bonus = d.roll(hit_dice);
        bonus+= con_bonus;
        if(bonus < 1)
            bonus = 1;
        maxHP += bonus;
        Log.i(TAG, name + " gains " + bonus + " points to maximum hitpoints");
        HP = maxHP;
    }

    private void rollStartingMoney()
    {
        Dice d = new Dice();
        money = 10 * d.roll("3d6",0);
    }

    @Override
    public int getAttackBonus()
    {
        return charClass.getAttackBonus(level);
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

    public int getHP()
    {
        return HP;
    }

    @Override
    public int getMaxHP() {
        return maxHP;
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

    public void setName(String name) {
        this.name = name;
    }

    public CharacterStats getStats() {
        return stats;
    }

    public CharacterClass getCharClass() {
        return charClass;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void resetHealth() {
        HP = maxHP;
    }

    @Override
    public String getNameWithTemplateName() {
        return String.format(Locale.US, "%s (%s)", name, charClass.getName());
    }

    public int getXP() {
        return XP;
    }

    public int getMoney() {
        return money;
    }

    @Override
    public String toString() {
        return "Character{" +
                "name='" + name + '\'' +
                ", stats=" + stats +
                ", charClass=" + charClass.getName() +
                ", maxHP=" + maxHP +
                ", HP=" + HP +
                ", level=" + level +
                ", XP=" + XP +
                ", money=" + money +
                ", atk bonus=" + getAttackBonus() +
                ", AC=" + getAC() +
                '}';
    }

    public JSONObject toJson() throws JSONException {
        JSONObject chr = new JSONObject();
        chr.put("name", name);
        chr.put("maxHP", maxHP);
        chr.put("HP", HP);
        chr.put("level", level);
        chr.put("XP", XP);
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
        c.name = chr.getString("name");
        c.maxHP = chr.getInt("maxHP");
        c.HP = chr.getInt("HP");
        c.level = chr.getInt("level");
        c.XP = chr.getInt("XP");
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
