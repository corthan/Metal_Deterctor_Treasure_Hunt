package com.gandgapps.metal_deterctor_treasure_hunt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.util.Random;

import static java.lang.Math.abs;

public class DetectActivity extends Activity {

    private static final String TAG = "none";
    public int gridPlots = 5;
    public int noOfFields = 1; //todo find way of intialising active or not active for each field
    public int level = 0;
    PlotsView plotsView;
    int plotDisplayWidth = 500;
    int plotDisplayHeight = 500;
    int maxTreasuresPerField = 5;
    int[] shiftXY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Intent intent = getIntent();
        // need level to run set up
        level = intent.getIntExtra("key", 0);

        SetUpDisplayObjects setUpDisplayObjects = new SetUpDisplayObjects(this, display, size, level);

        setContentView(R.layout.activity_detect);//todo
        Log.d(TAG, "this is the tag " + level );
        plotDisplayWidth = setUpDisplayObjects.getPlotDisplayWidth();
        Log.d(TAG, "this is the next tag " + plotDisplayWidth);

        Log.d(TAG, "this is the third tag " + plotDisplayWidth + plotDisplayHeight);
        plotsView = new PlotsView(this, setUpDisplayObjects);
        setContentView(plotsView);//todo
    }



    @Override
    protected void onResume() {
        Log.d(TAG, "before super");
        super.onResume();
        Log.d(TAG, "after super");
        plotsView.resume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "on pause called");
        super.onPause();
        plotsView.pause();
        Log.d(TAG, "onPause: end");
    }
    public void setFieldFragment(View view) {

        Context context = this;
        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = FieldFragment.newInstance();
        fragmentTransaction.replace(R.id.fragmentPlace, fragment);
        fragmentTransaction.commit();
    }
}
