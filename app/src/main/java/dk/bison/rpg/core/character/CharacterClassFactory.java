package dk.bison.rpg.core.character;

import android.util.Log;

import dk.bison.rpg.core.ai.AI;

/**
 * Created by bison on 16-08-2016.
 */
public class CharacterClassFactory {
    public static final String TAG = CharacterClassFactory.class.getSimpleName();

    public static CharacterClass makeClass(String classname)
    {
        try {
            String fullname = CharacterClassFactory.class.getPackage().getName() + "." + classname;
            Class<?> cls = Class.forName(fullname);
            Log.d(TAG, "Instantiating class = " + fullname);
            return (CharacterClass) cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Character class " + classname + " not found");
            return null;
        }
    }
}
