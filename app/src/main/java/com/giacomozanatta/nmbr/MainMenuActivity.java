package com.giacomozanatta.nmbr;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        TextView new_game=(TextView) findViewById(R.id.text_new_game);
        new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        findViewById(R.id.textEditor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditor();
            }
        });
    }
    public void goToEditor(){
        Intent editor = new Intent(this, EditorActivity.class);
        startActivity(editor);
    }
    public void startNewGame(){
        Intent startGame = new Intent(this, SelectCategoryActivity.class);
        startActivity(startGame);
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}