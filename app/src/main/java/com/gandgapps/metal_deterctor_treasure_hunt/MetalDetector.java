package com.gandgapps.metal_deterctor_treasure_hunt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class MetalDetector {

    public int powerLevel;
    public int powerSetting;
    public int[] powerDisplay;
    Context context;
    private boolean foundTresaure = false;

    private PlotOfLand[] plotArray;
    private SetUpDisplayObjects setUpDisplayObjects;
    private int field = 0;
    private PlotOfLand correctPlot;


    public MetalDetector(Context context,SetUpDisplayObjects setUpDisplayObjects, int field) {
        this.plotArray = setUpDisplayObjects.getLevelPlotArray()[field];
        powerLevel = 300; // todo setup the possibility of change
        powerSetting = 2; // todo get/set somehow
        powerDisplay = new int[2];
        powerDisplay = setPowerDisplay(powerSetting);
        this.context = context;
        this.field = field;
        this.setUpDisplayObjects = setUpDisplayObjects;
    }

    public void setupDetectorZones(PlotOfLand[] plots,int tested) {
        int farLayers = powerDisplay[1];
        int nearLayers = powerDisplay[0];
        // initialise all plots for new color
        for (PlotOfLand p: plots){
            p.setMyDetectorColor(R.color.outOfRangeColor, true);
        }
        recursiveColorSetting(plots[tested], farLayers, nearLayers);
        plots[tested].setMyDetectorColor(R.color.alreadyGuessedColor, false);
    }

    private void recursiveColorSetting(PlotOfLand plot, int farLayers, int nearLayers){
        int newFarLayers = farLayers - 1;
        int newNearLayers = nearLayers - 1;
        if (farLayers >= 0){
            for (PlotOfLand newPlot: plot.getMyNeighbours()){
                if (newPlot != null){
                    if (newPlot.isActive()) {
                        recursiveColorSetting(newPlot, newFarLayers, newNearLayers);
                    }
                }
            }
            if (nearLayers >= 0){
                plot.setMyDetectorColor(R.color.nearColor, false);
            }
            else if (plot.getMyDetectorColor(0) == R.color.outOfRangeColor){
                plot.setMyDetectorColor(R.color.farColor, false);
            }
        }
    }

    public int[] setPowerDisplay(int pSetting) {

        powerDisplay = new int[2];
        switch(pSetting){
            case (0): //  machine out of power
                powerDisplay[0] = 0;
                powerDisplay[1] = 0;
                powerLevel = powerLevel;
                break;
            case (1): //  machine low power only seen near neighbours
                powerDisplay[0] = 1;
                powerDisplay[1] = 1;
                powerLevel = powerLevel - 1;
                break;
            case (2): //  machine medium power
                powerDisplay[0] = 1;
                powerDisplay[1] = 3;
                powerLevel = powerLevel - 2;
                break;
            case (3): //  machine high power
                powerDisplay[0] = 2;
                powerDisplay[1] = 5;
                powerLevel = powerLevel - 3;
                break;
            case (4): //  machine saturated
                powerDisplay[0] = 3;
                powerDisplay[1] = 7;
                powerLevel = powerLevel - 4;
                break;
        }
        powerSetting = pSetting;
        return powerDisplay;
    }

    public int treasureColor(ArrayList<PlotOfLand> treasureList) { //must be called after setdetectorcolor

        int bestColor = R.color.outOfRangeColor;
        int testColor;
        for (PlotOfLand tPlot: treasureList){
            testColor = tPlot.getMyDetectorColor(0);
            if (testColor == R.color.nearColor) {
                if (bestColor != R.color.alreadyGuessedColor) {
                    bestColor = R.color.nearColor;
                }
            }
            else if (testColor == R.color.farColor) {
                if (bestColor == R.color.outOfRangeColor) {
                    bestColor = R.color.farColor;
                }
            }
            else if (testColor == R.color.alreadyGuessedColor) {
                bestColor = R.color.alreadyGuessedColor;
                foundTresaure = true;
                correctPlot = tPlot;
                // display find?
            }
        }
        return bestColor;
    }

    public void drawMD(Canvas canvas, PlotOfLand[] plotArray, int frame, int screenX, int screenY, int[] shiftXY) {

        int totalWidth = canvas.getWidth();
        int totalHeight = canvas.getHeight();
        int widthMD;
        int heightMD;
        int shiftMDX;
        int shiftMDY;
        int treasureColor;
        float fraction;
        Paint paint;
        RectF oval;
        paint = new Paint();
        if (totalWidth < totalHeight) {
            widthMD = totalWidth;
            heightMD = totalHeight - screenY - shiftXY[1];
            shiftMDX = 0;
            shiftMDY = screenY + shiftXY[1];
        }
        else {
            widthMD = totalWidth - screenX - shiftXY[0];
            heightMD = totalHeight;
            shiftMDX = screenX + shiftXY[0];
            shiftMDY = 0;
        }
        oval = new RectF(shiftMDX, shiftMDY,
                shiftMDX + Math.abs(widthMD - heightMD),
                shiftMDY + Math.abs(widthMD - heightMD));
        ArrayList<PlotOfLand> treasureArray;
        treasureArray = setUpDisplayObjects.getTreasureList()[field];
        treasureColor = treasureColor(treasureArray);
        paint.setColor(context.getResources().getColor(treasureColor));
        canvas.drawOval(oval, paint);
        if (widthMD < heightMD) {
            shiftMDY =  shiftMDY + (heightMD - widthMD) / 2;
            heightMD = widthMD; //todo put other MD display here
        }
        else {
            shiftMDX = shiftMDX + (widthMD - heightMD) / 2;
            widthMD = heightMD; // todo put other MD display here
        }
        paint.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        oval = new RectF(shiftMDX, shiftMDY, shiftMDX + widthMD, shiftMDY + heightMD);
        canvas.drawOval(oval, paint);
        fraction = (float) widthMD/screenX;
        for (PlotOfLand pl : plotArray){
            pl.drawMyDetectorImage(canvas, frame, shiftMDX, shiftMDY, fraction, field);
        }

    }

    public int treasureFound() {

        int treasureFind = -1;
        if (foundTresaure) {
            treasureFind = correctPlot.getTreasure(field);
            setUpDisplayObjects.modifyTreasureList(correctPlot, field);
            correctPlot = null;
            foundTresaure = false;
        }
        return treasureFind;
    }
}
