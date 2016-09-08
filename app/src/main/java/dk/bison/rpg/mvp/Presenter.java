package dk.bison.rpg.mvp;

import android.content.Context;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {
    /**
     * This is called by the view (activity, fragment, whatever) when its created or resumed
     * from the background.
     * @param mvpView
     */
    void attachView(V mvpView);

    /**
     * Called from the view whenever its going away / being destroyed
     */
    void detachView();

    /**
     * This is called by the PresentationManager when the presenter is first instantiated
     * @param context
     */
    void onCreate(Context context);

    /**
     * Called by the PresentationManager if the presenter is removed, this could happen as the
     * result of a user change etc.
     */
    void onDestroy();

    void onEvent(MvpEvent event);
}
