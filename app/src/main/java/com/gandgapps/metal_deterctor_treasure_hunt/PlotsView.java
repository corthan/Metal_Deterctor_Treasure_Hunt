package com.gandgapps.metal_deterctor_treasure_hunt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.sql.Time;

class PlotsView extends SurfaceView implements Runnable{

    private static final String TAG = "surface view";
    private PlotOfLand[][] plotsToDisplay;
    private int screenX;
    private  int screenY;

    private SurfaceHolder ourHolder;
    private Canvas canvas;
    private Paint paint;
    private RectF rect;

    private Thread thread;

    public PlotsView(Context context, int x, int y, PlotOfLand[][] plots) {
        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);
        plotsToDisplay = plots;
        screenX = x;
        screenY = y;
        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

    }

    public void draw() {
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            Log.d(TAG, "holder valid");
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            canvas.drawText("Test", 100,50, paint);
            int padding = 1;
            for (int i = 0; i < plotsToDisplay[0].length; i ++) {
                rect = new RectF(plotsToDisplay[0][i].getMyX() + padding,
                        plotsToDisplay[0][i].getMyY() + padding,
                        plotsToDisplay[0][i].getMyX() + plotsToDisplay[0][i].getMyWidth() - padding,
                        plotsToDisplay[0][i].getMyY() + plotsToDisplay[0][i].getMyHeight() - padding);
                canvas.drawRect(rect, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        } else {
            Log.d(TAG, "holder not valid");
        }

    }

    @Override
    public void run() {
        while (true) {
            draw();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void resume() {
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
