package dk.bison.rpg.ui.character;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.core.armor.Armor;
import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.character.CharacterStats;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.weapon.Weapon;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.util.Snacktory;

public class EditCharacterActivity extends BaseActivity implements EditCharacterMvpView {
    public static final String TAG = EditCharacterActivity.class.getSimpleName();

    @BindView(R.id.focus_thief)
    View focusThief;
    @BindView(R.id.create_char_name_et)
    EditText nameEt;
    @BindView(R.id.create_char_reroll_btn)
    Button rerollBtn;
    @BindView(R.id.create_char_level_btn)
    Button levelBtn;

    @BindView(R.id.create_char_str_tv)
    TextView strTv;
    @BindView(R.id.create_char_int_tv)
    TextView intTv;
    @BindView(R.id.create_char_wis_tv)
    TextView wisTv;
    @BindView(R.id.create_char_dex_tv)
    TextView dexTv;
    @BindView(R.id.create_char_con_tv)
    TextView conTv;
    @BindView(R.id.create_char_cha_tv)
    TextView chaTv;

    @BindView(R.id.create_char_class_tv)
    TextView classTv;
    @BindView(R.id.create_char_level_tv)
    TextView levelTv;
    @BindView(R.id.create_char_xp_tv)
    TextView xpTv;

    @BindView(R.id.create_char_hp_tv)
    TextView hpTv;
    @BindView(R.id.create_char_ac_tv)
    TextView acTv;
    @BindView(R.id.create_char_ab_tv)
    TextView abTv;

    @BindView(R.id.create_char_class_ll)
    LinearLayout classLl;

    @BindView(R.id.create_char_armor_ll)
    LinearLayout armorLl;
    @BindView(R.id.create_char_armor_name_tv)
    TextView armorNameTv;
    @BindView(R.id.create_char_armor_ac_tv)
    TextView armorAcTv;

    @BindView(R.id.create_char_shield_ll)
    LinearLayout shieldLl;
    @BindView(R.id.create_char_shield_name_tv)
    TextView shieldNameTv;
    @BindView(R.id.create_char_shield_ac_tv)
    TextView shieldAcTv;

    @BindView(R.id.create_char_mainhand_ll)
    LinearLayout mainHandLl;
    @BindView(R.id.create_char_mainhand_name_tv)
    TextView mainHandNameTv;
    @BindView(R.id.create_char_mainhand_dmg_tv)
    TextView mainHandDmgTv;

    @BindView(R.id.create_char_offhand_ll)
    LinearLayout offHandLl;
    @BindView(R.id.create_char_offhand_name_tv)
    TextView offHandNameTv;
    @BindView(R.id.create_char_offhand_dmg_tv)
    TextView offHandDmgTv;

    @BindView(R.id.create_char_create_btn)
    Button createBtn;

    EditCharacterPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);

        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, EditCharacterPresenter.class);
        //presenter.reset();

        ButterKnife.bind(this);

        focusThief.requestFocus();

        rerollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.rerollStats();
            }
        });

        levelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.levelUp();
            }
        });

        armorLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.chooseArmor(EditCharacterActivity.this, ArmorTemplate.CHEST);
            }
        });
        armorLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                presenter.setArmor(null);
                return true;
            }
        });

        shieldLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.chooseShield(EditCharacterActivity.this, ArmorTemplate.SHIELD);
            }
        });
        shieldLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                presenter.setShield(null);
                return true;
            }
        });

        mainHandLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.chooseWeapon(EditCharacterActivity.this, Attack.MAIN_HAND);
            }
        });
        mainHandLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                presenter.setMainhand(null);
                return true;
            }
        });

        offHandLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.chooseWeapon(EditCharacterActivity.this, Attack.OFF_HAND);
            }
        });
        offHandLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                presenter.setOffhand(null);
                return true;
            }
        });

        // save name on change
        nameEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                presenter.setName(editable.toString());
            }
        });

        // this is to make the edit text less annoying
        nameEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    nameEt.post(new Runnable() {
                        @Override
                        public void run() {
                            focusThief.requestFocus();
                        }
                    });
                    return true;
                }
                return false;
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

    private boolean validate()
    {
        if(nameEt.getText().toString().isEmpty())
        {
            Snacktory.showError(getContentView(), "Name is empty");
            return false;
        }
        return true;
    }

    @Override
    public void showStats(CharacterStats stats) {
        strTv.setText(String.format(Locale.US, "STR %02d %+d", stats.getSTR(), CharacterStats.calcStatBonusPenalty(stats.getSTR())));
        intTv.setText(String.format(Locale.US, "INT %02d %+d", stats.getINT(), CharacterStats.calcStatBonusPenalty(stats.getINT())));
        wisTv.setText(String.format(Locale.US, "WIS %02d %+d", stats.getWIS(), CharacterStats.calcStatBonusPenalty(stats.getWIS())));
        dexTv.setText(String.format(Locale.US, "DEX %02d %+d", stats.getDEX(), CharacterStats.calcStatBonusPenalty(stats.getDEX())));
        conTv.setText(String.format(Locale.US, "CON %02d %+d", stats.getCON(), CharacterStats.calcStatBonusPenalty(stats.getCON())));
        chaTv.setText(String.format(Locale.US, "CHA %02d %+d", stats.getCHA(), CharacterStats.calcStatBonusPenalty(stats.getCHA())));
    }

    @Override
    public void showClassLevelXp(String class_name, int level, int xp, int xp_next_level)
    {
        classTv.setText(class_name);
        levelTv.setText(String.format(Locale.US, "Level %d", level));
        xpTv.setText(String.format(Locale.US, "%d XP (%d)", xp, xp_next_level));
    }

    @Override
    public void showHpAcAttackBonus(int hp, int ac, int atk_bonus) {
        hpTv.setText(String.format(Locale.US, "%d", hp));
        acTv.setText(String.format(Locale.US, "%d", ac));
        abTv.setText(String.format(Locale.US, "%+d", atk_bonus));
    }

    @Override
    public void showArmor(Armor armor) {
        if(armor != null) {
            armorNameTv.setText(armor.getName());
            armorAcTv.setText(String.format(Locale.US, "AC %d", armor.getAC()));
        }
        else
        {
            armorNameTv.setText("No armor");
            armorAcTv.setText(String.format(Locale.US, "N/A"));
        }
    }

    @Override
    public void showShield(Armor armor) {
        if(armor != null) {
            shieldNameTv.setText(armor.getName());
            shieldAcTv.setText(String.format(Locale.US, "AC %d", armor.getAC()));
        }
        else
        {
            shieldNameTv.setText("No armor");
            shieldAcTv.setText(String.format(Locale.US, "N/A"));
        }
    }

    @Override
    public void showMainhand(Weapon weapon) {
        if(weapon != null) {
            mainHandNameTv.setText(weapon.getName());
            mainHandDmgTv.setText(weapon.getDamageDice());
        }
        else
        {
            mainHandNameTv.setText("None");
            mainHandDmgTv.setText("N/A");
        }
    }

    @Override
    public void showOffhand(Weapon weapon) {
        if(weapon != null) {
            offHandNameTv.setText(weapon.getName());
            offHandDmgTv.setText(weapon.getDamageDice());
        }
        else
        {
            offHandNameTv.setText("None");
            offHandDmgTv.setText("N/A");
        }
    }

    @Override
    public void showName(String name) {
        nameEt.setText(name);
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setEditMode(final boolean edit_mode) {
        if(edit_mode)
            createBtn.setText("Save");
        else
            createBtn.setText("Create");
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    if(!edit_mode)
                        presenter.create(EditCharacterActivity.this);
                    else
                        presenter.save(EditCharacterActivity.this);
                    finish();
                }
            }
        });
    }


}
