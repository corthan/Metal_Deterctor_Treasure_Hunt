package com.gandgapps.metal_deterctor_treasure_hunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "menu";
    public int buttonNo = 0;
    public int noButtons = 8; // todo could change
    public int thisButtonID = 12345;
    public String[] levelName;
    public int level = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void levelSelect(View view){// Get the id of the button clicked
        int butID = view.getId();
        int[] buttonID = {
                R.id.level1Button,
                R.id.level2Button,
                R.id.level3Button,
                R.id.level4Button,
                R.id.slevel1Button,
                R.id.slevel2Button,
                R.id.slevel3Button,
                R.id.slevel4Button,
        };
        this.levelName = new String[]{
                "Puzzle level 1",
                "Puzzle level 2",
                "Puzzle level 3",
                "Puzzle level 4",
                "Squirrel level 1",
                "Squirrel level 2",
                "Squirrel level 3",
                "Squirrel level 4",

        };
        for (int bid = 0; bid < noButtons; bid++){
            if (buttonID[bid] == butID) {
                buttonNo = bid;
                thisButtonID = buttonID[bid];
            }
        }
        //if (buttonNo < 4){
        //    level = buttonNo + 1; // todo decide on no of levels etc
        //} else {
        //    level= buttonNo - 3;
        //}
        level = 40;
    }

    public void playGame(View view){ //set up intent for game activity

        if (buttonNo < noButtons / 2){
            Intent playIntent = new Intent(this, DetectActivity.class);
            Log.d(TAG,"level passed" + level );
            playIntent.putExtra("key", level);
            startActivity(playIntent);
        } else {
            Intent squirrelIntent = new Intent(this, SquirrelActivity.class);
            squirrelIntent.putExtra("key",level);
            startActivity(squirrelIntent);
        }

    }
}
