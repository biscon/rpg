package dk.bison.rpg.core.combat;

import java.util.Comparator;

public class HPComparator implements Comparator<Combatant>
{
    @Override
    public int compare(Combatant c1, Combatant c2) {
        if(c1.getHP() == c2.getHP())
            return 0;
        if(c1.getHP() > c2.getHP())
            return 1;
        else
            return -1;
    }
}