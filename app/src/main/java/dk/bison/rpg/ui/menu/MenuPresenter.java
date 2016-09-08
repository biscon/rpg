package dk.bison.rpg.ui.menu;

import android.content.Context;

import dk.bison.rpg.mvp.BasePresenter;

/**
 * Created by bison on 19-08-2016.
 */
public class MenuPresenter extends BasePresenter<MenuMvpView> {
    public static final String TAG = MenuPresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(MenuMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


}
