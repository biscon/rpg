package dk.bison.rpg.ui.encounter.combat_log;

import android.text.SpannableStringBuilder;

import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface CombatLogMvpView extends MvpView {
    void postMessage(CombatLogMessage msg);
    void addDivider();
    void show();
    void showImmediately();
    void showPeriod(int delay);
    void hide();
}
