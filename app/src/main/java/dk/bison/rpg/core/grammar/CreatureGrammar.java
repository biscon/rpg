package dk.bison.rpg.core.grammar;

/**
 * Created by bison on 27-09-2016.
 */

public class CreatureGrammar implements Grammar{
    @Override
    public String getThirdPersonSingularObjectPronoun() {
        return "it";
    }

    @Override
    public String getThirdPersonSingularSubjectPronoun() {
        return "it";
    }

    @Override
    public String getPossessivePronoun() {
        return "its";
    }

    @Override
    public String getReflexivePronoun() {
        return "itself";
    }
}
