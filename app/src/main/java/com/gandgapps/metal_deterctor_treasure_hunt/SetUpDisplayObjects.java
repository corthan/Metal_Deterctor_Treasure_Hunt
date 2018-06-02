package com.gandgapps.metal_deterctor_treasure_hunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

class SetUpDisplayObjects {

    private static final String TAG = "none";
    public int gridPlots = 5;
    public int noOfFields = 3;
    public int level = 0;
    private int plotDisplayWidth = 500;
    private int plotDisplayHeight = 500;
    private int maxTreasuresPerField = 5;
    private int[] shiftXY;
    private Context context;
    private PlotOfLand[][] levelPlotArray;
    private ArrayList<PlotOfLand>[] treasureList;
    private Bitmap bitmapHole;
    private Bitmap bitmap1;
    private int plotWidth;
    private int plotHeight;
    private Bitmap bitmapHoleCoins;
    private Bitmap bitmapBoot;

    public SetUpDisplayObjects(Context context, Display display, Point size, int level){
        this.context = context;
        this.level = level;
        display.getSize(size);
        plotDisplayWidth = Math.min(size.x, size.y) *10 / 11;
        plotDisplayHeight = plotDisplayWidth;
        shiftXY = new int[2];
        shiftXY[0] = 0;
        shiftXY[1] = plotDisplayHeight / 10;
        // set up
        levelPlotArray = setUpPlots(plotDisplayWidth);

        // set up treasure + miscellaneous items
        treasureList = setUpTreasure(levelPlotArray);
        Log.d(TAG, "level in setup " + level);


    }

    public PlotOfLand[][] setUpPlots(int canvasSizeX){

        Log.d(TAG, "setupplots started " );
        int noPlots = 9 + level; // todo decide on level differences
        int centralDiagonal = 11; // size of grid intend 15 todo decide on size and if different par level
        int topSide = 6; // makes 11 rows (central diagonal - topside)*2 + 1
        // int noRows = (centralDiagonal - topSide) * 2 + 1;
        // calculate no of grid plots
        gridPlots = (centralDiagonal * centralDiagonal + topSide - topSide * topSide);
        // set up the plots in hexagon shape (x,y) identified with neighbours identified,
        // and the five central plots active
        PlotOfLand[] plotArray = new PlotOfLand[gridPlots];
        PlotOfLand[][] levelPlotArray = new PlotOfLand[noOfFields][noPlots];
        int halfPlotWidth = (((canvasSizeX - 10) / centralDiagonal) - 2) / 2;
        plotWidth = halfPlotWidth * 2;
        // todo perhaps make this a function of levelplot?
        plotHeight = (halfPlotWidth  + 1) * 1732 / 1000 - 2;
        setUpPictures();
        int sizeRow = topSide;
        int row = 0;
        int column = centralDiagonal - topSide;
        int count = 1;
        int shift = 3;
        for (int i = 0; i < gridPlots; i++){
            plotArray[i] = new PlotOfLand(context, this);
            plotArray[i].setMyX(column * halfPlotWidth + shiftXY[0]);
            plotArray[i].setMyY(row * plotHeight + shiftXY[1]);
            if (count < sizeRow) {
                column = column + 2;
                count++;
            } else {
                if (row <  centralDiagonal - topSide){
                    sizeRow++;
                } else {
                    sizeRow--;
                    shift = 1;
                }
                column = column - sizeRow * 2 + shift;
                count = 1;
                row++;
            }

            Log.d(TAG, "plotarray initialised " + i + " x " + plotArray[i].getMyX()
                    + " y " + plotArray[i].getMyY());
        }
        // now set the 5 central plots active and find their neighbours
        PlotOfLand[] neighbours;
        int[] activePlots = new int[5]; // todo make possible to use a different number
        int countPlots = 0;
        for (int i = (gridPlots/2) - 2; i < (gridPlots/2) + 3; i++){
            for (int f = 0; f < noOfFields; f++){
                plotArray[i].setActive(true, f);
            }
            activePlots[countPlots] = i;
            countPlots++;
            // set neighbours
            neighbours = setNeighbours(plotArray[i], plotArray);
            plotArray[i].setMyNeighbours(neighbours);
        }
        // set up random field 5 times for level
        for (int f = 0; f < noOfFields; f++){
            Log.d(TAG, "level setting up " + f);
            levelPlotArray[f] = setUpField(plotArray, activePlots, noPlots, f);
            Log.d(TAG, "level set up " + f);
        }
        return levelPlotArray;
    }

