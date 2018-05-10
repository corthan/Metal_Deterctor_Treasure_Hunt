package com.gandgapps.metal_deterctor_treasure_hunt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;
import java.util.Random;

import static java.lang.Math.abs;

public class DetectActivity extends Activity {

    private static final String TAG = "none";
    public int gridPlots = 5;
    public int noOfFields = 1; //todo find way of intialising active or not active for each field
    public int level = 0;
    PlotsView plotsView;
    int sizeX = 500;
    int sizeY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sizeX = size.x;
        sizeY = size.y;

        Intent intent = getIntent();
        level = intent.getIntExtra("key", 0);

        setContentView(R.layout.activity_detect);//todo
        TextView textView = findViewById(R.id.programText);
        Log.d(TAG, "this is the tag " + level );
        textView.setText("check");
        Log.d(TAG, "this is the next tag " + sizeX);
        // set up
        PlotOfLand[][] levelPlotArray = setUpPlots(sizeX, sizeY);
        Log.d(TAG, "this is the third tag " + sizeX + sizeY );
        plotsView = new PlotsView(this, size.x, size.y, levelPlotArray);
        setContentView(plotsView);//todo
    }

    public PlotOfLand[][] setUpPlots(int canvasSizeX, int canvasSizeY){

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
        PlotOfLand[][] levelPlotArray = new PlotOfLand[5][noPlots];
        int halfPlotWidth = (((canvasSizeX - 10) / centralDiagonal) - 2) / 2; // todo perhaps make this a function of levelplot?
        int plotHeight = (halfPlotWidth  + 1) * 1732 / 1000 - 2;
        int sizeRow = topSide;
        int row = 0;
        int column = centralDiagonal - topSide;
        int count = 1;
        int shift = 3;
        for (int i = 0; i < gridPlots; i++){
            plotArray[i] = new PlotOfLand();
            plotArray[i].setMyX(column * halfPlotWidth);
            plotArray[i].setMyY(row * plotHeight);
            plotArray[i].setMyWidth(2*halfPlotWidth);
            plotArray[i].setMyHeight(plotHeight);
            if (count < sizeRow) {
                column = column + 2;
                count++;
            } else {
                if (row <  centralDiagonal - topSide){ // todo debug hexagon shape
                    sizeRow++;
                } else {
                    sizeRow--;
                    shift = 1;
                }
                column = column - sizeRow * 2 + shift;
                count = 1;
                row++;
            }

            Log.d(TAG, "plotarray initialised " + i + " x " + plotArray[i].getMyX() + " y " + plotArray[i].getMyY());
        }
        // now set the 5 central plots active and find their neighbours
        PlotOfLand[] neighbours;
        int[] activePlots = new int[5]; // todo make possible to use a different number
        int countPlots = 0;
        for (int i = (gridPlots/2) - 2; i < (gridPlots/2) + 3; i++){
            plotArray[i].active = true;
            activePlots[countPlots] = i;
            countPlots++;
            // set neighbours
            neighbours = setNeighbours(plotArray[i], plotArray);
            plotArray[i].setMyNeighbours(neighbours);
        }
        // set up random field 5 times for level
        for (int f = 0; f < noOfFields; f++){
            Log.d(TAG, "level setting up " + f);
            levelPlotArray[f] = setUpField(plotArray, activePlots, noPlots);
            Log.d(TAG, "level set up " + f);
        }




        return levelPlotArray;
    }

    private PlotOfLand[] setUpField(PlotOfLand[] plotArray, int[] activePlots, int noPlots) {
        int initialLength = activePlots.length;
        PlotOfLand[] fieldArray = new PlotOfLand[noPlots];
        Random generator = new Random();
        int randomField;
        int randomNeighbour;
        PlotOfLand[] neighbours;
        for (int i = 0; i < initialLength; i++){
            fieldArray[i] = plotArray[activePlots[i]];
        }
        int newLength = initialLength;
        int count = 0;
        while (newLength < noPlots){
            Log.d(TAG, "LENGTH " + newLength + "no plots " + noPlots);
            randomField = generator.nextInt(newLength);
            neighbours = fieldArray[randomField].getMyNeighbours();
            randomNeighbour = generator.nextInt(neighbours.length);
            PlotOfLand newPlot = neighbours[randomNeighbour];
            if (newPlot != null){
                count = 0;
                if (!newPlot.isActive()){
                    newPlot.active = true;
                    fieldArray[newLength] = newPlot;
                    newLength++;
                    neighbours = setNeighbours(newPlot, plotArray);
                    newPlot.setMyNeighbours(neighbours);
                }
            } else {
                count ++;
                if (count > 10) {

                }
            }

        }
        Log.d(TAG, "fieldarray done");
        return fieldArray;

    }

    public PlotOfLand[] setNeighbours(PlotOfLand thisPlot, PlotOfLand[] plotArray){
        int x = thisPlot.getMyX();
        int y = thisPlot.getMyY();
        Log.d(TAG, " my x " + x + " my y " + y);
        int halfPlotWidth = thisPlot.getMyWidth() / 2;
        int plotHeight = thisPlot.getMyHeight();
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

    @Override
    protected void onResume() {
        super.onResume();
        plotsView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        plotsView.pause();
    }
}
