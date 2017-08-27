package com.giacomozanatta.nmbr;

import java.io.Serializable;

/**
 * Created by giaco on 8/20/2017.
 */

public class Question implements Serializable{
    private String question;
    private String answer;
    public Question(String question, String answer){
        this.question=question;
        this.answer=answer;
    }
    public String getQuestion(){
        return question;
    }

    public String getAnswer(){
        return answer;
    }
}