    public  ArrayList<PlotOfLand>[] setUpTreasure(PlotOfLand[][] levelPlotArray) {
        int bronze = 50; // PRB out of "gold" for bronze
        int silver = 90; // PRB minus bronze out of "gold" for silver
        int gold = 100; // rest for gold
        int countGold = 0; // want at least one gold per level
        int boot = 50; // PRB out of "nuts" for boot
        int leftShoe = 90; //PRB minus boot out of nuts for leftshoe
        int rightShoe = 91; //etc
        int nuts = 120; // total prob for misc items
        int lengthField = levelPlotArray[0].length;
        Random generator = new Random();
        int random;
        int randomPlot;
        int randomField;
        int noTreasures = Math.min
                (Math.max(noOfFields* lengthField /20, noOfFields),
                        noOfFields * maxTreasuresPerField) ; // possible more treasures limited at 5 per field
        int treasurePlots = noOfFields;
        // PlotOfLand[][] treasure = new PlotOfLand[noOfFields][noTreasures];

        //gavins example
        //ArrayList<PlotOfLand> treasures = new ArrayList<>();

        // treasures.add(new PlotOfLand());
        ArrayList<PlotOfLand>[] treasure = new ArrayList [noOfFields];

        int[] checkArray = new int[noTreasures];
        int count[] = new int[noOfFields];

        for (int t = 0; t < noOfFields; t++){ // at least one treasure and one misc per field
            treasure[t] = new ArrayList<>();
            randomPlot = generator.nextInt(lengthField);
            random = generator.nextInt(gold);
            checkArray[t] = randomPlot;
            treasure[t].add(levelPlotArray[t][randomPlot]);
            count[t]= 1;
            if (random < bronze){
                levelPlotArray[t][randomPlot].setTreasure(2, t);//bronze
            }
            else if (random < silver) {
                levelPlotArray[t][randomPlot].setTreasure(1, t); //silver
            }
            else {
                levelPlotArray[t][randomPlot].setTreasure(0, t); //gold
                countGold++;
            }
            for (int misc = 0; misc < (int)(lengthField / 9); misc++) {
                randomPlot = generator.nextInt(lengthField);
                random = generator.nextInt(nuts);
                while (levelPlotArray[t][randomPlot].getTreasure(t) >= 0) {
                    randomPlot = generator.nextInt(lengthField);
                }
                if (random < boot){
                    levelPlotArray[t][randomPlot].setTreasure(3, t); // boot
                }
                else if (random < leftShoe) {
                    levelPlotArray[t][randomPlot].setTreasure(4, t); // Lshoe
                }
                else if (random < rightShoe) {
                    levelPlotArray[t][randomPlot].setTreasure(5, t); // Rshoe
                }
                else {
                    levelPlotArray[t][randomPlot].setTreasure(6, t); // nuts
                }
            }
        }
        if (countGold == 0){ // ensure at least one gold
            randomField = generator.nextInt(noOfFields);
            levelPlotArray[randomField][checkArray[randomField]].setTreasure(0, randomField);
        }
        while (treasurePlots < noTreasures) {
            randomField = generator.nextInt(noOfFields);
            randomPlot = generator.nextInt(lengthField);
            random = generator.nextInt(gold);
            if (levelPlotArray[randomField][randomPlot].getTreasure(randomField) < 0){
                count[randomField]++;
                if (count[randomField] <= maxTreasuresPerField) {
                    treasurePlots++;
                    treasure[randomField].add(levelPlotArray[randomField][randomPlot]);
                    if (random < bronze){
                        levelPlotArray[randomField][randomPlot].setTreasure(2, randomField);
                    }
                    else if (random < silver) {
                        levelPlotArray[randomField][randomPlot].setTreasure(1, randomField);
                    }
                    else {
                        levelPlotArray[randomField][randomPlot].setTreasure(0, randomField);
                    }
                }
            }
        }
        return treasure;
    }

