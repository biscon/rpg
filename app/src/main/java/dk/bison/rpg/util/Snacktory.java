package dk.bison.rpg.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by bison on 07/06/16.
 */
public class Snacktory {
    public static void showMessage(View attach, String message, int length) {
        Snackbar snack = Snackbar.make(attach, message, length);
        //snack.setAction(Translation.defaultSection.ok,listener);
        View view = snack.getView();
        view.setBackgroundResource(android.R.color.black);
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    public static void showError(View attach, String message) {
        Snackbar snack = Snackbar.make(attach, message, Snackbar.LENGTH_LONG);
        //snack.setAction(Translation.defaultSection.ok,listener);
        View view = snack.getView();
        view.setBackgroundColor(Color.RED);
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }
}
