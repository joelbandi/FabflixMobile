package com.threefourfive.joel.fabflixmobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class StarActivity extends AppCompatActivity {
    //var and view defintitions
    TextView first_name;
    TextView last_name;
    TextView dob;
    LinearLayout moviesection;
    ListView movielist;
    ArrayList<String> moviearray;
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get intent message as well
        Bundle bundle = getIntent().getExtras();
        String star = bundle.getString("star");
        Log.i("Intent received",star);

        //view and var instanstiation
        first_name = (TextView)findViewById(R.id.first_name);
        last_name = (TextView)findViewById(R.id.last_name);
        dob = (TextView)findViewById(R.id.dob);
        moviesection = (LinearLayout)findViewById(R.id.moviesection);
        moviesection.setVisibility(View.GONE);
        movielist = (ListView)findViewById(R.id.movielist);
        moviearray = new ArrayList<String>();
        adapter  = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,moviearray);


        //String result = setView(star);
        String result = "{\"movies\":[\"In Good Company\",\"Love Actually\"],\"dob\":\"1954-04-09\",\"last_name\":\"Quaid\",\"photo\":\"http://movies.yahoo.com/shop?d=hc&id=1800015473&cf=pg&photoid=552364&intl=us\",\"id\":\"48007\",\"first_name\":\"Dennis\"}";
        try {

            JSONObject obj = new JSONObject(result);
            first_name.append(obj.getString("first_name"));
            last_name.append(obj.getString("last_name"));
            dob.append(obj.getString("dob"));

            JSONArray arr = new JSONArray(obj.getString("movies"));
            Log.i("ARRAY",arr.toString());
            if(arr.length()!=0){
                moviesection.setVisibility(View.VISIBLE);
                moviearray.clear();
                for(int i=0; i<arr.length();i++){
                    moviearray.add(i, arr.getString(i));
                    Log.i("added",arr.getString(i));
                }

            }
            movielist.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unexpected server communication error ", Toast.LENGTH_SHORT).show();
        }



        movielist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MovieActivity.class);
                intent.putExtra("movie",moviearray.get(position));
                startActivity(intent);
            }
        });

    }



    class DownloadStarTask extends AsyncTask<String, Void, String> {

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


    public String setView(String star) {
        DownloadStarTask task = new DownloadStarTask();
        String IPaddr = "52.36.253.151";
//        String IPaddr = "192.168.0.5";
        String url = "http://" + IPaddr + ":8080/fabflix/api/star?star=" + star;
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
