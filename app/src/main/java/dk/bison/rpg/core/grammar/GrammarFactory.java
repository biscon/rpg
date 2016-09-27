package dk.bison.rpg.core.grammar;

import android.util.Log;

import dk.bison.rpg.core.ai.AI;

/**
 * Created by bison on 16-08-2016.
 */
public class GrammarFactory {
    public static final String TAG = GrammarFactory.class.getSimpleName();

    public static Grammar make(String classname)
    {
        try {
            String fullname = GrammarFactory.class.getPackage().getName() + "." + classname;
            Class<?> grammar_cls = Class.forName(fullname);
            Log.d(TAG, "Instantiating Grammar = " + fullname);
            return (Grammar) grammar_cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Grammar class " + classname + " not found");
            return null;
        }
    }
}
