package dk.bison.rpg.core.combat;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Combatant>
{
    @Override
    public int compare(Combatant c1, Combatant c2) {
        if(c1.getDistanceToCurrentTarget() == c2.getDistanceToCurrentTarget())
            return 0;
        if(c1.getDistanceToCurrentTarget() > c2.getDistanceToCurrentTarget())
            return 1;
        else
            return -1;
    }
}