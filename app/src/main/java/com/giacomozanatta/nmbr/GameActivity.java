package com.giacomozanatta.nmbr;

import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends Activity {
    int SHOW_SOLUTION_TAG = 11;
    int CONTINUE = 12;
    int MAIN_MENU = 13;
    MediaPlayer soundCorrect;
    MediaPlayer soundWrong;
    MediaPlayer soundGameOver;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        soundWrong = MediaPlayer.create(this, R.raw.wrong);
        soundCorrect = MediaPlayer.create(this, R.raw.correct);
        soundGameOver = MediaPlayer.create(this, R.raw.game_over);
        initKeyboard();
        if (getIntent().getSerializableExtra("Question") != null || !Game.isInit()) {
            Game.initGame(this);
            ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra("Question");
            try {
                Game.getInstance().setQuestions(questions);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
            try {
                Game.getInstance().setActivity(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        startGame();
    }

    public void startGame(){
        /*metto visibile la tastiera, invisibile il layoutstart*/
        try {
            Game.getInstance().startNewGame("roman");
        } catch (Exception e) {
            Log.i("CIAO", e.getMessage());
        }
    }

    public void endGame(){
        Intent intent = new Intent(this, GameOverActivity.class);
        try {
            String curAnswer = Game.getInstance().getCurAnswer();
            String curQuestion = Game.getInstance().getCurQuestion();
            intent.putExtra("QUESTION", curQuestion);
            intent.putExtra("ANSWER", curAnswer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(intent);
        finish();
    }

    public void initKeyboard(){
        TextView keyboard[] = new TextView[12];
        final TextView answer = (TextView)findViewById(R.id.textAnswer);
        for(int i=0; i<10; i++){
            String res = "button0"+i;
            int resID = getResources().getIdentifier(res, "id", getPackageName());
            keyboard[i]=(TextView)findViewById(resID);
            keyboard[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer.getText().length() < 4) {
                        TextView pressed = (TextView) v;
                        String ans = (String) answer.getText() + pressed.getText();
                        answer.setText(ans);
                    }
                }
            });
        }
        keyboard[10] = (TextView)findViewById(R.id.buttonDel);
        keyboard[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.getText().length() > 0 ) {
                    String ans = (String) answer.getText();
                    ans = ans.substring(0, ans.length()-1);
                    answer.setText(ans);
                }
            }
        });

        keyboard[11] = (TextView) findViewById(R.id.buttonEnter);
        keyboard[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ans = (String) answer.getText();
                try {

                    if (Game.getInstance().checkAnswer(ans)) {
                        Log.i("CIAO", "ora aggiorno i punti");
                        Game.getInstance().updatePoints();
                        Log.i("CIAO", "nuova domanda");
                        soundCorrect.start();
                        Game.getInstance().newQuestion(); /*nuova domanda*/
                    }
                    else if(Game.getInstance().checkEndGame()) {
                        soundGameOver.start();
                        Game.getInstance().endGame();
                    }
                    else{
                        /*prima di questo, devo mostrare la soluzione, e permettere all'utente di
                        uscire dal gioco. Mostrare: punteggio e vite rimaste.*/
                        Game.getInstance().stopCountdown();
                        showSolution("WRONG_ANS");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void showSolution(String cause){
        Intent intent = new Intent(this, ShowSolutionActivity.class);
        soundWrong.start();
        try {
            String curAnswer = Game.getInstance().getCurAnswer();
            String curQuestion = Game.getInstance().getCurQuestion();
            String points = ""+Game.getInstance().getPoints();
            int life = Game.getInstance().getLife();
            intent.putExtra("QUESTION", curQuestion);
            intent.putExtra("ANSWER", curAnswer);
            intent.putExtra("LIFE", life);
            intent.putExtra("POINTS", points);
            intent.putExtra("CAUSE", cause);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivityForResult(intent, SHOW_SOLUTION_TAG);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SHOW_SOLUTION_TAG){
            /*gestisco il ritorno*/
            if(resultCode == CONTINUE){
                try {
                    Log.i("CIAO", "CONTINUO GIOCO!");
                    Game.getInstance().newQuestion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode == MAIN_MENU){
                try {
                    Game.getInstance().destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(this, MainMenuActivity.class);
                startActivity(i);
            }
        }
    }
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        try {
            Game.getInstance().destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);

    }

}