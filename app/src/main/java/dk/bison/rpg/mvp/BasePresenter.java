package dk.bison.rpg.mvp;

import android.content.Context;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class BasePresenter<T extends MvpView> implements Presenter<T> {

    private T mMvpView;
    protected Class<?> presenterClass;

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
        PresentationManager.instance().deliverPendingEventsOnAttach(presenterClass);
    }

    @Override
    public void detachView() {
        mMvpView = null;
    }

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onEvent(MvpEvent event) {

    }

    @Override
    public void setPresenterClass(Class<?> cls) {
        presenterClass = cls;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public T getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}