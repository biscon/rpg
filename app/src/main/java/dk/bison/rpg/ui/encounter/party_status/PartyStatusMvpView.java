package dk.bison.rpg.ui.encounter.party_status;

import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface PartyStatusMvpView extends MvpView {
    void showParty(Party party);
}
