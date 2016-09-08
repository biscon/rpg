package dk.bison.rpg.core.character;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bison on 07-08-2016.
 */
public class FighterClass extends CharacterClass {
    static String name = "Fighter";

    static List<LevelRow> levelTable = new ArrayList<LevelRow>() {{
        add(new LevelRow(1,0,1999,"1d8"));
        add(new LevelRow(2,2000,3999,"2d8"));
        add(new LevelRow(3,4000,7999,"3d8"));
        add(new LevelRow(4,8000,15999,"4d8"));
        add(new LevelRow(5,16000,31999,"5d8"));
        add(new LevelRow(6,32000,63999,"6d8"));
        add(new LevelRow(7,64000, 119999,"7d8"));
        add(new LevelRow(8,120000,239999,"8d8"));
        add(new LevelRow(9,240000,359999,"9d8"));
        add(new LevelRow(10,360000,479999,"9d8+2"));
        add(new LevelRow(11,480000,599999,"9d8+4"));
        add(new LevelRow(12,600000,719999,"9d8+6"));
        add(new LevelRow(13,720000,839999,"9d8+8"));
        add(new LevelRow(14,840000,959999,"9d8+10"));
        add(new LevelRow(15,960000,1079999,"9d8+12"));
        add(new LevelRow(16,1080000,1199999,"9d8+14"));
        add(new LevelRow(17,1200000,1319999,"9d8+16"));
        add(new LevelRow(18,1320000,1439999,"9d8+18"));
        add(new LevelRow(19,1440000,1559999,"9d8+20"));
        add(new LevelRow(20,1560000,21559999,"9d8+22"));
    }};


    @Override
    public String getClassName()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public int calcLevel(int xp) {
        for(LevelRow r : levelTable)
        {
            if(xp >= r.minXP && xp <= r.maxXP)
                return r.level;
        }
        return 0;
    }

    @Override
    public String getHitDice(int level) {
        if(level > 10)
            level = 10;
        for(LevelRow r : levelTable)
        {
            if(r.level == level)
                return r.hitDice;
        }
        return null;
    }

    @Override
    public int getAttackBonus(int level) {
        int bonus = 0;
        if(level == 1)
            bonus = 1;
        else if(level >=2 && level <=3)
            bonus = 2;
        else if(level == 4)
            bonus = 3;
        else if(level >= 5 && level <= 6)
            bonus = 4;
        else if(level == 7)
            bonus = 5;
        else if(level >= 8 && level <= 10)
            bonus = 6;
        else if(level >= 11 && level <= 12)
            bonus = 7;
        else if(level >= 13 && level <= 15)
            bonus = 8;
        else if(level >= 16 && level <= 17)
            bonus = 9;
        else if(level >= 18 && level <= 20)
            bonus = 10;
        return bonus;
    }

    // capped to max level
    @Override
    public int getXPForLevel(int level) {
        for(LevelRow r : levelTable)
        {
            if(r.level == level)
                return r.minXP;
        }
        return levelTable.get(levelTable.size()-1).minXP;
    }

    public String getName() {
        return name;
    }
}
