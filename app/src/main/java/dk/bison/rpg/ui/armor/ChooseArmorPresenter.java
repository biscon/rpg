package dk.bison.rpg.ui.armor;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dk.bison.rpg.core.armor.ACComparator;
import dk.bison.rpg.core.armor.ArmorFactory;
import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.PresentationManager;

/**
 * Created by bison on 19-08-2016.
 */
public class ChooseArmorPresenter extends BasePresenter<ChooseArmorMvpView> {
    public static final String TAG = ChooseArmorPresenter.class.getSimpleName();
    List<ArmorTemplate> armorTemplates;
    int filter = -1;

    @Override
    public void onCreate(Context context) {
        armorTemplates = ArmorFactory.getTemplates();
        Collections.sort(armorTemplates, new ACComparator());
    }

    @Override
    public void attachView(ChooseArmorMvpView mvpView) {
        super.attachView(mvpView);
        getMvpView().showTemplates(armorTemplates);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void filterType(int type)
    {
        filter = type;
        Iterator<ArmorTemplate> it = armorTemplates.iterator();
        while(it.hasNext())
        {
            ArmorTemplate at = it.next();
            if(at.getType() != type)
                it.remove();
        }
        Collections.sort(armorTemplates, new ACComparator());
        if(isViewAttached())
            getMvpView().showTemplates(armorTemplates);
    }

    public void clearFilter()
    {
        filter = -1;
        armorTemplates = ArmorFactory.getTemplates();
        Collections.sort(armorTemplates, new ACComparator());
        if(isViewAttached())
            getMvpView().showTemplates(armorTemplates);
    }

    public void onArmorSelected(ArmorTemplate template)
    {
        Log.d(TAG, "Armor selected: " + template.toString());
        PresentationManager.instance().publishEvent(new ArmorChangeEvent(template));
        getMvpView().closeView();
    }
}
