package com.giacomozanatta.nmbr;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;
/**
 * Created by giaco on 8/22/2017.
 */

public class Game {
    private Activity activity;
    private ArrayList<Question> questions;
    private AnswerCountDown countdown;
    static int initialized = 0;
    private int points;
    private int curQuestion = 0;
    private int life = 0;
    ValueAnimator colorAnimation;
    public static Game game;

    private Game(Activity activity) {
        Log.i("CIAO", "creo question");
        questions = new ArrayList<>();
        this.activity = activity;
        initializeCountDown();
    }

    public static Game getInstance() throws Exception {
        if (initialized == 0)
            throw new Exception();
        else
            return game;
    }

    public static void initGame(Activity act) {
        game = new Game(act);
        initialized = 1;
    }
    public static boolean isInit(){
        if(initialized==1)
            return true;
        return false;
    }
    public Activity getActivity(){
        return activity;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public int getPoints() {
        return points;
    }


    public void startNewGame(String type) {
        Log.i("CIAO", "sorting..");
        sortQuestions();
        updateLife(3);
        newQuestion();
    }

    private void sortQuestions() {
        Log.i("CIAO", "domande lette: "+questions.size());
        Random r = new Random();
        for (int i = 0; i < questions.size(); i++) {
            int index = r.nextInt(questions.size());
            Question temp = questions.get(i);
            questions.set(i, questions.get(index));
            questions.set(index, temp);
        }
    }

    private void initializeCountDown() {
        Log.i("CIAO", "inizializzo countdown");
        countdown = new AnswerCountDown(11000, 1000);
    }

    private void startCountdown() {
        countdown.start();
    }
    public void stopCountdown() {
        if(countdown != null)
            countdown.cancel();
    }

    public void newQuestion() {
        stopCountdown();
        String numQuestion = "#" + (curQuestion + 1);
        ((TextView) activity.findViewById(R.id.textAnswer)).setText("");
        Log.i("CIAO", questions.get(curQuestion).getQuestion()+" "+questions.get(curQuestion).getAnswer());
        ((TextView) activity.findViewById(R.id.text_question)).setText(questions.get(curQuestion).getQuestion());
        curQuestion++;
        Log.i("CIAO", "NEW QUESTION: "+curQuestion);
        if(colorAnimation!=null) {
            Log.i("CIAO", "cancello animation");
            colorAnimation.cancel();
        }
        int color = ContextCompat.getColor(activity, R.color.backgroundQuestion);
        activity.findViewById(R.id.layoutQuestion).setBackgroundColor(color);
        ((TextView) activity.findViewById(R.id.text_num_question)).setText(numQuestion);
        initializeCountDown();
        startCountdown();
    }
    public boolean checkAnswer(String answer){
        if(answer.equals(getCurAnswer()))
            return true;
        else{
            updateLife(-1);
            return false;
        }
    }

    private void updateLife(int i) {
        String hearth="";
        life+=i;
        for(i=0; i<life; i++){
            hearth+="❤";
        }
        ((TextView)activity.findViewById(R.id.textLife)).setText(hearth);
    }

    public boolean checkEndGame(){
        if(life == 0)
            return true;
        else
            return false;
    }

    public void updatePoints(){
        points += countdown.getRemainingTime();
        String p = ""+points;
        ((TextView)activity.findViewById(R.id.textPoints)).setText(p);
        Log.i("CIAO", "Aggiorno punteggio di "+points);
    }
    public String getCurAnswer() {
        return questions.get(curQuestion-1).getAnswer();
    }

    public String getCurQuestion() {
        return questions.get(curQuestion-1).getQuestion();
    }

    public void startColorAnimation() {
        int colorFrom = ContextCompat.getColor(activity, R.color.backgroundQuestion);
        int colorTo = ContextCompat.getColor(activity, R.color.redAlert);
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(3000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                try {
                    Game.getInstance().getActivity().findViewById(R.id.layoutQuestion).setBackgroundColor((int) animator.getAnimatedValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        colorAnimation.start();
    }

    public int getLife() {
        return life;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void reset() {
        Log.i("CIAO", "reset. domande ->"+questions.size());
        curQuestion = 0;
        life = 0;
    }

    public void endGame(){
        countdown.cancel();
        ((GameActivity)activity).endGame();
    }

    public void destroy(){
        Log.i("CIAO", "Distruggo gioco");
        if(countdown != null)
            countdown.cancel();
        game = null;
        initialized=0;
    }
    public void timerFinish(){
        updateLife(-1);
        if(checkEndGame())
            endGame();
        else
            ((GameActivity)activity).showSolution("TIMER_END");
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }
}

