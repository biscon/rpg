package dk.bison.rpg;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by bison on 22-08-2016.
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * Returns the root contentview of this activity
     */
    public View getContentView() {
        return findViewById(android.R.id.content);
    }
}
