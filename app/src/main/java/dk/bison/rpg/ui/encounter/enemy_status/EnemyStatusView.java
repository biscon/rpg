package dk.bison.rpg.ui.encounter.enemy_status;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.ButterKnife;
import dk.bison.rpg.R;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.mvp.PresentationManager;

/**
 * Created by bison on 11-09-2016.
 */
public class EnemyStatusView extends FrameLayout implements EnemyStatusMvpView {
    public static final String TAG = EnemyStatusView.class.getSimpleName();
    EnemyStatusPresenter presenter;
    ScrollView scrollView;
    LinearLayout contentLl;
    ArgbEvaluator rgbEvaluator = new ArgbEvaluator();
    private static int deadColor = 0xFFB71C1C;
    private static int maxHpColor = 0xFF558B2F;

    public EnemyStatusView(Context context) {
        super(context);
        presenter = PresentationManager.instance().presenter(context, EnemyStatusPresenter.class);
    }
    public EnemyStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        presenter = PresentationManager.instance().presenter(context, EnemyStatusPresenter.class);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scrollView = new ScrollView(getContext());
        contentLl = new LinearLayout(getContext());
        contentLl.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(contentLl);
        addView(scrollView);
        presenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.detachView();
        removeAllViews();
        super.onDetachedFromWindow();
    }

    @Override
    public void showParty(Party party)
    {
        contentLl.removeAllViews();
        contentLl.addView(makeHeaderView());
        for(Combatant c : party)
        {
            View char_v = makeCharacterView(c);
            contentLl.addView(char_v);
        }
    }

    private View makeCharacterView(final Combatant c) {
        View v = LayoutInflater.from(this.getContext()).inflate(R.layout.viewholder_char_status, this, false);
        TextView name_tv = ButterKnife.findById(v, R.id.name_tv);
        TextView level_tv = ButterKnife.findById(v, R.id.level_tv);
        TextView ac_tv = ButterKnife.findById(v, R.id.ac_tv);
        TextView hp_tv = ButterKnife.findById(v, R.id.hp_tv);

        level_tv.setText(String.format(Locale.US, "%d", c.getLevel()));
        ac_tv.setText(String.format(Locale.US, "%d", c.getAC()));

        float max_hp = (float) c.getMaxHP();
        float cur_hp = (float) c.getHP();
        float frac = 0;
        if(cur_hp > 0)
            frac = cur_hp / max_hp;
        //Log.e(TAG, "max_hp = " + max_hp + ", cur_hp = " + cur_hp+ " frac = " + frac);
        int col = (int) rgbEvaluator.evaluate(frac, deadColor, maxHpColor);
        hp_tv.setTextColor(col);
        hp_tv.setText(String.format(Locale.US, "%d", c.getHP()));

        name_tv.setText(c.getName());
        name_tv.setTextColor(col);
        if(c.isDead()) {
            name_tv.setPaintFlags(name_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return v;
    }

    private View makeHeaderView() {
        View v = LayoutInflater.from(this.getContext()).inflate(R.layout.viewholder_char_status, this, false);
        TextView name_tv = ButterKnife.findById(v, R.id.name_tv);
        TextView level_tv = ButterKnife.findById(v, R.id.level_tv);
        TextView ac_tv = ButterKnife.findById(v, R.id.ac_tv);
        TextView hp_tv = ButterKnife.findById(v, R.id.hp_tv);
        name_tv.setText("");
        name_tv.setTypeface(null, Typeface.BOLD);
        level_tv.setText("LVL");
        level_tv.setTypeface(null, Typeface.BOLD);
        ac_tv.setText("AC");
        ac_tv.setTypeface(null, Typeface.BOLD);
        hp_tv.setText("HP");
        hp_tv.setTypeface(null, Typeface.BOLD);
        return v;
    }
}
