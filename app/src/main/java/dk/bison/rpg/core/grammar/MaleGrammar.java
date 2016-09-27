package dk.bison.rpg.core.grammar;

/**
 * Created by bison on 27-09-2016.
 */

public class MaleGrammar implements Grammar{
    @Override
    public String getThirdPersonSingularObjectPronoun() {
        return "him";
    }

    @Override
    public String getThirdPersonSingularSubjectPronoun() {
        return "he";
    }

    @Override
    public String getPossessivePronoun() {
        return "his";
    }

    @Override
    public String getReflexivePronoun() {
        return "himself";
    }
}
