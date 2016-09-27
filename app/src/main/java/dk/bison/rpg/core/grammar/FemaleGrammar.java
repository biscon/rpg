package dk.bison.rpg.core.grammar;

/**
 * Created by bison on 27-09-2016.
 */

public class FemaleGrammar implements Grammar{
    @Override
    public String getThirdPersonSingularObjectPronoun() {
        return "her";
    }

    @Override
    public String getThirdPersonSingularSubjectPronoun() {
        return "she";
    }

    @Override
    public String getPossessivePronoun() {
        return "her";
    }

    @Override
    public String getReflexivePronoun() {
        return "herself";
    }
}
