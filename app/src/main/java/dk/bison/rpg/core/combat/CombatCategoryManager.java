package dk.bison.rpg.core.combat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bison on 28-09-2016.
 */

public class CombatCategoryManager {
    public static final String TAG = CombatCategoryManager.class.getSimpleName();
    static CombatCategoryManager _i = null;
    Map<String, CombatCategory> categoryMap;
    private LoadTask loadTask;
    String fileName= "combat_text_categories.json";

    private CombatCategoryManager() {
        categoryMap = new HashMap<>();
    }

    public static CombatCategoryManager instance()
    {
        if(_i == null)
        {
            _i = new CombatCategoryManager();
        }
        return _i;
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

    public Map<String, CombatCategory> getCategoryMap() {
        return categoryMap;
    }

    public class LoadTask extends AsyncTask<Void, Void, Void> {
        public Context context;
        public Runnable listener;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                long then = System.currentTimeMillis();
                InputStream is = context.getAssets().open(fileName);
                DataInputStream dataIO = new DataInputStream(is);
                String strLine = null;
                StringBuffer content = new StringBuffer();
                do {
                    strLine = dataIO.readLine();
                    content.append(strLine);
                } while(strLine != null);

                //Log.e(TAG, "read content: " + content);
                JSONObject cats_json = new JSONObject(content.toString());
                Iterator<String> key_it = cats_json.keys();
                while(key_it.hasNext())
                {
                    String cat_name = key_it.next();
                    Log.e(TAG, "Parsing category: " + cat_name);
                    JSONObject cat_json = cats_json.getJSONObject(cat_name);
                    CombatCategory cat = new CombatCategory();
                    cat.name = cat_name;
                    if(cat_json.has("hit"))
                        cat.hit = getTexts(cat_json.getJSONArray("hit"));
                    if(cat_json.has("miss"))
                        cat.miss = getTexts(cat_json.getJSONArray("miss"));
                    if(cat_json.has("crit"))
                        cat.crit = getTexts(cat_json.getJSONArray("crit"));
                    if(cat_json.has("fail"))
                        cat.fail = getTexts(cat_json.getJSONArray("fail"));
                    categoryMap.put(cat_name, cat);
                }

                dataIO.close();
                is.close();
                long elapsed = System.currentTimeMillis() - then;
                Log.i(TAG, "Loading took " + elapsed + " ms");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Loading failed. File likely doesn't exist or JSON parsing error.");
                return null;
            }
        }

        private List<CombatText> getTexts(JSONArray cat_json) throws JSONException {
            ArrayList<CombatText> text_list = new ArrayList<>();
            for(int i = 0; i < cat_json.length(); i++)
            {
                JSONObject txt_json = cat_json.getJSONObject(i);
                CombatText ct = new CombatText();
                ct.text = txt_json.getString("text");
                if(txt_json.has("death_postfix"))
                    ct.deathPostfix = txt_json.getString("death_postfix");
                text_list.add(ct);
                Log.e(TAG, "Added combat text: " + ct.text);
            }
            return text_list;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(listener != null)
                listener.run();
        }
    }
}
