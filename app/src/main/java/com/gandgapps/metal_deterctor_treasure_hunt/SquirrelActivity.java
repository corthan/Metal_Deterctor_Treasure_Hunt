package com.gandgapps.metal_deterctor_treasure_hunt;
 // todo insert squirrel thread
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SquirrelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int level = intent.getIntExtra("key", 0);
        Intent playIntent = new Intent(this, DetectActivity.class);
        playIntent.putExtra("key",level);
        startActivity(playIntent);
        setContentView(R.layout.activity_squirrel);
    }
}
