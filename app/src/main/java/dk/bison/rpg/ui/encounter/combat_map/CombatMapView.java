package dk.bison.rpg.ui.encounter.combat_map;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.util.Util;

/**
 * Created by bison on 25-09-2016.
 */

public class CombatMapView extends View implements CombatMapMvpView {
    public static final String TAG = CombatMapView.class.getSimpleName();
    CombatMapPresenter presenter;
    Paint linePaint;
    Paint labelPaint;
    Paint labelPaint2;
    Paint friendlyPaint;
    Paint enemyPaint;
    Paint movePaint;
    List<Combatant> combatants;
    DisplayMetrics metrics;
    int mapMin = -50;
    int mapMax = 50;
    float [] widths = new float[10];
    private static int deadColor = 0xFFB71C1C;
    private static int maxHpColor = 0xFFFFFFFF;
    ArgbEvaluator rgbEvaluator = new ArgbEvaluator();
    boolean displayMove = false;
    Combatant movingCombatant;
    int moveDistance;

    public CombatMapView(Context context) {
        super(context);
        init(context);
    }

    public CombatMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        presenter = PresentationManager.instance().presenter(context, CombatMapPresenter.class);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        labelPaint = new Paint();
        labelPaint.setColor(0x80FFFFFF);
        labelPaint.setTextSize(metrics.density*10);

        labelPaint2 = new Paint();
        labelPaint2.setColor(0xFFCCCCCC);
        labelPaint2.setTextSize(metrics.density*10);

        linePaint = new Paint();
        linePaint.setColor(0xFFFFFFFF);
        linePaint.setStrokeWidth(metrics.density);
        friendlyPaint = new Paint();
        friendlyPaint.setColor(0xA000AA00);
        enemyPaint = new Paint();
        enemyPaint.setColor(0xA0AA0000);

        movePaint = new Paint();
        movePaint.setStrokeWidth(2*metrics.density);
        movePaint.setColor(0xAAEF6C00);
    }

    @Override
    public void drawMap(List<Combatant> combatants)
    {
        this.combatants = combatants;
        invalidate();
    }

    @Override
    public void displayMove(Combatant c, int distance)
    {
        displayMove = true;
        movingCombatant = c;
        moveDistance = distance;
        postInvalidate();
    }

    @Override
    public void clearMove()
    {
        displayMove = false;
        movingCombatant = null;
        moveDistance = 0;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float middle = getHeight()/2;
        float marker_height = metrics.density * 5;
        float middle_marker_height = metrics.density * 15;
        //linePaint.setStrokeWidth(2 * metrics.density);
        canvas.drawLine(0, middle, getWidth(), middle, linePaint);
        linePaint.setStrokeWidth(metrics.density);
        for(int i=mapMin; i < mapMax; i += 10)
        {
            float x = (float) Util.reMapDouble(mapMin, mapMax, 0, (double) getWidth(), i);
            if(i > mapMin) {
                if(i == 0) {
                    String str_i = String.valueOf(i);
                    float txt_w = labelPaint.measureText(str_i);
                    canvas.drawText(str_i, x - (txt_w/2), middle + middle_marker_height + (4 * metrics.density) + labelPaint.getTextSize(), labelPaint);
                    canvas.drawLine(x, middle - middle_marker_height, x, middle + middle_marker_height, linePaint);
                }
                else {
                    String str_i = String.valueOf(i);
                    float txt_w = labelPaint.measureText(str_i);
                    canvas.drawText(str_i, x - (txt_w/2), middle + marker_height + (4 * metrics.density) + labelPaint.getTextSize(), labelPaint);
                    canvas.drawLine(x, middle - marker_height, x, middle + marker_height, linePaint);
                }
            }
        }
        int ally_index = 0;
        int enemy_index = 0;
        float spacing = 2 * metrics.density;
        for(int i=0; i < combatants.size(); i++)
        {
            Combatant c = combatants.get(i);
            float max_hp = (float) c.getMaxHP();
            float cur_hp = (float) c.getHP();
            float frac = 0;
            if(cur_hp > 0)
                frac = cur_hp / max_hp;
            //Log.e(TAG, "max_hp = " + max_hp + ", cur_hp = " + cur_hp+ " frac = " + frac);
            int col = (int) rgbEvaluator.evaluate(frac, deadColor, maxHpColor);
            labelPaint2.setColor(col);
            if(c.isDead())
                continue;
            float x = (float) Util.reMapDouble(-50, 50, 0, (double) getWidth(), (double) c.getPosition());
            if(c.getFaction().getName().contentEquals("Player")) {
                canvas.drawLine(x, middle, x, middle - (35 * metrics.density) - (ally_index*(labelPaint.getTextSize()+spacing)), labelPaint);
                canvas.drawCircle(x, middle, 8 * metrics.density, friendlyPaint);
                canvas.drawText(c.getName(), x - (labelPaint.measureText(c.getName())/2), middle - (35 * metrics.density) - (ally_index*(labelPaint.getTextSize()+spacing)), labelPaint2);
                ally_index++;
            }
            else {
                canvas.drawLine(x, middle, x,middle + (35 * metrics.density) + (enemy_index*(labelPaint.getTextSize()+spacing)), labelPaint);
                canvas.drawCircle(x, middle, 8 * metrics.density, enemyPaint);
                canvas.drawText(c.getName(), x - (labelPaint.measureText(c.getName())/2), middle + (35 * metrics.density) + labelPaint.getTextSize() + (enemy_index*(labelPaint.getTextSize()+spacing)), labelPaint2);
                enemy_index++;
            }
        }
        // draw gotoMove
        if(displayMove)
        {
            float start_x = (float) Util.reMapDouble(-50, 50, 0, (double) getWidth(), (double) movingCombatant.getPosition());
            float end_x = (float) Util.reMapDouble(-50, 50, 0, (double) getWidth(), (double) (movingCombatant.getPosition()+moveDistance));
            canvas.drawLine(start_x, middle, end_x, middle, movePaint);
            canvas.drawCircle(end_x, middle, 8 * metrics.density, movePaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.detachView();
        super.onDetachedFromWindow();
    }
}
