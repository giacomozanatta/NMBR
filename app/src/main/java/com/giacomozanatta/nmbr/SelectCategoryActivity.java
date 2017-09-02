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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.giacomozanatta.nmbr.view.TextViewApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SelectCategoryActivity extends Activity {
    ArrayList<Question> questions = new ArrayList<>();
    ArrayList<Category> downloadedCategories = new ArrayList<>();
    ArrayList<Question> downloadedQuestions = new ArrayList<>();
    /*attributi per la creazione dell'ExpandableList*/
    CategoryExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        super.onCreate(savedInstanceState);



        /*EXPANDABLE LIST VIEW*/
        expListView = (ExpandableListView) findViewById(R.id.expandableListCate);


        /*ottengo le categorie memorizzate sulla memoria interna*/
        ArrayList<String> categoryInternal = FileHandler.getOfficialCategories(this);
        /*ottengo le categorie e le domande dal server:*/
        RequestCategory rC = new RequestCategory(this);
        rC.execute();
        /*ottengo le custom cate*/
        ArrayList<String> categoryCustom = FileHandler.getCustomCategories();
        showCategories(categoryInternal, downloadedCategories, categoryCustom);

        listAdapter = new CategoryExpandableListAdapter(this, listDataHeader, listDataChild);
        // Listview on child click listener

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition==1 && downloadedCategories.size()<1){
                    /*caso del downloaded*/
                    requestCategory();
                }

            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                if(groupPosition == 0)
                    loadLocalQuestions(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                else if(groupPosition == 1) {
                    String categoryName = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                    requestQuestion(findIdOf(downloadedCategories, categoryName), categoryName);
                }
                else if(groupPosition == 2){
                    loadCustomQuestions(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                }
                return false;
            }
        });
        expListView.setAdapter(listAdapter);
    }

    private String findIdOf(ArrayList<Category> categories, String categoryName) {
        for(Category c : categories){
            if(c.getName().equals(categoryName))
                return c.getId();
        }
        return "";
    }

    private void loadLocalQuestions(String cats) {
        questions = FileHandler.getOfficialQuestions(this, cats);
        Log.i("CIAO", "DOMANDE LETTE: "+questions.size());
        startGame();
    }

    private void loadCustomQuestions(String cats) {
        questions = FileHandler.getCustomQuestions(cats);
        Log.i("CIAO", "DOMANDE LETTE: "+questions.size());
        startGame();
    }


    private void requestCategory(){
            RequestCategory rC = new RequestCategory(this);
        rC.execute();
    }
    private void requestQuestion(String supportValue, String categoryName) {
        RequestQuestion rQ = new RequestQuestion(this,  supportValue, categoryName);
        rQ.execute();
    }

    public void showCategories(ArrayList<String> local, ArrayList<Category> server, ArrayList<String> custom){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Scaricate");
        listDataHeader.add("Server");
        listDataHeader.add("Personali");
        List<String> serverCats = new ArrayList<String>();
        for(Category c : server)
            serverCats.add(c.getName());
        listDataChild.put(listDataHeader.get(0), local); // Header, Child data
        listDataChild.put(listDataHeader.get(1), serverCats);
        listDataChild.put(listDataHeader.get(2), custom);

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
            downloadedCategories = gson.fromJson(result, type);
            for (Category cat : downloadedCategories) {
                Log.i("CIAO", cat.getId() + "-" + cat.getName());
            }
            List<String> cats = new ArrayList<String>();
            for(Category downCats : downloadedCategories)
                cats.add(downCats.getName());
            listDataChild.put(listDataHeader.get(1), cats);
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }
    public class RequestQuestion extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        Activity requested;
        String idCat;
        String categoryName;
        public RequestQuestion(Activity requested, String idCat, String categoryName) {
            this.requested = requested;
            this.idCat = idCat;
            this.categoryName = categoryName;
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
                Log.i("CIAO", "url -> "+url.toString());
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
                Log.i("CIAO", "Errore: non posso ricevere il file!");
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
            FileHandler.saveOfficialQuestions(requested, categoryName, questions);
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