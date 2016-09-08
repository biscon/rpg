package dk.bison.rpg.core.character;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import dk.bison.rpg.core.Dice;

/**
 * Created by bison on 07-08-2016.
 */
public class CharacterStats {
    public static final String TAG = CharacterStats.class.getSimpleName();
    int STR;
    int INT;
    int WIS;
    int DEX;
    int CON;
    int CHA;

    public void roll()
    {
        roll4D6DropLowest();
    }

    /*
        strict rule
     */
    public void roll3D6()
    {
        Dice d = new Dice();
        STR = d.roll("3d6", 0);
        INT = d.roll("3d6", 0);
        WIS = d.roll("3d6", 0);
        DEX = d.roll("3d6", 0);
        CON = d.roll("3d6", 0);
        CHA = d.roll("3d6", 0);
    }

    /*
        This gives a more favorable distribution
     */
    public void roll4D6DropLowest()
    {
        Dice d = new Dice();
        ArrayList<Integer> results = new ArrayList<>();
        STR = roll4D6DropLowest(d, results);
        INT = roll4D6DropLowest(d, results);
        WIS = roll4D6DropLowest(d, results);
        DEX = roll4D6DropLowest(d, results);
        CON = roll4D6DropLowest(d, results);
        CHA = roll4D6DropLowest(d, results);
    }

    private int roll4D6DropLowest(Dice d, ArrayList<Integer> results)
    {
        results.clear();
        results.add(d.roll("1d6"));
        results.add(d.roll("1d6"));
        results.add(d.roll("1d6"));
        results.add(d.roll("1d6"));
        Collections.sort(results);
        //Log.e(TAG, "Removing " + results.get(0));
        results.remove(0);
        int sum = 0;
        for(Integer i : results)
        {
            //Log.e(TAG, "Summing " + i);
            sum += i;
        }
        return sum;
    }

    public int getSTR() {
        return STR;
    }

    public int getINT() {
        return INT;
    }

    public int getWIS() {
        return WIS;
    }

    public int getDEX() {
        return DEX;
    }

    public int getCON() {
        return CON;
    }

    public int getCHA() {
        return CHA;
    }

    /*
        Basic fantasy
     */
    /*
    public static int calcStatBonusPenalty(int val)
    {
        int bonus = 0;
        if(val <= 3)
            bonus = -3;
        else if(val >=4 && val <=5)
            bonus = -2;
        else if(val >=6 && val <=8)
            bonus = -1;
        else if(val >= 9 && val <= 12)
            bonus = 0;
        else if(val >= 13 && val <= 15)
            bonus = 1;
        else if(val >= 16 && val <= 17)
            bonus = 2;
        else if(val >= 18)
            bonus = 3;
        return bonus;
    }
    */

    /*
        D&D 3.5E
     */
    public static int calcStatBonusPenalty(int val)
    {
        int bonus = 0;
        if(val <= 1)
            bonus = -5;
        else if(val >=2 && val <=3)
            bonus = -4;
        else if(val >=4 && val <=5)
            bonus = -3;
        else if(val >= 6 && val <= 7)
            bonus = -2;
        else if(val >= 8 && val <= 9)
            bonus = -1;
        else if(val >= 10 && val <= 11)
            bonus = 0;
        else if(val >= 12 && val <= 13)
            bonus = 1;
        else if(val >= 14 && val <= 15)
            bonus = 2;
        else if(val >= 16 && val <= 17)
            bonus = 3;
        else if(val >= 18 && val <= 19)
            bonus = 4;
        else if(val >= 20 && val <= 21)
            bonus = 5;
        else if(val >= 22 && val <= 23)
            bonus = 6;
        else if(val >= 24 && val <= 25)
            bonus = 7;
        else if(val >= 26 && val <= 27)
            bonus = 8;
        else if(val >= 28 && val <= 29)
            bonus = 9;
        else if(val >= 30 && val <= 31)
            bonus = 10;

        return bonus;
    }

    @Override
    public String toString() {
        return "CharacterStats{" +
                "STR=" + STR + " (" + calcStatBonusPenalty(STR) + ")" +
                ", INT=" + INT + " (" + calcStatBonusPenalty(INT) + ")" +
                ", WIS=" + WIS + " (" + calcStatBonusPenalty(WIS) + ")" +
                ", DEX=" + DEX + " (" + calcStatBonusPenalty(DEX) + ")" +
                ", CON=" + CON + " (" + calcStatBonusPenalty(CON) + ")" +
                ", CHA=" + CHA + " (" + calcStatBonusPenalty(CHA) + ")" +
                '}';
    }

    public JSONObject toJson() throws JSONException {
        JSONObject stats = new JSONObject();
        stats.put("STR", STR);
        stats.put("INT", INT);
        stats.put("WIS", WIS);
        stats.put("DEX", DEX);
        stats.put("CON", CON);
        stats.put("CHA", CHA);
        return stats;
    }

    public static CharacterStats fromJson(JSONObject chr) throws JSONException {
        CharacterStats stats = new CharacterStats();
        stats.STR = chr.getInt("STR");
        stats.INT = chr.getInt("INT");
        stats.WIS = chr.getInt("WIS");
        stats.DEX = chr.getInt("DEX");
        stats.CON = chr.getInt("CON");
        stats.CHA = chr.getInt("CHA");
        return stats;
    }
}
