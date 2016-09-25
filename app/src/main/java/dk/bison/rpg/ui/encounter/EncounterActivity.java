package dk.bison.rpg.ui.encounter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.mvp.PresentationManager;

public class EncounterActivity extends BaseActivity implements EncounterMvpView {
    public static final String TAG = EncounterActivity.class.getSimpleName();
    EncounterPresenter presenter;
    @BindView(R.id.next_round_btn)
    Button nextRoundBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter);
        ButterKnife.bind(this);

        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, EncounterPresenter.class);
        presenter.setup();

        nextRoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                CombatLogMessage msg = new CombatLogMessage();
                msg.normal("normal").violent("violent").bold("bold").normal("normal").bright("bright");
                PresentationManager.instance().publishEvent(msg);
                */
                presenter.executeRound();
            }
        });

        nextRoundBtn.post(new Runnable() {
            @Override
            public void run() {
                nextRoundBtn.setVisibility(View.GONE);
                presenter.executeRound();
            }
        });
    }

    @Override
    public void showNextRoundButton()
    {
        if(nextRoundBtn.getVisibility() == View.VISIBLE)
            return;
        Log.e(TAG, "Showing next");
        //nextRoundBtn.clearAnimation();
        nextRoundBtn.setVisibility(View.VISIBLE);
        nextRoundBtn.setY(-nextRoundBtn.getHeight());
        nextRoundBtn.animate().translationY(0).setDuration(1000).setInterpolator(new BounceInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                nextRoundBtn.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    @Override
    public void hideNextRoundButton()
    {
        if(nextRoundBtn.getVisibility() != View.VISIBLE)
            return;
        Log.e(TAG, "Hiding next");
        //nextRoundBtn.clearAnimation();
        nextRoundBtn.animate().translationY(-nextRoundBtn.getHeight()).setDuration(350).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                nextRoundBtn.setVisibility(View.GONE);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        presenter.detachView();
        super.onPause();
    }
}
