package com.gandgapps.metal_deterctor_treasure_hunt;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class PlotOfLand {

    private static final String TAG = "PlotOfLand" ;
    private int[] treasure;
    private int myX;
    private int myY;
    private PlotOfLand[] myNeighbours;
    private boolean active;
    private boolean activeField[];
    private boolean dugUp;
    private int[] myDetectorColor;
    private Bitmap myBitmap;
    private Bitmap myBitmapHole;
    private Context context;
    private SetUpDisplayObjects setUpDisplayObjects;
    private Bitmap[] bitmapTreasure;

    public PlotOfLand(Context context, SetUpDisplayObjects setUpDisplayObjects) {
        myDetectorColor = new int[4];
        for (int i: myDetectorColor) {
            myDetectorColor[i] = R.color.outOfRangeColor;
            dugUp = false;
            active = false;
            myX = 0;
            myY = 0;
            this.context = context;
            this.setUpDisplayObjects = setUpDisplayObjects;
        }
    }

    public int getMyDetectorColor(int frame) {
        return myDetectorColor[frame];
    }

    public void setMyDetectorColor(int myDetectorColor, boolean resetAndShift) {
        if (resetAndShift){
            this.myDetectorColor[3] = this.myDetectorColor[2];
            this.myDetectorColor[2] = this.myDetectorColor[1];
            this.myDetectorColor[1] = this.myDetectorColor[0];
        }
        this.myDetectorColor[0] = myDetectorColor;
    }

    public int getTreasure(int field) {
        int trCode;
        if (treasure == null) {
            trCode = -1;
        } else  {
            trCode = treasure[field];
        }
        return trCode;
    }

    public void setTreasure(int treasure, int field) {
        if (this.treasure == null){
            this.treasure = new int[setUpDisplayObjects.noOfFields];
            bitmapTreasure = new Bitmap[setUpDisplayObjects.noOfFields];
            for (int f = 0; f < setUpDisplayObjects.noOfFields; f++) {
                this.treasure[f] = -1;
            }
        }
        this.treasure[field] = treasure;
        if (this.treasure[field] >= 0){
            if (treasure < 3 ){
                bitmapTreasure[field] = setUpDisplayObjects.getBitmapHoleCoins();
            }
            else{
                bitmapTreasure[field] = setUpDisplayObjects.getBitmapBoot();
            }

        }
    }

    // has to have coordinates and a click suface (button?)

    public int getMyX() {
        return myX;
    }

    public void setMyX(int myX) {
        this.myX = myX;
    }

    public int getMyY() {
        return myY;
    }

    public void setMyY(int myY) {
        this.myY = myY;
    }

    public PlotOfLand[] getMyNeighbours() {
        return myNeighbours;
    }
    // has to identify its neighbours
    public void setMyNeighbours(PlotOfLand[] myNeighbours) {
        this.myNeighbours = myNeighbours;
    }

    // can be inactif

    public boolean isActive() {
        return active;
    }

    public boolean fieldActive(int field) {
        return activeField[field];
    }

    public void setActive(boolean active, int field) {
        if (!this.active) {
            myBitmap = setUpDisplayObjects.getBitmap1();
            myBitmapHole = setUpDisplayObjects.getBitmapHole();
            this.active = true;
            activeField = new boolean[setUpDisplayObjects.noOfFields];
        }
        activeField[field] = active;
    }


    // must change its image if inactiv or if dug up

    // can be dug up

    public void setDugUp(boolean dugUp) {
        this.dugUp = dugUp;
        if (dugUp) {
            Log.d(TAG, "hole true ");
        }
    }
    public void drawMe(Canvas canvas, Paint paint, int field){
        if (active) {
            if (activeField [field]){
                canvas.drawBitmap(myBitmap, myX, myY, paint);
                if (dugUp) {
                    if (myBitmapHole == null) {
                        Log.w(TAG, "drawMe: myBitmapHole is null", null);
                    }
                    else {
                        canvas.drawBitmap(myBitmapHole, myX + 5, myY + 5, paint);
                    }
                    if (treasure != null) {
                        if (treasure[field] >= 0) {
                            canvas.drawBitmap(bitmapTreasure[field], myX + 5, myY + 5, paint);
                        }
                    }
                }
            }
        }
    }
    public void drawMyDetectorImage(Canvas canvas, int frame, int deltaX, int deltaY, float fraction, int field){
        int myWidth = setUpDisplayObjects.getPlotWidth();
        int myHeight = setUpDisplayObjects.getPlotHeight();
        if (active) {
            if (activeField[field]) {
                Paint paint = new Paint();
                RectF oval = new RectF(myX*fraction + deltaX,
                        myY*fraction + deltaY,
                        myX*fraction + deltaX + myWidth*fraction,
                        myY*fraction + deltaY + myHeight*fraction);
                paint.setColor(context.getResources().getColor(myDetectorColor[frame]));
                canvas.drawOval(oval, paint);
            }
        }
    }
}
