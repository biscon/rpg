package dk.bison.rpg.ui.encounter.player_control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.R;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.PresentationManager;

/**
 * Created by bison on 11-09-2016.
 */
public class PlayerControlView extends FrameLayout implements PlayerControlMvpView {
    public static final String TAG = PlayerControlView.class.getSimpleName();
    PlayerControlPresenter presenter;
    ArgbEvaluator rgbEvaluator = new ArgbEvaluator();
    private static int deadColor = 0xFFB71C1C;
    private static int maxHpColor = 0xFFFFFFFF;

    @BindView(R.id.pcv_do_nothing_btn)
    Button doNothingBtn;
    @BindView(R.id.pcv_move_btn)
    Button moveBtn;
    @BindView(R.id.pcv_attack_btn)
    Button attackBtn;
    @BindView(R.id.pcv_name_tv)
    TextView nameTv;
    @BindView(R.id.pcv_action_view)
    LinearLayout actionView;
    @BindView(R.id.pcv_move_view)
    LinearLayout moveView;
    @BindView(R.id.pcv_attack_view)
    LinearLayout attackView;
    @BindView(R.id.pcv_move_sb)
    SeekBar moveSb;
    @BindView(R.id.pcv_move_exec_btn)
    Button execMoveBtn;
    @BindView(R.id.pcv_move_info_tv)
    TextView moveInfoTv;
    @BindView(R.id.pcv_attack_target_tv)
    TextView targetTv;
    @BindView(R.id.pcv_attack_list_ll)
    LinearLayout attackListLl;

    Combatant combatant;
    Combatant target;
    List<Combatant> combatants;
    int curDistance = 0;

    public PlayerControlView(Context context) {
        super(context);
        presenter = PresentationManager.instance().presenter(context, PlayerControlPresenter.class);
    }
    public PlayerControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        presenter = PresentationManager.instance().presenter(context, PlayerControlPresenter.class);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initUI();
        presenter.attachView(this);
    }

    private void initUI()
    {
        ButterKnife.bind(this);
        doNothingBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.endTurn();
            }
        });

        moveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.gotoMove();
            }
        });

        attackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.gotoAttack();
            }
        });

        moveSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress - (seekBar.getMax()/2);
                curDistance = value;
                PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(combatant, value));
                updateMoveInfo(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        execMoveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curDistance != 0)
                    presenter.performMove(curDistance);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.detachView();
        super.onDetachedFromWindow();
    }

    private void updateMoveInfo(int value)
    {
        String dir = "";
        if(value < 0)
            dir = "backwards";
        if(value > 0)
            dir = "forwards";
        moveInfoTv.setText(String.format(Locale.US, "Move %dm %s", Math.abs(value), dir));
    }

    @Override
    public void updateAttacks(List<Attack> attacks) {
        attackListLl.removeAllViews();
        if(attacks == null)
            return;
        for(Attack atk : attacks)
        {
            View v = makeAttackView(atk);
            attackListLl.addView(v);
        }
        if(attacks.isEmpty())
        {
            TextView tv = new TextView(getContext());
            tv.setText(combatant.getName() + " has no more usable attacks against " + target.getName() + " at this range or in this round.");
            tv.setTextColor(getResources().getColor(R.color.white));
            //float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            attackListLl.addView(tv);
        }
    }

    private View makeAttackView(Attack atk) {
        View v = LayoutInflater.from(this.getContext()).inflate(R.layout.viewholder_attack, attackListLl, false);
        TextView name_tv = ButterKnife.findById(v, R.id.name_tv);
        TextView dmg_tv = ButterKnife.findById(v, R.id.dmg_tv);
        TextView size_tv  = ButterKnife.findById(v, R.id.size_tv);
        TextView dist_tv  = ButterKnife.findById(v, R.id.dist_tv);
        LinearLayout weapon_ll = ButterKnife.findById(v, R.id.weapon_ll);

        name_tv.setText(atk.name);
        dmg_tv.setText(atk.damage);
        //size_tv.setText(String.format(Locale.US, "s%", atk.getSizeAsString()));
        dist_tv.setText(String.format(Locale.US, "Range %d", atk.range));
        weapon_ll.setTag(atk);
        weapon_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            presenter.performAttack((Attack) view.getTag());
            }
        });

        return v;
    }

    @Override
    public void show() {
        final View pv = this;
        pv.setAlpha(0);
        pv.setVisibility(View.VISIBLE);
        pv.animate().alpha(1).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pv.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    @Override
    public void hide() {
        final View pv = this;
        pv.setAlpha(1);
        pv.setVisibility(View.VISIBLE);
        pv.animate().alpha(0).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pv.setVisibility(View.GONE);
            }
        }).start();
    }

    @Override
    public void showActionView()
    {
        // clear movement display if any
        PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(null, 0));
        actionView.setVisibility(View.VISIBLE);
        moveView.setVisibility(View.GONE);
        attackView.setVisibility(View.GONE);
        nameTv.setText(combatant.getName() + " wants to");
    }

    @Override
    public void showMoveView()
    {
        actionView.setVisibility(View.GONE);
        attackView.setVisibility(View.GONE);
        moveView.setVisibility(View.VISIBLE);
        int speed = combatant.getAI().getSpeed();
        moveSb.setMax(2*speed);
        moveSb.setProgress(speed);
    }

    @Override
    public void showAttackView()
    {
        // clear movement display if any
        PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(null, 0));
        actionView.setVisibility(View.GONE);
        moveView.setVisibility(View.GONE);
        attackView.setVisibility(View.VISIBLE);
        setTarget(target);
    }

    @Override
    public void setMoveEnabled(boolean enabled) {
        moveBtn.setEnabled(enabled);
        if(enabled)
            moveBtn.setBackgroundResource(R.drawable.ripple_accent);
        else
            moveBtn.setBackgroundResource(R.drawable.ripple_trans);
    }

    @Override
    public void setAttackEnabled(boolean enabled) {
        attackBtn.setEnabled(enabled);
        if(enabled)
            attackBtn.setBackgroundResource(R.drawable.ripple_accent);
        else
            attackBtn.setBackgroundResource(R.drawable.ripple_trans);
    }

    @Override
    public void setTarget(Combatant target) {
        this.target = target;
        if(target == null) {
            targetTv.setText("Target: Please select from list");
        }
        else {
            targetTv.setText(String.format(Locale.US, "Target: %s (%d/%d HP) at %dm", target.getName(), target.getHP(), target.getMaxHP(), CombatPosition.distanceBetweenCombatants(combatant, target)));
        }
    }

    @Override
    public void showCombatant(Combatant c) {
        combatant = c;
        showActionView();
    }

    @Override
    public void setCombatants(List<Combatant> combatants) {
        this.combatants = combatants;
    }


}
