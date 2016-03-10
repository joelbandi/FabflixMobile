package com.threefourfive.joel.fabflixmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
                search(query);
                movielist.setAdapter(adapter);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


    }

    //define the async search download task
    class MovieDownloadUpdateTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String result="";
            URL url = null;
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


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONArray arr = new JSONArray(result);

                for(int i =0; i<arr.length();i++){
                    String movie = arr.getString(i);
                    moviearray.add(movie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void search(String query){
        MovieDownloadUpdateTask task = new MovieDownloadUpdateTask();
        String IPaddr = "52.36.253.151";
        String url = "http://"+IPaddr+":8080/fabflix/typeahead?typeahead="+query;
        try {
            String received = task.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
