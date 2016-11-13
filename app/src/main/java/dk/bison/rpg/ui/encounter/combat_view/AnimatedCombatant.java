package dk.bison.rpg.ui.encounter.combat_view;

import android.graphics.RectF;

import dk.bison.rpg.core.combat.Combatant;

/**
 * Created by bison on 13-11-2016.
 */

public class AnimatedCombatant {
    public Combatant combatant;
    public RectF bounds = new RectF();
    public AnimationStrip walkStrip;

    public AnimatedCombatant(Combatant combatant) {
        this.combatant = combatant;
    }

    public void update(double dt)
    {
        if(walkStrip != null)
            walkStrip.update(dt);
    }
}
