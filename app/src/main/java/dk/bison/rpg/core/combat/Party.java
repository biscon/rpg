package dk.bison.rpg.core.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dk.bison.rpg.core.character.Character;

/**
 * Created by bison on 11-09-2016.
 */
public class Party implements Iterable<Combatant> {
    List<Combatant> combatants;

    public Party() {
        combatants = new ArrayList<>();
    }

    public Party(List<Combatant> combatants) {
        this.combatants = combatants;
    }

    @Override
    public Iterator<Combatant> iterator() {
        return combatants.iterator();
    }

    public void add(Combatant c)
    {
        combatants.add(c);
    }

    public void remove(Combatant c)
    {
        combatants.remove(c);
    }
}
