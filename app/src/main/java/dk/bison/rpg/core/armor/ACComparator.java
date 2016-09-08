package dk.bison.rpg.core.armor;

import java.util.Comparator;

import dk.bison.rpg.core.combat.Combatant;

public class ACComparator implements Comparator<ArmorTemplate>
{
    @Override
    public int compare(ArmorTemplate c1, ArmorTemplate c2) {
        if(c1.getAC() == c2.getAC())
            return 0;
        if(c1.getAC() > c2.getAC())
            return 1;
        else
            return -1;
    }
}