package com.threefourfive.joel.fabflixmobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {






    EditText email;
    EditText pass;
    static String IPaddr = "192.168.0.5";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Email me at jbandi@uci.edu", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onLoginClick(View view) {
        String thisemail = email.getText().toString();
        String thispass = pass.getText().toString();
        boolean Auth = false;
        if (thisemail.equals("") || thisemail.equals(null) || thispass.equals("") || thispass.equals(null)) {
            Toast.makeText(MainActivity.this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            Auth = authenticate(thisemail, thispass);
            if(Auth) {
                Log.i("SERVER RETURNED ", "true");
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }else{
                Log.i("SERVER RETURNED ", "false");
                Toast.makeText(MainActivity.this, "Email and/or Password is wrong", Toast.LENGTH_SHORT).show();

            }
        }


    }


    public class AuthenticateTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection = null;
            String result ="";
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }

    public Boolean authenticate(String email, String pass) {
        AuthenticateTask task = new AuthenticateTask();
        String result = "";
        String url = "http://"+IPaddr+":8080/fabflix/api/login?email="+email+"&pass="+pass;
        //String url = "http://www.joelbandi.me/";

        Log.i("URL is: ",url);
        try {
            result = task.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.valueOf(result);
    }


}
