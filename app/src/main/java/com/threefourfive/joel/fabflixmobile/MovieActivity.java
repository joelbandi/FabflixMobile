package com.threefourfive.joel.fabflixmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    //define view names and vars
    TextView title;
    TextView director;
    TextView year;
    ListView starlist;
    LinearLayout starsection;
    ArrayList<String> stararray;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //instantiate required views and vars
        title = (TextView) findViewById(R.id.title);
        director = (TextView) findViewById(R.id.director);
        year = (TextView) findViewById(R.id.year);
        starlist = (ListView) findViewById(R.id.starlist);
        starsection = (LinearLayout)findViewById(R.id.starsection);
        starsection.setVisibility(View.GONE);
        stararray = new ArrayList<String>();
        adapter  = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stararray);



        Bundle bundle = getIntent().getExtras();
        String movie = bundle.getString("name");
        Log.i("intent received",movie);
        //uncomment this...
        String result = setView(movie);
        try {
            //result="{\"trailer\":\"http://a2016.g.akamai.net/5/2016/51/cc0fd4ba52ea34/1a1a1aaa2198c627970773d80669d84574a8d80d3cb12453c02589f25382f668c9329e0375e8178cff608f0375d63ca20b6e/traffic_480.mov\",\"starnames\":[\"Michael Douglas\",\"Benicio Del Toro\",\"Catherine Zeta-Jones\"],\"year\":\"2000\",\"director\":\"Steven Soderbergh\",\"banner\":\"http://images.amazon.com/images/P/B000067IZ3.01.LZZZZZZZ.jpg\",\"id\":\"490011\",\"title\":\"Traffic\",\"genrenames\":[]}";
            JSONObject obj = new JSONObject(result);
            title.append(obj.getString("title"));
            year.append(obj.getString("year"));
            director.append(obj.getString("director"));

            JSONArray arr = new JSONArray(obj.getString("starnames"));
            Log.i("ARRAY",arr.toString());
            if(arr.length()!=0){
                starsection.setVisibility(View.VISIBLE);
                stararray.clear();
                for(int i=0; i<arr.length();i++){
                    stararray.add(i,arr.getString(i));
                    Log.i("added",arr.getString(i));
                }

            }
            starlist.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unexpected server communication error ", Toast.LENGTH_SHORT).show();
        }


    }

    class DownloadMovieTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    result += (char) data;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }


            return "Error while fetching results";
        }


    }


    public String setView(String movie) {
        DownloadMovieTask task = new DownloadMovieTask();
        String IPaddr = "52.36.253.151";
//        String IPaddr = "192.168.0.5";
        String url = "http://" + IPaddr + ":8080/fabflix/api/movie?movie=" + movie;
        url = url.trim();
        url = url.replaceAll("\\s+", "%20");


        Log.i("URL", url);
        try {
            String result = task.execute(url).get();
            if (result.equals("Error while fetching results")) {
                Toast.makeText(this, "Error while fetching. Please check connectivity", Toast.LENGTH_SHORT).show();
                return "Error while fetching. Please check connectivity";
            }
            Log.i("JSON ", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error while searching. Please check connectivity", Toast.LENGTH_SHORT).show();
            return "Error while searching. Please check connectivity ";
        }
    }
}



