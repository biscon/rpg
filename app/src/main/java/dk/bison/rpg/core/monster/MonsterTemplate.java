package dk.bison.rpg.core.monster;

import java.util.Locale;

/**
 * Created by bison on 16-08-2016.
 */
public class MonsterTemplate {
    String name;
    int AC;
    String noAttacks;
    String damage;
    int XP;
    String weaponTemplate;
    String factionTemplate;
    String aiClass;

    public MonsterTemplate(String name, int AC, String noAttacks, String damage, int XP, String weaponTemplate,
                           String factionTemplate, String aiClass) {
        this.name = name;
        this.AC = AC;
        this.noAttacks = noAttacks;
        this.damage = damage;
        this.XP = XP;
        this.weaponTemplate = weaponTemplate;
        this.factionTemplate = factionTemplate;
        this.aiClass = aiClass;
    }

    public int getAttackBonus(int level)
    {
        if(level >= 0 && level <= 7)
            return level;
        if(level >= 8 && level <= 9)
            return 8;
        if(level >= 10 && level <= 11)
            return 9;
        if(level >= 12 && level <= 13)
            return 10;
        if(level >= 14 && level <= 15)
            return 11;
        if(level >= 16 && level <= 19)
            return 12;
        if(level >= 20 && level <= 23)
            return 13;
        if(level >= 24 && level <= 27)
            return 14;
        if(level >= 28 && level <= 31)
            return 15;
        if(level >= 21)
            return 16;
        return 0;
    }

    public String getHitDice(int level)
    {
        return String.format(Locale.US, "%dd8", level);
    }
}
