package dk.bison.rpg.ui.encounter.combat_view;

import android.animation.TimeInterpolator;

/**
 * Created by bison on 06-11-2016.
 */

public abstract class CameraEffect {
    public boolean done = false;
    public float camX = 0;
    public float camY = 0;
    public float camW = 512;
    public float camH = 512;
    public float camMaxX;
    public float camMaxY;
    public TimeInterpolator interpolator;


    public abstract void update(double dt);

    public void centerCamAt(float x, float y)
    {

        float nx = x - (camW/2);
        float ny = y - (camH/2);
        if(nx > camMaxX-camW)
            nx = camMaxX-camW;
        if(nx < 0)
            nx = 0;
        if(ny > camMaxY-camH)
            ny = camMaxY-camH;
        if(ny < 0)
            ny = 0;
        camX = nx;
        camY = ny;
    }

    public CameraEffect(float camX, float camY, float camW, float camH, float camMaxX, float camMaxY) {
        this.camX = camX;
        this.camY = camY;
        this.camW = camW;
        this.camH = camH;
        this.camMaxX = camMaxX;
        this.camMaxY = camMaxY;
    }
}