    private PlotOfLand[] setUpField(PlotOfLand[] plotArray, int[] activePlots, int noPlots, int field) {
        int initialLength = activePlots.length;
        PlotOfLand[] fieldArray = new PlotOfLand[noPlots];
        Random generator = new Random();
        int randomPlot;
        int randomNeighbour;
        PlotOfLand[] neighbours;
        for (int i = 0; i < initialLength; i++){
            fieldArray[i] = plotArray[activePlots[i]];
        }
        int newLength = initialLength;
        while (newLength < noPlots){
            Log.d(TAG, "LENGTH " + newLength + "no plots " + noPlots);
            randomPlot = generator.nextInt(newLength);
            neighbours = fieldArray[randomPlot].getMyNeighbours();
            randomNeighbour = generator.nextInt(neighbours.length);
            PlotOfLand newPlot = neighbours[randomNeighbour];
            if (newPlot != null){
                if (!newPlot.isActive()){
                    newPlot.setActive(true, field);
                    fieldArray[newLength] = newPlot;
                    newLength++;
                    neighbours = setNeighbours(newPlot, plotArray);
                    newPlot.setMyNeighbours(neighbours);
                }
                if (!newPlot.fieldActive(field)){
                    newPlot.setActive(true, field);
                    fieldArray[newLength] = newPlot;
                    newLength++;
                }
            }

        }
        Log.d(TAG, "fieldarray done");
        return fieldArray;

    }

    private PlotOfLand[] setNeighbours(PlotOfLand thisPlot, PlotOfLand[] plotArray){
        int x = thisPlot.getMyX();
        int y = thisPlot.getMyY();
        Log.d(TAG, " my x " + x + " my y " + y);
        int halfPlotWidth = this.plotWidth / 2;
        int plotHeight = this.plotHeight;
        int count = 0;
        PlotOfLand[] neighbours = new PlotOfLand[6];
        // set neighbours
        for (int n = 0; n < gridPlots; n++){
            int testX = plotArray[n].getMyX();
            int testY = plotArray[n].getMyY();
            boolean isNeighbour = ((abs(x - testX) < (3 * halfPlotWidth))
                    && (abs(y - testY) < (2 * plotHeight - 2)) );
            if ((x - testX) != 0 || (y - testY) != 0){
                if (isNeighbour){
                    neighbours[count] = plotArray[n];
                    Log.d(TAG, "neighbour x " + testX + " neigbour y " + testY);
                    count++;
                }
            }
        }
        return neighbours;
    }


    public void setUpPictures() {

        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.untouchedground1);
        bitmap1 = Bitmap.createScaledBitmap(bitmap1, plotWidth, plotWidth, false);

        bitmapHole = BitmapFactory.decodeResource(context.getResources(), R.drawable.hole);
        bitmapHole = Bitmap.createScaledBitmap(bitmapHole,
                plotWidth * 3 / 4 ,
                plotHeight * 3 / 4, false);
        bitmapHoleCoins = BitmapFactory.decodeResource(context.getResources(), R.drawable.holewithcoins);
        bitmapHoleCoins = Bitmap.createScaledBitmap(bitmapHoleCoins,
                plotWidth * 3 / 4 ,
                plotHeight * 3 / 4, false);
        bitmapBoot = BitmapFactory.decodeResource(context.getResources(), R.drawable.boot);
        bitmapBoot = Bitmap.createScaledBitmap(bitmapBoot,
                plotWidth * 3 / 4 ,
                plotHeight * 3 / 4, false);

    }

    public PlotOfLand[][] getLevelPlotArray() {
        return levelPlotArray;
    }

    public int getPlotDisplayWidth() {
        return plotDisplayWidth;
    }

    public int getPlotDisplayHeight() {
        return plotDisplayHeight;
    }

    public int getPlotWidth() {
        return plotWidth;
    }

    public int getPlotHeight() {
        return plotHeight;

    }

    public int[] getShiftXY() {
        return shiftXY;
    }

    public Bitmap getBitmapHole() {
        return bitmapHole;
    }

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public  ArrayList<PlotOfLand>[] getTreasureList() {
        return treasureList;
    }

    public Bitmap getBitmapHoleCoins() {
        return bitmapHoleCoins;
    }

    public Bitmap getBitmapBoot() {
        return bitmapBoot;

    }

    public void modifyTreasureList(PlotOfLand correctPlot, int field) {

        treasureList[field].remove(correctPlot);
    }
}
