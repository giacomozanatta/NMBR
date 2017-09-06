package com.giacomozanatta.nmbr;


import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class ShowSolutionActivity extends Activity {
    int CONTINUE = 12;
    int MAIN_MENU = 13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_solution);
        String cause = getIntent().getStringExtra("CAUSE");
        int hearth = getIntent().getExtras().getInt("LIFE");
        if (cause.equals("WRONG_ANS"))
            ((TextView) findViewById(R.id.textCauseOf)).setText(getString(R.string.lost_wrong));
        else if (cause.equals("TIMER_END"))
            ((TextView) findViewById(R.id.textCauseOf)).setText(getString(R.string.lost_timer));
        ((TextView) findViewById(R.id.textQuestionShowSolution)).setText(getIntent().getStringExtra("QUESTION"));
        ((TextView) findViewById(R.id.textAnswerShowSolution)).setText(getIntent().getStringExtra("ANSWER"));
        ((TextView) findViewById(R.id.textPointsShowSolution)).setText(getIntent().getStringExtra("POINTS"));
        /*inserisco la vita. Effetto fade in*/
        String life = "";
        for (int i = 0; i < hearth; i++)
            life += "❤";
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        fadeIn.setStartOffset(100);
        ((TextView) findViewById(R.id.textHearthShowSolution)).setText(life);
        findViewById(R.id.textHearthShowSolution).startAnimation(fadeIn);
        findViewById(R.id.textBtnContinueShowSolution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(CONTINUE);
                finish();
            }
        });
        findViewById(R.id.textBtnMainMenuShowSolution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(MAIN_MENU);
                finish();
            }
        });
    }
}
