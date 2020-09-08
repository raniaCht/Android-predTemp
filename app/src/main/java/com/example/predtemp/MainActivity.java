package com.example.predtemp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.text.*;

public class MainActivity extends AppCompatActivity  { //implements AdapterView.OnItemSelectedListener

    RequestQueue requestQueue;
    String url;

    DecimalFormat df = new DecimalFormat("#.##");


    String currentDate;
    String currentTime;

    TextView dateCurrenttxt;
    TextView timeCurrenttxt;

    DisplayMetrics displayMetrics;
    int height;
    int width;
    RelativeLayout acceilLayout;
    ViewGroup.LayoutParams params;

    TextView tempActuelTxt;
    ImageView imageBackground;

    TextView mintemp,maxtemp,moytemp;

    TextView [] tempsTxt;

    public Spinner spinner;
    public String [] strings;
    public int [] ints;
    public String ville;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ville ="";
        requestQueue = Volley.newRequestQueue(this);
        spinner = (Spinner)findViewById(R.id.spinner);



        tempsTxt = new TextView[24];
        tempsTxt[0] = (TextView) findViewById(R.id.temp01);
        tempsTxt[1] = (TextView) findViewById(R.id.temp02);
        tempsTxt[2] = (TextView) findViewById(R.id.temp03);
        tempsTxt[3] = (TextView) findViewById(R.id.temp04);
        tempsTxt[4] = (TextView) findViewById(R.id.temp05);
        tempsTxt[5] = (TextView) findViewById(R.id.temp6);
        tempsTxt[6] = (TextView) findViewById(R.id.temp7);
        tempsTxt[7] = (TextView) findViewById(R.id.temp8);
        tempsTxt[8] = (TextView) findViewById(R.id.temp9);
        tempsTxt[9] = (TextView) findViewById(R.id.temp10);
        tempsTxt[10] = (TextView) findViewById(R.id.temp11);
        tempsTxt[11] = (TextView) findViewById(R.id.temp12);
        tempsTxt[12] = (TextView) findViewById(R.id.temp13);
        tempsTxt[13] = (TextView) findViewById(R.id.temp14);
        tempsTxt[14] = (TextView) findViewById(R.id.temp15);
        tempsTxt[15] = (TextView) findViewById(R.id.temp16);
        tempsTxt[16] = (TextView) findViewById(R.id.temp17);
        tempsTxt[17] = (TextView) findViewById(R.id.temp18);
        tempsTxt[18] = (TextView) findViewById(R.id.temp19);
        tempsTxt[19] = (TextView) findViewById(R.id.temp20);
        tempsTxt[20] = (TextView) findViewById(R.id.temp21);
        tempsTxt[21] = (TextView) findViewById(R.id.temp22);
        tempsTxt[22] = (TextView) findViewById(R.id.temp23);
        tempsTxt[23] = (TextView) findViewById(R.id.temp24);


        maxtemp = (TextView) findViewById(R.id.maxtemp);
        mintemp = (TextView) findViewById(R.id.mintemp);
        moytemp = (TextView) findViewById(R.id.moytemp);


