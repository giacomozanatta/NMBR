package com.giacomozanatta.nmbr;

import android.os.CountDownTimer;

import android.widget.TextView;


/**
 * Created by giaco on 8/22/2017.
 */

public class AnswerCountDown extends CountDownTimer {

    long remainingTime;
    public AnswerCountDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        remainingTime = millisInFuture / 1000;
    }

    public void onTick(long millisUntilFinished) {
        remainingTime = (millisUntilFinished / 1000)-1;
        String text = "" + remainingTime;
        try {
            ((TextView)Game.getInstance().getActivity().findViewById(R.id.textCountDown)).setText(text);
            if(remainingTime==6){
                Game.getInstance().startColorAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFinish() {
        try {
            ((TextView)Game.getInstance().getActivity().findViewById(R.id.textCountDown)).setText("0");
            Game.getInstance().timerFinish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRemainingTime(){
        return (int)remainingTime;
    }
}