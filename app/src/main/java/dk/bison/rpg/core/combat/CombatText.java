package dk.bison.rpg.core.combat;

/**
 * Created by bison on 28-09-2016.
 */

public class CombatText {
    public String text;
    public String deathPostfix;

    public String getText(Combatant c, Combatant opponent, int dmg) {
        return replaceTokens(c, opponent, dmg, text);
    }

    public String getDeathPostfix(Combatant c, Combatant opponent, int dmg) {
        return replaceTokens(c, opponent, dmg, deathPostfix);
    }

    private String replaceTokens(Combatant c, Combatant opponent, int dmg, String str)
    {
        String s = str.replaceAll("#name#", c.getName());
        s = s.replaceAll("#enemy_name#", opponent.getName());

        s = s.replaceAll("#dmg#", String.valueOf(dmg));

        s = s.replaceAll("#pos#", c.getGrammar().getPossessivePronoun());
        s = s.replaceAll("#enemy_pos#", opponent.getGrammar().getPossessivePronoun());

        s = s.replaceAll("#obj#", c.getGrammar().getThirdPersonSingularObjectPronoun());
        s = s.replaceAll("#enemy_obj#", opponent.getGrammar().getThirdPersonSingularObjectPronoun());

        s = s.replaceAll("#sub#", c.getGrammar().getThirdPersonSingularSubjectPronoun());
        s = s.replaceAll("#enemy_sub#", opponent.getGrammar().getThirdPersonSingularSubjectPronoun());

        s = s.replaceAll("#ref#", c.getGrammar().getReflexivePronoun());
        s = s.replaceAll("#enemy_ref#", opponent.getGrammar().getReflexivePronoun());
        return s;
    }
}