        currentDate = new SimpleDateFormat("EEE dd MMM", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;//acceilLayout
        acceilLayout = (RelativeLayout) findViewById(R.id.acceilLayout);
        params = acceilLayout.getLayoutParams();
        params.height = height;
        params.width = width;

        acceilLayout.setLayoutParams(params);


        dateCurrenttxt = (TextView) findViewById(R.id.dateCurrenttxt);
        timeCurrenttxt = (TextView) findViewById(R.id.timeCurrenttxt);



        tempActuelTxt = (TextView) findViewById(R.id.tempActuelTxt);
        imageBackground = (ImageView) findViewById(R.id.imageBackground);
        getTempActuel();




        final Thread thread = new Thread(){
            @Override
            public void run() {
                while (!isInterrupted()){
                    try {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                currentDate = new SimpleDateFormat("EEE dd MMM", Locale.getDefault()).format(new Date());
                                currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                dateCurrenttxt.setText(currentDate);
                                timeCurrenttxt.setText(currentTime);

                                String hour = currentTime.split(":")[0];
                                int hourInteger = Integer.parseInt(hour);
                                System.out.println(hourInteger);
                                float tempActuelInt = Float.parseFloat((tempActuelTxt.getText().toString()).split(" ")[0]);
                                if(hourInteger > 20 || hourInteger < 6){
                                    if (tempActuelInt <= 0){
                                        imageBackground.setImageResource(R.drawable.hiver_nuit);
                                    }

                                    if (tempActuelInt > 0 && tempActuelInt <= 30){
                                        imageBackground.setImageResource(R.drawable.nuit);
                                    }

                                    if (tempActuelInt > 30){
                                        imageBackground.setImageResource(R.drawable.autonme);
                                    }

                                }else{
                                    if (tempActuelInt <= 0){
                                        imageBackground.setImageResource(R.drawable.hiver_jour);
                                    }

                                    if (tempActuelInt > 0 && tempActuelInt <= 30){
                                        imageBackground.setImageResource(R.drawable.printemps);
                                    }


                                    if (tempActuelInt > 30){
                                        imageBackground.setImageResource(R.drawable.summer);
                                    }
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
        getRepoList();




        handler.post(runnableCode);





        //List<String> list = new ArrayList<String>();
        List<Integer> listID = new ArrayList<Integer>();
        final List<String> listTitre = new ArrayList<String>();

        url = "http://192.168.43.175:8000/models/noms-villes/";
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            String list = "";
                            strings = new String[response.length()+1];
                            ints = new int[response.length()+1];
                            float min,max;
                            float moy = 0;
                            strings[0] = "Selectionner ville : ";
                            ints[0] = -1;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);

                                    int listID = Integer.valueOf(jsonObj.get("id").toString());
                                    String listTitre = String.valueOf(jsonObj.get("titre"));
                                    strings[i+1] = listTitre;
                                    ints[i+1] = listID;

                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }


                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                    android.R.layout.simple_spinner_item,strings);
                            spinner.setAdapter(adapter);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            adapter.notifyDataSetChanged();


                        } else {
                            // The user didn't have any repos.
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }



                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    Toast.makeText(adapterView.getContext(),strings[i],Toast.LENGTH_LONG).show();
                    tempville(ints[i]);
                    tempactuelleville(ints[i]);
                }else{
                    getRepoList();
                    getTempActuel();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.e("messsage ========= ","ani hanaaaaaaaaaa ==========");
            }
        });
    }

    Handler handler = new Handler();

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if(ville == ""){
                getTempActuel();
                getRepoList();
            }
            handler.postDelayed(runnableCode, 500000);
        }
    };

    private void getRepoList() {
        url = "http://192.168.43.175:8000/models/multi/";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            String list = "";
                            float min,max;
                            float moy = 0;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    float repoName = Float.valueOf(df.format(jsonObj.get("temperature")).replace(",", "."));

                                    tempsTxt[i].setText(String.valueOf(repoName));
                                    moy += repoName;
                                    list += String.valueOf(repoName);
                                    Log.e("predictions", String.valueOf(repoName));

                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                            moytemp.setText(df.format(moy/tempsTxt.length).replace(",", "."));
                            min = Float.parseFloat(tempsTxt[0].getText().toString());
                            max = Float.parseFloat(tempsTxt[0].getText().toString());
                            for (int i = 1; i<tempsTxt.length; i++){
                                if (max < Float.parseFloat(tempsTxt[i].getText().toString())){
                                    max = Float.parseFloat(tempsTxt[i].getText().toString());
                                }
                                if (min > Float.parseFloat(tempsTxt[i].getText().toString())){
                                    min = Float.parseFloat(tempsTxt[i].getText().toString());
                                }
                            }
                            maxtemp.setText(String.valueOf(max));
                            mintemp.setText(String.valueOf(min));
                            Toast.makeText(getApplicationContext(), list,Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);
    }






    private void getTempActuel() {

        url = "http://192.168.43.175:8000/models/temperature-actuelle/";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    float repoName = Float.valueOf(df.format(jsonObj.get("temperature_current")).replace(",", "."));

                                    tempActuelTxt.setText(String.valueOf(repoName));
                                    Log.e("predictions", String.valueOf(repoName));

                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        } else {
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);
    }


    public void tempville(int idville){
        url = "http://192.168.43.175:8000/models/temperature-autre-ville/?modelchoisi="+idville;

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            String list = "";
                            float min,max;
                            float moy = 0;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    float repoName = Float.valueOf(df.format(jsonObj.get("temperature")).replace(",", "."));

                                    tempsTxt[i].setText(String.valueOf(repoName));
                                    moy += repoName;
                                    list += String.valueOf(repoName);
                                    Log.e("predictions", String.valueOf(repoName));

                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                            moytemp.setText(df.format(moy/tempsTxt.length).replace(",", "."));
                            min = Float.parseFloat(tempsTxt[0].getText().toString());
                            max = Float.parseFloat(tempsTxt[0].getText().toString());
                            for (int i = 1; i<tempsTxt.length; i++){
                                if (max < Float.parseFloat(tempsTxt[i].getText().toString())){
                                    max = Float.parseFloat(tempsTxt[i].getText().toString());
                                }
                                if (min > Float.parseFloat(tempsTxt[i].getText().toString())){
                                    min = Float.parseFloat(tempsTxt[i].getText().toString());
                                }
                            }
                            maxtemp.setText(String.valueOf(max));
                            mintemp.setText(String.valueOf(min));
                            Toast.makeText(getApplicationContext(), list,Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                900000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);

    }


    public void tempactuelleville(int idville){
        url = "http://192.168.43.175:8000/models/temperature-actuelle-autre-ville/?modelchoisi="+idville;
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    float repoName = Float.valueOf(df.format(jsonObj.get("temperature_current")).replace(",", "."));

                                    tempActuelTxt.setText(String.valueOf(repoName));
                                    Log.e("predictions", String.valueOf(repoName));

                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        } else {
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);
    }

   /* private void getVilleList(final List<Integer> listID) {
        url = "http://192.168.43.175:8000/models/noms-villes/";
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            String list = "";
                            float min,max;
                            float moy = 0;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);

                                    listID.add(Integer.valueOf(jsonObj.get("id").toString()));
                                    listTitre.add(String.valueOf(jsonObj.get("titre")));
                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }


                        } else {
                            // The user didn't have any repos.
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //int position = spinner.getSelectedItemPosition();
        Toast.makeText(adapterView.getContext(), "nandab",Toast.LENGTH_LONG).show();
        spinner.setSelection(i);
        url = "http://192.168.43.175:8000/models/temperature-autre-ville/?modelchoisi="+listID.get(i);

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            String list = "";
                            float min,max;
                            float moy = 0;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    float repoName = Float.valueOf(df.format(jsonObj.get("temperature")).replace(",", "."));

                                    tempsTxt[i].setText(String.valueOf(repoName));
                                    moy += repoName;
                                    list += String.valueOf(repoName);
                                    Log.e("predictions", String.valueOf(repoName));

                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                            moytemp.setText(df.format(moy/tempsTxt.length).replace(",", "."));
                            min = Float.parseFloat(tempsTxt[0].getText().toString());
                            max = Float.parseFloat(tempsTxt[0].getText().toString());
                            for (int i = 1; i<tempsTxt.length; i++){
                                if (max < Float.parseFloat(tempsTxt[i].getText().toString())){
                                    max = Float.parseFloat(tempsTxt[i].getText().toString());
                                }
                                if (min > Float.parseFloat(tempsTxt[i].getText().toString())){
                                    min = Float.parseFloat(tempsTxt[i].getText().toString());
                                }
                            }
                            maxtemp.setText(String.valueOf(max));
                            mintemp.setText(String.valueOf(min));
                            Toast.makeText(getApplicationContext(), list,Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("error","No repos found.");
                            Toast.makeText(getApplicationContext(), "not found",Toast.LENGTH_LONG).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );

        arrReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(arrReq);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }*/
}