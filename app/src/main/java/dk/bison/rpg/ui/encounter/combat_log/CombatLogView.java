package dk.bison.rpg.ui.encounter.combat_log;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
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

    @Override
    public void addDivider()
    {
        int dp3 = (int) (3f * metrics.density);
        View v = new View(getContext());
        v.setBackgroundColor(0xff656565);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) metrics.density);
        lp.setMargins(0, dp3, 0, dp3);
        v.setLayoutParams(lp);
        v.setAlpha(0f);
        contentLl.addView(v);
        v.animate().alpha(1f).setDuration(500).setInterpolator(new LinearOutSlowInInterpolator()).start();
        scrollDown();

    }

    private void addEntry(SpannableStringBuilder sb, int effect) {
        FrameLayout fl = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp4 = (int) (4f * metrics.density);
        int dp6 = (int) (6f * metrics.density);
        int dp8 = (int) (8f * metrics.density);
        int dp16 = (int) (16f * metrics.density);
        lp.setMargins(0, 0, 0, 0);
        fl.setLayoutParams(lp);
        fl.setPadding(dp16, dp4, dp16, dp4);

        TextView tv = new TextView(getContext());
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(flp);
        tv.setText(sb);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(textColor);
        fl.addView(tv);
        contentLl.addView(fl);
        switch(effect)
        {
            case CombatLogMessage.FADE:
                setupFadeEffect(fl);
                break;
            case CombatLogMessage.SLIDE_SCALE_FADE:
                setupSlideScaleFadeEffect(fl);
                break;
            case CombatLogMessage.ROTATE:
                setupRotateEffect(fl);
                break;
            case CombatLogMessage.BOUNCE:
                setupBounceEffect(fl);
                break;
            default:
                setupFadeEffect(fl);
                break;
        }

        if(contentLl.getChildCount() > BACKLOG)
        {
            contentLl.removeViewAt(0);
        }
        scrollDown();
    }

    private void setupFadeEffect(View v)
    {
        v.setAlpha(0f);
        v.animate().alpha(1f).setDuration(750).setInterpolator(new LinearInterpolator()).start();
    }

    private void setupSlideScaleFadeEffect(View v)
    {
        float x = 0;
        float dist = (float) metrics.widthPixels - x;
        v.setX((float) metrics.widthPixels);
        v.setScaleY(4.0f);
        v.setAlpha(0.0f);
        //Interpolator customInterpolator = PathInterpolatorCompat.create(0.225f, 1.205f, 0.810f, -0.335f);
        v.animate().translationXBy(-dist).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
        v.animate().alpha(1f).setDuration(750).setInterpolator(new LinearInterpolator()).start();
        v.animate().scaleY(1).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
        //fl.animate().rotationXBy(360).setDuration(750).setInterpolator(new LinearInterpolator()).start();
    }

    private void setupRotateEffect(View v)
    {
        v.setAlpha(0.0f);
        v.setRotationX(-180);
        //v.setScaleY(3.0f);
        //Interpolator customInterpolator = PathInterpolatorCompat.create(0.225f, 1.205f, 0.810f, -0.335f);
        v.animate().alpha(1f).setDuration(750).setInterpolator(new LinearInterpolator()).start();
        v.animate().rotationX(0).setDuration(750).setInterpolator(new LinearInterpolator()).start();
        //v.animate().scaleY(1).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void setupBounceEffect(View v)
    {
        v.setAlpha(0.0f);
        v.setScaleY(3.0f);
        //Interpolator customInterpolator = PathInterpolatorCompat.create(0.225f, 1.205f, 0.810f, -0.335f);
        v.animate().alpha(1f).setDuration(750).setInterpolator(new LinearInterpolator()).start();
        v.animate().scaleY(1).setDuration(1500).setInterpolator(new BounceInterpolator()).start();
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
                if(msg.isDivider()) {
                    addDivider();
                }
                else if(msg.isRoundDone()) {
                    PresentationManager.instance().publishEvent(new RoundDoneEvent());
                }
                else
                    addEntry(msg.getSb(), msg.getEffect());
            }
        });
    }

    public void scrollDown()
    {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void scrollDownInstant()
    {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, scrollView.getChildAt(0).getHeight());
            }
        });
    }
}
