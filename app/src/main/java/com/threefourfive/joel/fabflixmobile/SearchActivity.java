package com.threefourfive.joel.fabflixmobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    //variables and object definition
    SearchView searchbar;
    ListView movielist;
    ArrayList<String> moviearray;
    ArrayAdapter<String> adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // variable and object init
        searchbar = (SearchView)findViewById(R.id.searchbar);
        movielist = (ListView)findViewById(R.id.movielist);
        moviearray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, moviearray);


        //define and create a listener of the event
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {

                String result = search(query);
                try {
                    JSONArray arr = new JSONArray(result);
                    moviearray.clear();
                    for(int i=0; i<arr.length();i++){
                        moviearray.add(arr.getString(i));
                    }

                    movielist.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                }


                movielist.setAdapter(adapter);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        //define a new listener/ logic to handle movie list individual clicks


        movielist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),moviearray.get(position),Toast.LENGTH_SHORT).show();
                String name = moviearray.get(position);
                Intent intent  = new Intent(getApplicationContext(), MovieActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });










    }

    //define the async search download task
    class MovieDownloadUpdateTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String result="";
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    result+=(char)data;
                    data = reader.read();
                }
                return result;

            }catch(Exception e){
                e.printStackTrace();
            }


            return "Error while fetching results";
        }


    }

    public String search(String query){
        MovieDownloadUpdateTask task = new MovieDownloadUpdateTask();
        String IPaddr = "52.36.253.151";
//        String IPaddr = "192.168.0.5";
        String url = "http://"+IPaddr+":8080/fabflix/typeahead?typeahead="+query;
        url = url.trim();
        url = url.replaceAll("\\s+", "%20");


//        String url = "http://www.joelbandi.me/";
        Log.i("URL",url);
        try {
            String result = task.execute(url).get();
            if(result.equals("Error while fetching results")){
                Toast.makeText(this,"Error while fetching. Please check connectivity",Toast.LENGTH_SHORT).show();
                return "Error while fetching. Please check connectivity";
            }
            Log.i("JSON ",result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Error while searching. Please check connectivity",Toast.LENGTH_SHORT).show();
            return "Error while searching. Please check connectivity ";
        }
    }
    /*--------------------------------------------------------*/







}
