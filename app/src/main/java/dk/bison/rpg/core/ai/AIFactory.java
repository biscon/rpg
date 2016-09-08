package dk.bison.rpg.core.ai;

import android.util.Log;

/**
 * Created by bison on 16-08-2016.
 */
public class AIFactory {
    public static final String TAG = AIFactory.class.getSimpleName();

    public static AI makeAI(String classname)
    {
        try {
            String fullname = AIFactory.class.getPackage().getName() + "." + classname;
            Class<?> ai_cls = Class.forName(fullname);
            Log.d(TAG, "Instantiating AI = " + fullname);
            return (AI) ai_cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "AI class " + classname + " not found");
            return null;
        }
    }
}
