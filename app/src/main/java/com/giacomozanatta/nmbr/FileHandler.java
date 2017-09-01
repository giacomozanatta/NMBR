package com.giacomozanatta.nmbr;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by giaco on 8/31/2017.
 */

public class FileHandler {


    public static ArrayList<String> getCustomCategories() {
        /*
            leggere da ext
            1. controllo se è possibile leggere
            2. leggo dalla memoria i nomi dei file, e parso il nome su un AL <String>
         */
        String state = Environment.getExternalStorageState();
        ArrayList<String> categories = new ArrayList<>();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) || Environment.MEDIA_MOUNTED.equals(state)) {
            /*si può leggere e scrivere nella memoria.
             */
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/nmbr/custom_questions");
            /*
            controllo se esiste la directory, se non esiste allora ritorno un arrayList vuoto
             */
            if (directory.exists()) {
                ObjectInputStream input;
                /*esiste la directory. Cerco i nomi dei file e li parso.*/
                File[] customCates = directory.listFiles();
                for (File f : customCates) {
                    /* E' indifferente la lingua per le categorie locali. Nome del file senza estensione -> nome categoria, contenuto -> domande */
                    categories.add(f.getName().substring(0, f.getName().lastIndexOf(".")));
                }
            }
        }
        return categories;
    }

    public static void saveCustomQuestions(Context context, String category, ArrayList<Question> questions) {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/nmbr/custom_questions");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Category>>() {
        }.getType();
        String json = gson.toJson(questions, type);
        if (!directory.exists())
            directory.mkdir();
        try {
            BufferedWriter outputStreamWriter = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/nmbr/custom_questions" + category + ".txt"));
            outputStreamWriter.write(json);
            Log.i("CIAO", "Scritte domande su file!");
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static ArrayList<Question> getCustomQuestions(String category) {
        String state = Environment.getExternalStorageState();
        ArrayList<Question> questions = new ArrayList<>();
        String lineRead = "";
        StringBuilder builder = new StringBuilder("");
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            BufferedReader bufferedReader = null;
            try {
                BufferedReader bR = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/nmbr/custom_questions/" + category + ".txt")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                while ((lineRead = bufferedReader.readLine()) != null) {
                    builder.append(lineRead);
                }
                /*build contiene le domande in JSON -> da fare il parsing.*/
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Question>>() {
                }.getType();
                Log.i("CIAO", "JSOOON" + questions);
                questions = gson.fromJson(builder.toString(), type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return questions;
    }

    public static ArrayList<String> getOfficialCategories(Context context) {
        /*get official category ritorna le categorie ufficiali. Queste, a differenza di quelle create dall'utente, vengono salvate sulla memoria interna.*/
        ArrayList<String> categories = new ArrayList<>();
        File directory = new File(context.getFilesDir() + "/questions");
        if (directory.exists()) {
            ObjectInputStream input;
                /*esiste la directory. Cerco i nomi dei file e li parso.*/
            File[] customCates = directory.listFiles();
            for (File f : customCates) {
                    /* E' indifferente la lingua per le categorie locali. Nome del file senza estensione -> nome categoria, contenuto -> domande */
                categories.add(f.getName().substring(0, f.getName().lastIndexOf("_")));
            }
        }
        return categories;
    }

    public static void saveOfficialQuestions(Context context, String category, ArrayList<Question> questions) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Category>>() {
            }.getType();
            String json = gson.toJson(questions, type);
            File directory = new File(context.getFilesDir() + "/questions");
            if (!directory.exists())
                directory.mkdir();
            try {
                BufferedWriter outputStreamWriter = new BufferedWriter(new FileWriter(context.getFilesDir() + "/questions/" + category + "_" +Locale.getDefault().getLanguage()+ ".txt"));
                outputStreamWriter.write(json);
                Log.i("CIAO", "Scritte domande su file!");
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }


    public static ArrayList<Question> getOfficialQuestions(Context context, String category) {
        ArrayList<Question> questions = new ArrayList<>();
        String lineRead = "";
        StringBuilder builder = new StringBuilder("");
        try {
            BufferedReader bW = new BufferedReader(new FileReader(context.getFilesDir() + "/questions/" + category + "_" + Locale.getDefault().getLanguage() + ".txt"));
            while ((lineRead = bW.readLine()) != null) {
                builder.append(lineRead);
            }
            /*build contiene le domande in JSON -> da fare il parsing.*/
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Question>>() {
            }.getType();
            Log.i("CIAO", "JSON" + questions);
            questions = gson.fromJson(builder.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;

    }
}

