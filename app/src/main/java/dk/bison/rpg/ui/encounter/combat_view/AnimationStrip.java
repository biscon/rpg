package dk.bison.rpg.ui.encounter.combat_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bison on 06-11-2016.
 */

public class AnimationStrip {
    public static final String TAG = AnimationStrip.class.getSimpleName();
    public Bitmap sheet;
    public int frameWidth;
    public int frameHeight;
    public int currentFrame;
    public int noFrames;
    public Rect frameRect = new Rect();
    float frameDelay = 0.25f;
    float timer;

    public static AnimationStrip loadStrip(Context context, String filename, int fw, int fh)
    {
        AnimationStrip strip = new AnimationStrip();
        try {
            InputStream ims = context.getAssets().open(filename);
            strip.sheet = BitmapFactory.decodeStream(ims);
            strip.frameWidth = fw;
            strip.frameHeight = fh;
            strip.noFrames = strip.sheet.getWidth() / fw;
            strip.setCurrentFrame(0);
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return strip;
    }

    public void setCurrentFrame(int i)
    {
        if(i > noFrames-1)
            return;
        if(i < 0)
            return;
        //Log.e(TAG, "Setting current frame to " + i);
        currentFrame = i;
        frameRect.left = i * frameWidth;
        frameRect.top = 0;
        frameRect.right = i * frameWidth + frameWidth;
        frameRect.bottom = frameHeight;
    }

    public void update(double dt)
    {
        timer += dt;
        if(timer >= frameDelay)
        {
            int nf = currentFrame + 1;
            if(nf > noFrames-1)
                nf = 0;
            setCurrentFrame(nf);
            timer = 0;
        }
    }
}
