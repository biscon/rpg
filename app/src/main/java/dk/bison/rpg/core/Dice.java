/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.bison.rpg.core;

import java.util.Random;

/**
 *
 * @author bison
 */
public class Dice 
{
    Random Generator;
    
    public Dice()
    {
        // time seed random number generator
        Generator = new Random();
    }

    public int roll(String type)
    {
        String parts[] = type.split("\\+");
        int adds = 0;
        if(parts.length > 1)
            adds = Integer.parseInt(parts[1].trim());
        return roll(parts[0].trim(), adds);
    }
    
    public int roll(String type, int add)
    {
        type = type.toLowerCase();
        String di[] = type.split("d");
        int no = Integer.parseInt(di[0]);
        int pips = Integer.parseInt(di[1]);
        int result = 0;
        for(int i = 0; i < no; i++)
        {
            result += (Generator.nextInt(pips)+1);
        }
        result += add;
        return result;
    }
    
    public int roll(int no, int add)
    {
        int result = 0;
        for(int i = 0; i < no; i++)
        {
            result += (Generator.nextInt(6)+1);
        }
        result += add;
        return result;        
    }
}
