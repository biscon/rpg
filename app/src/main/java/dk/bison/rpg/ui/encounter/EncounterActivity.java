package dk.bison.rpg.ui.encounter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.core.character.CharacterManager;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogView;
import dk.bison.rpg.ui.encounter.combat_map.CombatMapView;
import dk.bison.rpg.ui.encounter.combat_view.CombatSurfaceView;

public class EncounterActivity extends BaseActivity implements EncounterMvpView {
    public static final String TAG = EncounterActivity.class.getSimpleName();

    public static final int STATUS_TAB = 0;
    public static final int MAP_TAB = 1;

    EncounterPresenter presenter;
    /*
    @BindView(R.id.next_round_btn)
    Button nextRoundBtn;
    @BindView(R.id.log_clv)
    CombatLogView logClv;
    */
    @BindView(R.id.tabs_tl)
    TabLayout tabsTl;
    @BindView(R.id.status_tab)
    LinearLayout statusTabLl;
    @BindView(R.id.map_tab)
    LinearLayout mapTabLl;
    /*
    @BindView(R.id.map_view)
    CombatMapView mapView;
    */
    //@BindView(R.id.combat_view)
    //CombatSurfaceView combatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter);
        ButterKnife.bind(this);

        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, EncounterPresenter.class);
        //presenter.setup(this);

        /*
        nextRoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startNextRound();
            }
        });
        nextRoundBtn.post(new Runnable() {
            @Override
            public void run() {
                nextRoundBtn.setVisibility(View.GONE);
            }
        });
        */

        tabsTl.addTab(tabsTl.newTab().setText("Status"));
        tabsTl.addTab(tabsTl.newTab().setText("Map"));
        tabsTl.setTabGravity(TabLayout.GRAVITY_FILL);

        tabsTl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition())
                {
                    case STATUS_TAB:
                        statusTabLl.setVisibility(View.VISIBLE);
                        mapTabLl.setVisibility(View.GONE);
                        break;
                    case MAP_TAB:
                        statusTabLl.setVisibility(View.GONE);
                        mapTabLl.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //presenter.startCombat();
    }

    /*
    private void showNextRoundButton()
    {
        if(nextRoundBtn.getVisibility() == View.VISIBLE)
            return;
        Log.e(TAG, "Showing next");
        //nextRoundBtn.clearAnimation();
        nextRoundBtn.setVisibility(View.VISIBLE);
        nextRoundBtn.setY(-nextRoundBtn.getHeight());
        nextRoundBtn.animate().translationY(0).setDuration(350).setInterpolator(new LinearInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                nextRoundBtn.setVisibility(View.VISIBLE);
            }
        }).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            logClv.scrollDownInstant();
        }
    }).start();

        final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) logClv.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(0, nextRoundBtn.getHeight());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                //logClv.setPadding(0, (int) valueAnimator.getAnimatedValue(), 0, 0);
                lp.setMargins(0, (int) valueAnimator.getAnimatedValue(), 0, 0);
                logClv.setLayoutParams(lp);
                logClv.scrollDownInstant();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                logClv.scrollDownInstant();
            }
        });
        animator.setDuration(400);
        animator.start();
    }

    private void hideNextRoundButton()
    {
        if(nextRoundBtn.getVisibility() != View.VISIBLE)
            return;
        Log.e(TAG, "Hiding next");
        //nextRoundBtn.clearAnimation();
        nextRoundBtn.animate().translationY(-nextRoundBtn.getHeight()).setDuration(350).setInterpolator(new LinearInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                nextRoundBtn.setVisibility(View.GONE);
            }
        }).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                logClv.scrollDownInstant();
            }
        }).start();

        final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) logClv.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(nextRoundBtn.getHeight(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                //logClv.setPadding(0, (int) valueAnimator.getAnimatedValue(), 0, 0);
                lp.setMargins(0, (int) valueAnimator.getAnimatedValue(), 0, 0);
                logClv.setLayoutParams(lp);
                logClv.scrollDownInstant();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                logClv.scrollDownInstant();
            }
        });
        animator.setDuration(400);
        animator.start();
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        //combatView.resume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        //combatView.pause();
        presenter.detachView();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(presenter.isWaitingOnInput())
        {
            PresentationManager.instance().publishEvent(new OnBackButtonEvent());
        }
        else
            super.onBackPressed();
    }

    @Override
    public void postShowNextRoundButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //showNextRoundButton();
            }
        });
    }

    @Override
    public void postHideNextRoundButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //hideNextRoundButton();
            }
        });
    }

    @Override
    public void gotoTab(int pos) {
        TabLayout.Tab tab = tabsTl.getTabAt(pos);
        tab.select();
    }

    @Override
    public void endOfCombat() {
        // save characters
        CharacterManager.instance().save(this);
    }
}
