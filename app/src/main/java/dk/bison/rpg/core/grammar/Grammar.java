package dk.bison.rpg.core.grammar;

/**
 * Created by bison on 27-09-2016.
 */

public interface Grammar {
    String getThirdPersonSingularObjectPronoun(); // (him/her/it)
    String getThirdPersonSingularSubjectPronoun(); // (he/she/it)
    String getPossessivePronoun(); // (his/her/its)
    String getReflexivePronoun(); // (himself/herself/itself)
}
