package dk.bison.rpg.ui.encounter.combat_log;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class CombatLogPresenter extends BasePresenter<CombatLogMvpView> {
    public static final String TAG = CombatLogPresenter.class.getSimpleName();
    private ConcurrentLinkedQueue<CombatLogMessage> messageQueue;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    public void onCreate(Context context) {
        messageQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void attachView(CombatLogMvpView mvpView) {
        super.attachView(mvpView);
        Log.e(TAG, "attachView");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //Log.i(TAG, "timer task running");
                if(!messageQueue.isEmpty())
                {
                    if(isViewAttached())
                        getMvpView().postMessage(messageQueue.remove());
                }
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 500);
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        timer.cancel();
        timer.purge();
        super.detachView();
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof CombatLogMessage)
        {
            messageQueue.add((CombatLogMessage) event);
            /*
            if(isViewAttached())
                getMvpView().postMessage((CombatLogMessage) event);
                */
        }
    }
}
