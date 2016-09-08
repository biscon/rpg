package dk.bison.rpg.core.character;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CharacterManager {
    public static final String TAG = CharacterManager.class.getSimpleName();
    static CharacterManager instance;
    List<Character> characters;
    String fileName = "characters.json";
    private SaveTask saveTask;
    private LoadTask loadTask;

    private CharacterManager()
    {
        characters = new ArrayList<>();
    }

    public static CharacterManager instance()
    {
        if(instance == null)
        {
            instance = new CharacterManager();
        }
        return instance;
    }

    public void add(Character c)
    {
        characters.add(c);
    }

    public void remove(Character c)
    {
        characters.remove(c);
    }

    public void load(Context c)
    {
        load(c, null);
    }

    public void load(Context c, Runnable done_handler)
    {
        if(loadTask != null)
        {
            //NLog.e(TAG, "Cancelling already running savetask");
            loadTask.cancel(true);
            loadTask = null;
        }
        loadTask = new LoadTask();
        loadTask.context = c;
        loadTask.listener = done_handler;
        loadTask.execute();
    }

    public void save(Context c)
    {
        save(c, null);
    }

    public void save(Context c, Runnable done_handler)
    {
        if(saveTask != null)
        {
            //NLog.e(TAG, "Cancelling already running savetask");
            saveTask.cancel(true);
            saveTask = null;
        }
        saveTask = new SaveTask();
        saveTask.context = c;
        saveTask.listener = done_handler;
        saveTask.execute();
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public class SaveTask extends AsyncTask<Void, Void, Void> {
        public Context context;
        public Runnable listener;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                long then = System.currentTimeMillis();
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                PrintStream os = new PrintStream(fos);
                JSONArray array = new JSONArray();
                for(Character c : characters)
                {
                    array.put(c.toJson());
                }
                os.append(array.toString(2));
                os.close();
                fos.close();
                long elapsed = System.currentTimeMillis() - then;
                Log.i(TAG, "Saving took " + elapsed + " ms");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            if(listener != null)
                listener.run();
        }
    }


    public class LoadTask extends AsyncTask<Void, Void, Void> {
        public Context context;
        public Runnable listener;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                long then = System.currentTimeMillis();
                FileInputStream fis = context.openFileInput(fileName);

                DataInputStream dataIO = new DataInputStream(fis);
                String strLine = null;
                StringBuffer content = new StringBuffer();
                do {
                    strLine = dataIO.readLine();
                    content.append(strLine);
                } while(strLine != null);
                JSONArray array = new JSONArray(content.toString());
                characters.clear();
                for(int i = 0; i < array.length(); i++)
                {
                    JSONObject c = array.getJSONObject(i);
                    Character chr = Character.fromJson(c);
                    characters.add(chr);
                    Log.d(TAG, "Loading character " + chr.getName());
                }
                dataIO.close();
                fis.close();
                long elapsed = System.currentTimeMillis() - then;
                Log.i(TAG, "Loading took " + elapsed + " ms");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Loading failed. File likely doesn't exist.");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            if(listener != null)
                listener.run();
        }
    }
}
