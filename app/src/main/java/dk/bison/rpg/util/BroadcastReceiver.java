package dk.bison.rpg.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by bison on 15/03/16.
 */
public class BroadcastReceiver {
    public final static String TAG = BroadcastReceiver.class.getSimpleName();
    private IntentFilter filter;
    private OnIntentListener listener;

    public BroadcastReceiver() {
        filter = new IntentFilter();
    }

    private android.content.BroadcastReceiver messageReceiver = new android.content.BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "Receiving broadcast, action = " + action);
            if(listener != null)
                listener.onIntent(intent);
        }
    };

    public BroadcastReceiver addFilter(String action)
    {
        filter.addAction(action);
        return this;
    }

    public BroadcastReceiver setListener(OnIntentListener listener)
    {
        this.listener = listener;
        return this;
    }

    public BroadcastReceiver register(Context c)
    {
        Log.d(TAG, "Registering receiver.");
        LocalBroadcastManager.getInstance(c).registerReceiver(messageReceiver, filter);
        return this;
    }

    public BroadcastReceiver deregister(Context c)
    {
        Log.d(TAG, "Deregistering receiver.");
        LocalBroadcastManager.getInstance(c).unregisterReceiver(messageReceiver);
        return this;
    }

    public interface OnIntentListener
    {
        void onIntent(Intent intent);
    }

    public static void broadcast(Context c, Intent intent)
    {
        Log.d(TAG, "Broadcasting action " + intent.getAction());
        LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
    }
}
