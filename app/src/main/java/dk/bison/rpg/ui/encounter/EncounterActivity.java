package dk.bison.rpg.ui.encounter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;

public class EncounterActivity extends BaseActivity implements EncounterMvpView {
    public static final String TAG = EncounterActivity.class.getSimpleName();
    EncounterPresenter presenter;
    @BindView(R.id.add_btn)
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter);
        ButterKnife.bind(this);

        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, EncounterPresenter.class);
        presenter.setup();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                CombatLogMessage msg = new CombatLogMessage();
                msg.normal("normal").violent("violent").bold("bold").normal("normal").bright("bright");
                PresentationManager.instance().publishEvent(msg);
                */
                presenter.executeRound();
                if(presenter.isCombatDone())
                {
                    addBtn.setVisibility(View.GONE);
                }
            }
        });

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
