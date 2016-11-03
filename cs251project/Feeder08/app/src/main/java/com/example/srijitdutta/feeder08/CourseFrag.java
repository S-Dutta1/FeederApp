package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 30-Oct-16.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View.OnClickListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class CourseFrag extends Fragment{
    LinearLayout linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view=inflater.inflate(R.layout.coursefrag, container, false);
        linearLayout=(LinearLayout) view.findViewById(R.id.coursepage);
//        String ids[]={"CS207","CS215","CS293","CS251","CS225"};
//        for(int i=0;i<5;i++) {
//            linearLayout.addView(button(ids[i],i));
//        }
        new SendPostRequest().execute();

        return(view);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Courses");
    }
    private Button button(String id, int i){
        final ActionBar.LayoutParams lparams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        final Button butt = new Button(getContext());
        butt.setLayoutParams(lparams);
        butt.setText(id);

        //String col[] = {"#bfccd7", "#a8e6b0", "#a8e6cf", "#0ca5ea", "#5a5fbe", "#249393"};
        //butt.setBackgroundColor(Color.parseColor(col[i]));
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttid = butt.getText().toString();
                Fragment fragment = new FeedbackFrag();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        return butt;
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
                String roll=MainActivity.user1;
                String uril="http://192.168.0.107:8008/feeder/"+"coursenames/"+roll+"/";
            try {

                URL url = new URL(uril); // here is your URL path

//                JSONObject postDataParams = new JSONObject();
//                postDataParams.put("rollno", user1);
//                postDataParams.put("password", pass1);
//                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(9000 /* milliseconds */);
                conn.setConnectTimeout(9000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(getPostDataString(postDataParams));
//
//                writer.flush();
//                writer.close();
//                os.close();


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
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("courses");
                for(int i=0;i< jsonArray.length() ;i++)
                {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String coursecode = obj.getString("coursecode");
                    linearLayout.addView(button(coursecode,2));
                }

//                Toast.makeText(getContext(), result,
//                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){}
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
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
