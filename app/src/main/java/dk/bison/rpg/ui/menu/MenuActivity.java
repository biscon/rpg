package dk.bison.rpg.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.character.ChooseCharacterActivity;
import dk.bison.rpg.ui.character.CreateCharacterActivity;
import dk.bison.rpg.ui.party.PartyActivity;

public class MenuActivity extends BaseActivity implements MenuMvpView {
    public static final String TAG = MenuActivity.class.getSimpleName();
    @BindView(R.id.menu_create_char_btn)
    Button createCharBtn;
    @BindView(R.id.menu_random_enc_btn)
    Button randomEncBtn;
    MenuPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, MenuPresenter.class);

        createCharBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MenuActivity.this, CreateCharacterActivity.class);
                startActivity(i);
            }
        });

        randomEncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MenuActivity.this, PartyActivity.class);
                startActivity(i);
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
