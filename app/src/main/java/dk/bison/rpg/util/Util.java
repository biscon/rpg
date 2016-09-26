package dk.bison.rpg.util;

import android.util.Log;

/**
 * Created by bison on 26-09-2016.
 */

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    public static double reMapDouble(double oMin, double oMax, double nMin, double nMax, double x){
//range check
        if( oMin == oMax) {
            Log.i(TAG, "Warning: Zero input range");
            return -1;    }

        if( nMin == nMax){
            Log.i(TAG, "Warning: Zero output range");
            return -1;        }

//check reversed input range
        boolean reverseInput = false;
        double oldMin = Math.min(oMin, oMax );
        double oldMax = Math.max(oMin, oMax );
        if (oldMin == oMin)
            reverseInput = true;

//check reversed output range
        boolean reverseOutput = false;
        double newMin = Math.min(nMin, nMax );
        double newMax = Math.max(nMin, nMax );
        if (newMin == nMin)
            reverseOutput = true;

        double portion = (x-oldMin)*(newMax-newMin)/(oldMax-oldMin);
        if (reverseInput)
            portion = (oldMax-x)*(newMax-newMin)/(oldMax-oldMin);

        double result = portion + newMin;
        if (reverseOutput)
            result = newMax - portion;

        return result;
    }
}
