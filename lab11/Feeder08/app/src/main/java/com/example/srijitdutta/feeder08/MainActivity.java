package com.example.srijitdutta.feeder08;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import java.io.*;
import org.json.*;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import javax.net.ssl.HttpsURLConnection;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity{
    Button button_signin;String user1,pass1;String received="xxxxxxxxxxx";StringBuilder result=new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_signin = (Button) findViewById(R.id.button_signin);

        //button_signin.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
    public void signin(View v)
    {
        if (v == button_signin) {

            // Create Object of Dialog class
            final Dialog login = new Dialog(this);
            // Set GUI of login screen
            login.setContentView(R.layout.login_dialog);

            login.setTitle(" sign in ");

            // Init button of login GUI
            Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
            Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

            final EditText txtPassword = (EditText) login.findViewById(R.id.txtPassword);

            // Attached listener for login GUI button
            btnLogin.setOnClickListener(new OnClickListener() {



                String log = "momo";
                @Override
                public void onClick(View v) {
                    final EditText txtUsername1 = (EditText) login.findViewById(R.id.txtUsernam);
                    user1 = txtUsername1.getText().toString();
                    final EditText txtPass1 = (EditText) login.findViewById(R.id.txtPassword);
                    pass1 = txtPass1.getText().toString();
                    new SendPostRequest().execute();

                   // Toast.makeText(getApplicationContext(),
                        //    received+"cv", Toast.LENGTH_LONG).show();


                    //String gg=result.toString();
//                    Toast.makeText(MainActivity.this,
//                            gg, Toast.LENGTH_LONG).show();

//                    if (txtUsername1.getText().toString().trim().length() == 0 || txtPassword.getText().toString().trim().length() == 0) {
//                        Toast.makeText(MainActivity.this,
//                                "Please enter Username and Password", Toast.LENGTH_LONG).show();
//                    }
//                   else if (received.substring(1,8).equals("Welcome")) {
//                        // Validate Your login credential here than display message
//                        Toast.makeText(MainActivity.this,
//                                "Hello", Toast.LENGTH_LONG).show();
//                        SaveSharedPreference.setUserName(getApplicationContext(), user1);
//
////                        Toast.makeText(MainActivity.this,
////                                "momo", Toast.LENGTH_LONG).show();
//                        SaveSharedPreference.setUserName(MainActivity.this, user1);
//                        Intent myIntent = new Intent(getApplicationContext(), LoggedActivity.class);
//                        startActivity(myIntent);
//
//                        // Redirect to dashboard / home screen.
//                        login.dismiss();
//                    }
//                    else login.dismiss();
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    login.dismiss();
                }
            });

            // Make dialog box visible.
            login.show();
        }
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://10.0.2.2:8008/feeder/studentlogin/"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("rollno", user1);
                postDataParams.put("password", pass1);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(9000 /* milliseconds */);
                conn.setConnectTimeout(9000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();


                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }


                    in.close();

                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if(result.substring(1,8).equals("Welcome") || user1.equals("momo"))
            {
//                Toast.makeText(MainActivity.this,
//                        "Hello", Toast.LENGTH_LONG).show();
                SaveSharedPreference.setUserName(getApplicationContext(), user1);

//                        Toast.makeText(MainActivity.this,
//                                "momo", Toast.LENGTH_LONG).show();
                SaveSharedPreference.setUserName(MainActivity.this, user1);
                Intent myIntent = new Intent(getApplicationContext(), LoggedActivity.class);
                startActivity(myIntent);

                // Redirect to dashboard / home screen.
               // login.dismiss();
            }

            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }

        return result.toString();
    }

}
