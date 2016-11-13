package dk.bison.rpg.ui.encounter.combat_view;

import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;

/**
 * Created by bison on 06-11-2016.
 */

public class ZoomPanCameraEffect extends CameraEffect {
    public static final String TAG = ZoomPanCameraEffect.class.getSimpleName();
    float progress = 0;
    float startCamSize;
    float endCamSize;
    float targetX;
    float targetY;
    float startX;
    float startY;
    double duration = 15;
    double timer;
    CycleInterpolator zoomInterpolator;

    public ZoomPanCameraEffect(float camX, float camY, float camW, float camH, float camMaxX, float camMaxY, float sx, float sy, float x, float y, float start_cam, float end_cam) {
        super(camX, camY, camW, camH, camMaxX, camMaxY);
        targetX = x;
        targetY = y;
        startX = sx;
        startY = sy;
        startCamSize = start_cam;
        endCamSize = end_cam;
        timer = 0;
        interpolator = new AccelerateDecelerateInterpolator();
        //interpolator = new AccelerateInterpolator();
        //interpolator = new FastOutSlowInInterpolator();
        zoomInterpolator = new CycleInterpolator(.5f);
    }

    @Override
    public void update(double dt) {
        if(done)
            return;
        timer += dt;
        if(timer > duration)
        {
            done = true;
            /*
            camW = endCamSize;
            camH = endCamSize;
            */
            timer = 0;
            //centerCamAt(targetX, targetY);
            Log.e(TAG, "Anim done");
            Log.e(TAG, "progress = " + progress + " timer = " + timer);
            return;
        }

        progress = (float) (timer/duration);
        float val = interpolator.getInterpolation(progress);
        float zval = zoomInterpolator.getInterpolation(progress);
        camW = lerp(startCamSize, endCamSize, zval);
        camH = lerp(startCamSize, endCamSize, zval);
        float tx = lerp(startX, targetX, val);
        float ty = lerp(startY, targetY, val);


        //Log.e(TAG, "progress = " + progress + " timer = " + timer);
        centerCamAt(tx, ty);
    }

    public static float lerp(float a, float b, float f)
    {
        return (a * (1.0f - f)) + (b * f);
    }
}
