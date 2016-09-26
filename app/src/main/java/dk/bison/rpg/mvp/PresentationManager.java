package dk.bison.rpg.mvp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dk.bison.rpg.App;

/**
 * Created by bison on 13-08-2016.
 * This is a singleton manager/factory class for holding and instantiating Presenters
 * The manager owns the presenters which are being lazily instantiated when a view
 * requests a reference trough presenter().
 * Since presenters live trough out the whole process lifespan (even if you exit the app and
 * restart it, if it haven't been claimed by the runtime), storing them centrally like
 * this makes it possible to iterate trough them all and reset their state.
 * In this case presenters will receive the onDestroy callback if they need to do
 * any housekeeping. This is useful if you for example: want to login as another user
 * and clear all state of the previous user and their data.
 */
public class PresentationManager {
    public static final String TAG = PresentationManager.class.getSimpleName();
    static PresentationManager instance;
    Map<Class, Presenter> presenters;

    private PresentationManager()
    {
        presenters = new HashMap<>();
    }

    public static PresentationManager instance()
    {
        if(instance == null)
        {
            instance = new PresentationManager();
        }
        return instance;
    }

    /**
     *  This returns a presenter of the given type (given by the cls parameter, pass a class obj).
     *  The weird return type is so you don't have to cast shit :)
     *
     *  If the presenter is already instantiated, it is returned, otherwise its instantiated and its
     *  onCreate() method is called.
     */
    public <T extends Presenter> T presenter(Context context, Class<T> cls)
    {
        if(presenters.containsKey(cls))
        {
            return cls.cast(presenters.get(cls));
        }
        try {
            T p = cls.newInstance();
            presenters.put(cls, p);
            p.onCreate(context);
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends Presenter> T findRunningPresenter(Class<T> cls)
    {
        if(presenters.containsKey(cls))
        {
            return cls.cast(presenters.get(cls));
        }
        return null;
    }

    public void removePresenter(Class cls)
    {
        if(presenters.containsKey(cls))
        {
            presenters.get(cls).onDestroy();
            presenters.remove(cls);
        }
    }

    /**
     * Goes trough all the presenters and remove them from the map, calling their onDestroy method
     * in the process.
     */
    public void clear()
    {
        for(Iterator<Map.Entry<Class, Presenter>> it = presenters.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<Class, Presenter> entry = it.next();
            entry.getValue().onDestroy();
            it.remove();
        }
    }

    public void publishEvent(Class<?> target_cls, MvpEvent event)
    {
        if(presenters.containsKey(target_cls))
        {
            presenters.get(target_cls).onEvent(event);
        }
    }

    /**
     * If we're not on the ui thread then post on the ui thread instead of just running.
     * Makes the event mechanism safe to call from other threads.. somewhat...
     * @param event
     */
    public void publishEvent(final MvpEvent event)
    {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            for(Iterator<Map.Entry<Class, Presenter>> it = presenters.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry<Class, Presenter> entry = it.next();
                entry.getValue().onEvent(event);
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //Log.d("UI thread", "I am the UI thread");
                    for(Iterator<Map.Entry<Class, Presenter>> it = presenters.entrySet().iterator(); it.hasNext(); )
                    {
                        Map.Entry<Class, Presenter> entry = it.next();
                        entry.getValue().onEvent(event);
                    }
                }
            });
        }
    }
}
