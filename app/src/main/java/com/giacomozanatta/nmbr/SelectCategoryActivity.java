package com.giacomozanatta.nmbr;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import com.giacomozanatta.nmbr.view.TextViewApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class SelectCategoryActivity extends Activity {
    ArrayList<Question> questions = new ArrayList<>();
    ArrayList<Category> categories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        super.onCreate(savedInstanceState);

        /*ottengo le categorie*/
        RequestCategory rC = new RequestCategory(this);
        rC.execute();
    }

    public class RequestCategory extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        Activity requested;

        public RequestCategory(Activity requested) {
            this.requested = requested;
        }

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(requested, "Download...", "", true);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... type) {
            StringBuffer htmlCode = new StringBuffer();
            try {
                URL url = new URL("http://nmbrtest.altervista.org/android/get_categories.php?language="+Locale.getDefault().getLanguage());
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    htmlCode.append(inputLine);
                    Log.d("CIAO", "html: " + inputLine);
                }

                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("CIAO", "Error: " + e.getMessage());
                Log.d("CIAO", "HTML CODE: " + htmlCode);
            }
            return htmlCode.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //result contiene il JSON delle domande.
            //bisogna estrarre domanda e risposta e salvarle su questions
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Category>>() {
            }.getType();
            Log.i("CIAO", "JSOOON" + result);
            categories = gson.fromJson(result, type);
            for (Category cat : categories) {
                Log.i("CIAO", cat.getId() + "-" + cat.getName());
            }
            progressDialog.dismiss();
            showCategories();
            super.onPostExecute(result);
        }
    }

    public void showCategories(){
        ConstraintLayout cL =(ConstraintLayout)findViewById(R.id.ConstraintLayoutCategory);
        if(categories.size()>0){
            TextViewApp cate = new TextViewApp(this);
            cate.setText(categories.get(0).getName());
            cate.setSupportValue(categories.get(0).getId());
            cate.setGravity(Gravity.CENTER);
            cate.setTextColor(getResources().getColor(R.color.white));
            cate.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.category_text_size));
            cate.setId(View.generateViewId());
            cL.addView(cate);
            ConstraintSet cS = new ConstraintSet();
            cS.clone(cL);
            cS.connect(cate.getId(), ConstraintSet.TOP, cL.getId(), ConstraintSet.TOP, 8);
            cS.connect(cate.getId(), ConstraintSet.LEFT, cL.getId(), ConstraintSet.LEFT, 20);
            cS.connect(cate.getId(), ConstraintSet.RIGHT, cL.getId(), ConstraintSet.RIGHT, 20);
            cate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestQuestion(((TextViewApp)v).getSupportValue());
                }
            });
            cS.applyTo(cL);


            int lastId=cate.getId();
            for (int i=1; i<categories.size(); i++) {
                cate = new TextViewApp(this);
                cate.setText(categories.get(i).getName());
                cate.setSupportValue(categories.get(i).getId());
                cate.setGravity(Gravity.CENTER);
                cate.setTextColor(getResources().getColor(R.color.white));
                cate.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.category_text_size));
                cate.setId(View.generateViewId());
                cL.addView(cate);
                cS = new ConstraintSet();
                cS.clone(cL);
                cS.connect(cate.getId(), ConstraintSet.TOP, lastId, ConstraintSet.BOTTOM, 8);
                cS.connect(cate.getId(), ConstraintSet.LEFT, cL.getId(), ConstraintSet.LEFT, 20);
                cS.connect(cate.getId(), ConstraintSet.RIGHT, cL.getId(), ConstraintSet.RIGHT, 20);
                cS.applyTo(cL);
                lastId=cate.getId();
                cate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestQuestion(((TextViewApp)v).getSupportValue());
                    }
                });
            }
        }
    }

    private void requestQuestion(String supportValue) {
        RequestQuestion rQ = new RequestQuestion(this, supportValue);
        rQ.execute();
    }
    public class RequestQuestion extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        Activity requested;
        String idCat;
        public RequestQuestion(Activity requested, String idCat) {
            this.requested = requested;
            this.idCat = idCat;
        }

        protected void onPreExecute() {
            Log.i("CIAO", "SHOWING PROGRESS BAR!");
            progressDialog = ProgressDialog.show(requested, "Download...", "", true);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... type) {
            StringBuffer htmlCode = new StringBuffer();
            try {
                URL url = new URL("http://nmbrtest.altervista.org/android/get_question.php?catId="+idCat+"&language="+ Locale.getDefault().getLanguage());
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    htmlCode.append(inputLine);
                    Log.d("CIAO", "html: " + inputLine);
                }

                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("CIAO", "Error: " + e.getMessage());
                Log.d("CIAO", "HTML CODE: " + htmlCode);
            }
            return htmlCode.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //result contiene il JSON delle domande.
            //bisogna estrarre domanda e risposta e salvarle su questions
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Question>>() {
            }.getType();
            Log.i("CIAO", "JSOOON" + result);
            questions = gson.fromJson(result, type);
            for (Question q : questions) {
                Log.i("CIAO", q.getAnswer() + "-" + q.getQuestion());
            }
            progressDialog.dismiss();
            startGame();
            super.onPostExecute(result);
        }
    }

    public void startGame(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Question", questions);
        startActivity(intent);
    }
}