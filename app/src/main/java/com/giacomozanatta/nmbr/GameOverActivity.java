package com.giacomozanatta.nmbr;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        ((TextView)findViewById(R.id.textQuestion)).setText(getIntent().getExtras().getString("QUESTION"));
        ((TextView)findViewById(R.id.textAnswer)).setText(getIntent().getExtras().getString("ANSWER"));
        findViewById(R.id.buttonMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToMainMenu();
            }
        });
        findViewById(R.id.buttonRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
    }
    public void redirectToMainMenu(){
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void retry(){
        try {
            Game.getInstance().reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}