package dk.bison.rpg.ui.encounter.combat_log;

import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 02-10-2016.
 */

public class CombatLogShowPeriodEvent implements MvpEvent {
    public int wait;

    public CombatLogShowPeriodEvent(int wait) {
        this.wait = wait;
    }
}
