package dk.bison.rpg.ui.encounter.combat_log;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
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
import dk.bison.rpg.ui.encounter.StatusUpdateEvent;

/**
 * Created by bison on 11-09-2016.
 */
public class CombatLogView extends FrameLayout implements CombatLogMvpView {
    public static final String TAG = CombatLogView.class.getSimpleName();
    CombatLogPresenter presenter;
    ScrollView scrollView;
    LinearLayout contentLl;
    DisplayMetrics metrics;
    public static final int BACKLOG = 50;
    int textColor;

    public CombatLogView(Context context) {
        super(context);
        presenter = PresentationManager.instance().presenter(context, CombatLogPresenter.class);
    }
    public CombatLogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        presenter = PresentationManager.instance().presenter(context, CombatLogPresenter.class);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        textColor = getResources().getColor(R.color.grey_300);

        scrollView = new ScrollView(getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
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
        name_tv.setText(c.getName());
        level_tv.setText(String.format(Locale.US, "%d", c.getLevel()));
        ac_tv.setText(String.format(Locale.US, "%d", c.getAC()));
        hp_tv.setText(String.format(Locale.US, "%d", c.getHP()));
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

    private void addEntry(SpannableStringBuilder sb) {
        FrameLayout fl = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp4 = (int) (4f * metrics.density);
        int dp6 = (int) (6f * metrics.density);
        int dp8 = (int) (8f * metrics.density);
        int dp16 = (int) (16f * metrics.density);
        lp.setMargins(0, 0, 0, 0);
        fl.setLayoutParams(lp);
        fl.setAlpha(0.0f);
        fl.setPadding(dp16, dp4, dp16, dp4);

        TextView tv = new TextView(getContext());
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(flp);
        tv.setText(sb);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(textColor);
        fl.addView(tv);
        contentLl.addView(fl);
        //float x = 16 * metrics.density;
        float x = 0;
        float dist = (float) metrics.widthPixels - x;
        fl.setX((float) metrics.widthPixels);
        fl.setScaleY(4.0f);
        //Interpolator customInterpolator = PathInterpolatorCompat.create(0.225f, 1.205f, 0.810f, -0.335f);
        fl.animate().translationXBy(-dist).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
        fl.animate().alpha(1f).setDuration(750).setInterpolator(new LinearInterpolator()).start();
        fl.animate().scaleY(1).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
        //fl.animate().rotationXBy(360).setDuration(750).setInterpolator(new LinearInterpolator()).start();
        if(contentLl.getChildCount() > BACKLOG)
        {
            contentLl.removeViewAt(0);
        }
        scrollDown();
    }

    /**
     * This gets called from a timer thread, so it has to post to the GUI thread
     * @param msg
     */
    @Override
    public void postMessage(final CombatLogMessage msg) {
        post(new Runnable() {
            @Override
            public void run() {
                addEntry(msg.getSb());
            }
        });
    }

    private void scrollDown()
    {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
