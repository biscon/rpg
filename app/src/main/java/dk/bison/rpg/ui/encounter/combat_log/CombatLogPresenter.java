package dk.bison.rpg.ui.encounter.combat_log;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerInputRequestEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerInputResponseEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class CombatLogPresenter extends BasePresenter<CombatLogMvpView> implements Runnable {
    public static final String TAG = CombatLogPresenter.class.getSimpleName();
    private LinkedBlockingQueue<CombatLogMessage> messageQueue;
    Thread thread;
    boolean shouldTerminate = false;

    @Override
    public void onCreate(Context context) {
        messageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void attachView(CombatLogMvpView mvpView) {
        super.attachView(mvpView);
        Log.e(TAG, "attachView");
        shouldTerminate = false;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        shouldTerminate = true;
        messageQueue.add(new CombatLogMessage());
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.detachView();
    }

    public void addDivider()
    {
        if(isViewAttached())
            getMvpView().addDivider();
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
        if(event instanceof PlayerInputRequestEvent)
        {
            if(isViewAttached())
                getMvpView().hide();
        }
        if(event instanceof PlayerInputResponseEvent)
        {
            if(isViewAttached())
                getMvpView().show();
        }
    }

    static long lastMsgTime = 0;
    @Override
    public void run() {
        Log.e(TAG, "CombatLog thread started, going to sleep waiting for messages... zZzz");
        while(!shouldTerminate) {
            try {
                CombatLogMessage msg = messageQueue.take();
                if(shouldTerminate) {
                    break;
                }
                if(isViewAttached()) {
                    getMvpView().postMessage(msg);
                    // the combat round message is just to show next round ui at the right time, dont wait for it
                    // just show the fucker
                    if(msg.isRoundDone())
                    {
                        lastMsgTime = System.currentTimeMillis();
                        continue;
                    }
                }
                if(lastMsgTime > 0)
                {
                    long diff = System.currentTimeMillis() - lastMsgTime;
                    if(diff < 900) // if less than a second has passed since last msg, sleep till a second has passed, roughly
                    {
                        Log.e(TAG, "Sleeping for " + String.valueOf(1000-diff) + " ms to delay messages");
                        Thread.sleep(1000-diff);
                    }
                }
                lastMsgTime = System.currentTimeMillis();
                //Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "CombatLog thread terminating.");
    }
}
