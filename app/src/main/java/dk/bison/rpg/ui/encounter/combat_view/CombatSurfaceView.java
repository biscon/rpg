package dk.bison.rpg.ui.encounter.combat_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import dk.bison.rpg.mvp.PresentationManager;

/**
 * Created by bison on 30-10-2016.
 */

public class CombatSurfaceView extends SurfaceView implements Runnable, CombatSurfaceMvpView {
    public static final String TAG = CombatSurfaceView.class.getSimpleName();
    CombatSurfacePresenter presenter;

    Thread renderThread = null;
    SurfaceHolder surfaceHolder;
    volatile boolean rendering;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;
    private long lastFrameTime;
    double dT;

    Bitmap bgBitmap;
    RectF fRect = new RectF();
    Rect rect = new Rect();
    RectF screenRect = new RectF();

    Rect camRect = new Rect();
    RectF bgRect = new RectF();
    RectF viewportRect = new RectF();

    float camX = 0;
    float camY = 0;
    float camW = 512;
    float camH = 512;
    float camMaxX = 1024;
    float camMaxY = 512;
    float camPanX = 0;

    float smallCamSize = 64;
    float mediumCamSize = 256;
    float largeCamSize = 512;
    Matrix matrix;
    AnimationStrip strip;
    CameraEffect currentEffect;



    public CombatSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CombatSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        presenter = PresentationManager.instance().presenter(context, CombatSurfacePresenter.class);
        // Initialize ourHolder and paint objects
        surfaceHolder = getHolder();
        paint = new Paint();
        try {
            InputStream ims = context.getAssets().open("desert.png");
            bgBitmap = BitmapFactory.decodeStream(ims);
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Background bitmap is " + bgBitmap.getWidth() + "x" + bgBitmap.getHeight());
        bgRect.set(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());
        matrix = new Matrix();

        strip = AnimationStrip.loadStrip(context, "walk_anim_32x64.png", 32, 64);

        //centerCamAt(512,256);
        //currentEffect = new ZoomCameraEffect(camX, camY, camW, camH, camMaxX, camMaxY, 115, 370, largeCamSize, smallCamSize);
        currentEffect = new ZoomPanCameraEffect(camX, camY, camW, camH, camMaxX, camMaxY, 115, 370, 917, 333, smallCamSize, largeCamSize);
    }



    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (rendering) {

            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long diff = now - lastTime;
            if(diff == 0)
            {
                Log.e(TAG, "Fuck diff is zero!!!!");
            }
            lastTime = now;
            dT = (double) diff / 1000000000;
            fps = 1000000000 / diff;


            // Update the frame
            update();
            // Draw the frame
            draw();
        }
    }

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

    public void update() {

        //camPanX = lerp(0, 1024, progress);

        //Log.e(TAG, "camW = " + camW + "camH = " + camH + " dT=" + dT);

        //camX = camPanX;
        strip.update(dT);
        if(currentEffect != null) {
            currentEffect.update(dT);
            camX = currentEffect.camX;
            camY = currentEffect.camY;
            camW = currentEffect.camW;
            camH = currentEffect.camH;
            //Log.e(TAG, "camW = " + camW + "camH = " + camH + " dT=" + dT);
        }
        //centerCamAt(camPanX,256);
    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (surfaceHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            viewportRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
            camRect.set((int) camX, (int) camY, (int) (camX + camW), (int) (camY + camH));
            canvas.drawBitmap(bgBitmap, camRect, viewportRect, null);

            matrix.reset();
            float x_scale = (float) canvas.getWidth() / camW;
            float y_scale = (float) canvas.getHeight() / camH;

            matrix.setTranslate(-camX, -camY);
            matrix.postScale(x_scale, y_scale);

            int chr_x = 100;
            int chr_y = 350;
            fRect.set(chr_x, chr_y, chr_x + strip.frameWidth, chr_y + strip.frameHeight);
            matrix.mapRect(fRect);
            canvas.drawBitmap(strip.sheet, strip.frameRect, fRect, null);


            chr_x = 900;
            chr_y = 300;
            fRect.set(chr_x, chr_y, chr_x + strip.frameWidth, chr_y + strip.frameHeight);
            matrix.mapRect(fRect);
            canvas.drawBitmap(strip.sheet, strip.frameRect, fRect, null);

            // Draw the background color
            //canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  249, 129, 0));

            // Make the text a bit bigger
            paint.setTextSize(45);

            // Display the current fps on the screen
            canvas.drawText(String.format(Locale.US, "FPS: %s %dx%d dT=%.2f", fps, canvas.getWidth(), canvas.getHeight(), dT), 20, 40, paint);

            // Draw bob at bobXPosition, 200 pixels
            //canvas.drawBitmap(bitmapBob, bobXPosition, 200, paint);

            // New drawing code goes here

            // Draw everything to the screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    public void pause() {
        rendering = false;
        try {
            renderThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "joining thread");
        }

    }

    public void resume() {
        rendering = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    private static class Dimension
    {
        public int width;
        public int height;

        public Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static float lerp(float a, float b, float f)
    {
        return (a * (1.0f - f)) + (b * f);
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        if (original_width < bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        if (new_height < bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

}
