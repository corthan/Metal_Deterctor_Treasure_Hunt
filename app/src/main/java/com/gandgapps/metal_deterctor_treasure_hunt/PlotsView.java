package com.gandgapps.metal_deterctor_treasure_hunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.nfc.Tag;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.sql.Time;

class PlotsView extends SurfaceView implements Runnable{

    public Context context;

    private static final String TAG = "surface view";
    private PlotOfLand[][] plotsToDisplay;
    private int screenX;
    private  int screenY;
    private int[] shiftXY;
    private SetUpDisplayObjects setUpDisplayObjects;

    private SurfaceHolder ourHolder;
    private Canvas canvas;
    private Paint paint;
    private RectF rect;
    private int field;
    private MetalDetector metalDetector;

    private Thread thread;
    private boolean playing;

    public PlotsView(Context context, SetUpDisplayObjects setUpDisplayObjects) {
        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);
        this.setUpDisplayObjects = setUpDisplayObjects;
        plotsToDisplay = setUpDisplayObjects.getLevelPlotArray();
        screenX = setUpDisplayObjects.getPlotDisplayWidth();
        screenY = setUpDisplayObjects.getPlotDisplayHeight();
        this.shiftXY = setUpDisplayObjects.getShiftXY();
        field = 0; // update when find a way to ID them todo update
        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();
        metalDetector = new MetalDetector(context, setUpDisplayObjects, field);
        playing = true;
    }

    public void draw() {
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Log.d(TAG, "holder valid");
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 225, 225, 225));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255, 105, 249, 121));
            rect = new RectF(shiftXY[0],shiftXY[1],screenX, screenY);
            canvas.drawRect(rect, paint);
            canvas.drawText("Test", 100,0, paint);
            canvas.drawCircle(screenX/2, screenY + screenY/2 + shiftXY[1], screenX/2, paint);
            int padding = 1;
            for (int i = 0; i < plotsToDisplay[field].length; i ++) {
                plotsToDisplay[field][i].drawMe(canvas, paint, field);
            }
            int frame = 0; // todo get frame from touch input (here or in metaldetector?)
            metalDetector.drawMD(canvas, plotsToDisplay[field], frame, screenX, screenY, shiftXY);
            ourHolder.unlockCanvasAndPost(canvas);
        } else {
            Log.d(TAG, "holder not valid");
        }

    }

    @Override
    public void run() {
        while (playing) {
            draw();
            //try {
             //   Thread.sleep(50);
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }

    }

    public void resume() {
        Log.d(TAG, "on resume evoked");
        thread = new Thread(this);
        thread.start();
        playing = true;
    }

    public void pause() {
        playing = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "pause: done");
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        Boolean paused;
        float touchX;
        float touchY;
        int myX;
        int myY;
        int halfMyHeight;
        int halfMyWidth;
        int treasureFound = -1; // negative number if nothing

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "action down ");
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "action up ");
                touchX = motionEvent.getX();
                touchY = motionEvent.getY();
                halfMyHeight = setUpDisplayObjects.getPlotHeight() / 2;
                halfMyWidth = setUpDisplayObjects.getPlotWidth()  / 2;
                for (int i = 0; i < plotsToDisplay[field].length; i++){
                    myX = plotsToDisplay[field][i].getMyX();
                    myY = plotsToDisplay[field][i].getMyY();
                    if ((Math.abs(touchX - myX - halfMyWidth) < halfMyWidth) &&
                            (Math.abs(touchY - myY - halfMyHeight) < halfMyHeight)) {
                        plotsToDisplay[field][i].setDugUp(true);
                        metalDetector.setupDetectorZones(plotsToDisplay[field], i);
                        break;
                    }
                }
                treasureFound = metalDetector.treasureFound();
                // todo do something with the treasure
        }
        return true;
    }
}


