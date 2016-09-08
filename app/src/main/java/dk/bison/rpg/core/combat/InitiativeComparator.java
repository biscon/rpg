package dk.bison.rpg.core.combat;

import java.util.Comparator;

public class InitiativeComparator implements Comparator<Combatant>
{
    @Override
    public int compare(Combatant c1, Combatant c2) {
        if(c1.getLastInitiativeRolled() == c2.getLastInitiativeRolled())
            return 0;
        if(c1.getLastInitiativeRolled() < c2.getLastInitiativeRolled())
            return 1;
        else
            return -1;
    }
}