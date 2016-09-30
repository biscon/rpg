package dk.bison.rpg.ui.character;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.armor.ArmorFactory;
import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.character.Character;
import dk.bison.rpg.core.character.CharacterManager;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.weapon.Weapon;
import dk.bison.rpg.core.weapon.WeaponFactory;
import dk.bison.rpg.core.weapon.WeaponTemplate;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.armor.ArmorChangeEvent;
import dk.bison.rpg.ui.armor.ChooseArmorActivity;
import dk.bison.rpg.ui.weapon.ChooseWeaponActivity;
import dk.bison.rpg.ui.weapon.WeaponChangeEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class EditCharacterPresenter extends BasePresenter<EditCharacterMvpView> {
    public static final String TAG = EditCharacterPresenter.class.getSimpleName();
    Character character;
    String title = "Create Character";
    boolean editMode = false;

    @Override
    public void onCreate(Context context) {
        reset();
    }

    @Override
    public void attachView(EditCharacterMvpView mvpView) {
        super.attachView(mvpView);
        updateView();
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    private void updateView()
    {
        getMvpView().setTitle(title);
        getMvpView().setEditMode(editMode);
        getMvpView().showName(character.getName());
        getMvpView().setGender(character.getGender());
        getMvpView().showStats(character.getStats());
        int xp_next_level = character.getCharClass().getXPForLevel(character.getLevel()+1);
        getMvpView().showClassLevelXp(character.getCharClass().getName(), character.getLevel(), character.getXP(), xp_next_level);
        getMvpView().showHpAcAttackBonus(character.getHP(), character.getAC(), character.getAttackBonus());
        getMvpView().showArmor(character.getArmor());
        getMvpView().showShield(character.getShield());
        getMvpView().showMainhand(character.getMainHandWeapon());
        getMvpView().showOffhand(character.getOffHandWeapon());
    }

    public void reset()
    {
        Log.e(TAG, "resetting character");
        character = new Character();
        title = "Create Character";
        editMode = false;
    }


    public void rerollStats()
    {
        character.rerollStats();
        if(isViewAttached()) {
            updateView();
        }
    }

    public void levelUp()
    {
        character.giveLevel();
        if(isViewAttached())
            updateView();
    }

    public void chooseArmor(Context c, int type)
    {
        Intent i = new Intent(c, ChooseArmorActivity.class);
        i.putExtra("type", type);
        c.startActivity(i);
    }

    public void setArmor(ArmorTemplate template)
    {
        if(template != null)
            character.setArmor(ArmorFactory.makeArmor(template.getName()));
        else
            character.setArmor(null);
        if(isViewAttached())
            updateView();
    }

    public void chooseShield(Context c, int type)
    {
        Intent i = new Intent(c, ChooseArmorActivity.class);
        i.putExtra("type", type);
        c.startActivity(i);
    }

    public void setShield(ArmorTemplate template)
    {
        if(template != null) {
            Weapon mh_wep = character.getMainHandWeapon();
            if(mh_wep != null)
            {
                if(mh_wep.isRanged() || mh_wep.getSize() == WeaponTemplate.SIZE_L)
                    character.equipMainHand(null);
            }
            character.equipOffHand(null);
            character.setShield(ArmorFactory.makeArmor(template.getName()));
        }
        else
            character.setShield(null);
        if(isViewAttached())
            updateView();
    }

    public void chooseWeapon(Context c, int slot)
    {
        Intent i = new Intent(c, ChooseWeaponActivity.class);
        i.putExtra("slot", slot);
        c.startActivity(i);
    }

    public void setMainhand(WeaponTemplate template)
    {
        if(template != null) {
            character.equipMainHand(WeaponFactory.makeWeapon(template.getName()));
            // size L and ranged weapons unequip offhand
            if(template.getSize() == WeaponTemplate.SIZE_L) {
                character.equipOffHand(null);
                character.setShield(null);
            }
        }
        else
            character.equipMainHand(null);
        if(isViewAttached())
            updateView();
    }

    public void setOffhand(WeaponTemplate template)
    {
        if(template != null) {
            /*
                Check and see if we have a main hand and if it allows equipping an offhand
             */
            Weapon mh_wep = character.getMainHandWeapon();
            boolean mh_okay = false;
            if(mh_wep != null)
            {
                if(mh_wep.getSize() == WeaponTemplate.SIZE_S || mh_wep.getSize() == WeaponTemplate.SIZE_M)
                    mh_okay = true;
            }
            else
                mh_okay = true;
            /*
                Only equip small, medium weapons in offhand
             */
            if((template.getSize() == WeaponTemplate.SIZE_M
                    || template.getSize() == WeaponTemplate.SIZE_S)
                    && mh_okay)
            {
                character.setShield(null);
                character.equipOffHand(WeaponFactory.makeWeapon(template.getName()));
            }
        }
        else
            character.equipOffHand(null);
        if(isViewAttached())
            updateView();
    }

    public void setName(String name)
    {
        name = name.trim();
        character.setName(name);
        Log.d(TAG, "Name changed to " + name);
    }

    public void setGender(char gender)
    {
        character.setGender(gender);
    }

    public void create(Context c)
    {
        CharacterManager.instance().add(character);
        CharacterManager.instance().save(c);
        PresentationManager.instance().publishEvent(new CharacterCreateEvent(character));
    }

    public void save(Context c)
    {
        CharacterManager.instance().save(c);
        PresentationManager.instance().publishEvent(new CharacterEditEvent(character));
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof ArmorChangeEvent)
        {
            ArmorChangeEvent ac_event = (ArmorChangeEvent) event;
            if(ac_event.armor.getType() == ArmorTemplate.CHEST)
                setArmor(ac_event.armor);
            if(ac_event.armor.getType() == ArmorTemplate.SHIELD)
                setShield(ac_event.armor);
        }
        if(event instanceof WeaponChangeEvent)
        {
            WeaponChangeEvent wc_event = (WeaponChangeEvent) event; // lol
            if(wc_event.slot == Attack.MAIN_HAND)
            {
                setMainhand(wc_event.weapon);
            }
            if(wc_event.slot == Attack.OFF_HAND)
            {
                setOffhand(wc_event.weapon);
            }
        }
        if(event instanceof CharacterEditEvent)
        {
            Log.e(TAG, "Receiving CharacterEditEvent");
            editMode = true;
            CharacterEditEvent ce_event = (CharacterEditEvent) event;
            if(ce_event.character != null) {
                character = ce_event.character;
                character.resetHealth();
                title = character.getName();
            }
            else
            {
                reset();
            }
            if(isViewAttached()) {
                updateView();
            }
        }
    }
}
