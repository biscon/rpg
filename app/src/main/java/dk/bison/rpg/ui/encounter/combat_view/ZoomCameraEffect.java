package dk.bison.rpg.ui.encounter.combat_view;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;

/**
 * Created by bison on 06-11-2016.
 */

public class ZoomCameraEffect extends CameraEffect {
    public static final String TAG = ZoomCameraEffect.class.getSimpleName();
    float progress = 0;
    float startCamSize;
    float endCamSize;
    float targetX;
    float targetY;
    double duration = 10;
    double timer;

    public ZoomCameraEffect(float camX, float camY, float camW, float camH, float camMaxX, float camMaxY, float x, float y, float start_cam, float end_cam) {
        super(camX, camY, camW, camH, camMaxX, camMaxY);
        targetX = x;
        targetY = y;
        startCamSize = start_cam;
        endCamSize = end_cam;
        timer = 0;
        interpolator = new FastOutSlowInInterpolator();
    }

    @Override
    public void update(double dt) {
        if(done)
            return;
        timer += dt;
        if(timer > duration)
        {
            done = true;
            camW = endCamSize;
            camH = endCamSize;
            timer = 0;
            centerCamAt(targetX, targetY);
            Log.e(TAG, "Anim done");
            Log.e(TAG, "progress = " + progress + " timer = " + timer);
            return;
        }

        progress = (float) (timer/duration);
        float val = interpolator.getInterpolation(progress);
        camW = lerp(startCamSize, endCamSize, val);
        camH = lerp(startCamSize, endCamSize, val);


        //Log.e(TAG, "progress = " + progress + " timer = " + timer);
        centerCamAt(targetX, targetY);
    }

    public static float lerp(float a, float b, float f)
    {
        return (a * (1.0f - f)) + (b * f);
    }
}
