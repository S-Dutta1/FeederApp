package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 31-Oct-16.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class FeedbackFrag extends Fragment{

    LinearLayout layout;String response;JSONArray json_array_of_ques;String final_response="";RadioGroup rg_arr[];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.feedbackfrag, container, false);
        layout = (LinearLayout) view.findViewById(R.id.feedbackpage);
//        layout.addView(radioGroup("How was the midsem?"));
//        layout.addView(radioGroup("How was the endsem?"));
//        layout.addView(editText("Other Suggestion"));
        new SendPostRequest().execute();

        return(view);
    }

    private RadioGroup radioGroup(String ques,int id,int k)
    {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rg_arr[k]=new RadioGroup(getContext());
        rg_arr[k].setOrientation(LinearLayout.HORIZONTAL);
        rg_arr[k].setId(id);
        String options[]={"1","2","3","4","5"};
        TextView tv=new TextView(getContext());
        tv.setLayoutParams(p);
        tv.setText(ques);
        layout.addView(tv);
        for(int i =0; i < options.length; i++)
        {
            RadioButton radioButtonView = new RadioButton(getContext());
            radioButtonView.setOnClickListener(mThisButtonListener);
            radioButtonView.setText(options[i]);
            rg_arr[k].addView(radioButtonView, p);

            //((ViewGroup)layout.getParent()).removeView(layout);
        }

        return rg_arr[k];
    }

    private OnClickListener mThisButtonListener = new OnClickListener() { ////// for radio button
        public void onClick(View v) {
//            response =rg.getId()+""; //((RadioButton) v).getText().toString();
//            Toast.makeText(getContext(), response,
//                    Toast.LENGTH_LONG).show();
        }
    };
    ///////////edit text field/////////////
    private EditText editText(String ques)
    {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText ed=new EditText(getContext());
        ed.setLayoutParams(p);
        ed.setHint("Your Response");

        TextView tv=new TextView(getContext());
        tv.setLayoutParams(p);
        tv.setText(ques);
        layout.addView(tv);
        //((ViewGroup)layout.getParent()).removeView(layout);
        //layout.addView(tv);

//        layout.addView(ed);

        return ed;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Feedback");

    }


    /////////////////////////////////////

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            String cc=CourseFrag.selected_course;String feedname=FeedbackListFrag.feedback_name;
            String uril="http://192.168.0.104:8008/feeder/"+"getquestions/"+cc+"/"+feedname+"/";
            try {

                URL url = new URL(uril); // here is your URL path


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(9000 /* milliseconds */);
                conn.setConnectTimeout(9000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


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
                json_array_of_ques = jsonObject.getJSONArray("questions");
                rg_arr=new RadioGroup[json_array_of_ques.length()];
                for(int i=0;i< json_array_of_ques.length() ;i++)
                {
                    JSONObject obj_tmp = json_array_of_ques.getJSONObject(i);
                    String question = obj_tmp.getString("text");
                    layout.addView(radioGroup(question,i,i));

                }
                layout.addView(button("a",5));
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

    ///////////button////////////////
    private Button button(String id, int i){
        final ActionBar.LayoutParams lparams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        final Button butt = new Button(getContext());
        butt.setLayoutParams(lparams);
        butt.setText("Submit");

        //String col[] = {"#bfccd7", "#a8e6b0", "#a8e6cf", "#0ca5ea", "#5a5fbe", "#249393"};
        //butt.setBackgroundColor(Color.parseColor(col[i]));
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            for(int hh=0;hh<json_array_of_ques.length();hh++)
            {
                final_response+=(((rg_arr[hh].getCheckedRadioButtonId()-1)%5)+1)+"#";
            }
                final_response.substring(0,final_response.length()-1);
                //Toast.makeText(getContext(), final_response,Toast.LENGTH_LONG).show();
                new SendPostRequest1().execute();
                Fragment fragment = new FeedbackListFrag();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.addToBackStack(null);
                ft.commit();
            }

        });
        return butt;
    }
    ///////////////////////////////////sending feedback//////////////////////////////////////////////////////



    public class SendPostRequest1 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            String uril="http://192.168.0.104:8008/feeder/sentfeedback/";
            try {

                URL url = new URL(uril); // here is your URL path
//                Toast.makeText(getContext(), MainActivity.user1,
//                        Toast.LENGTH_LONG).show();
//                Toast.makeText(getContext(), CourseFrag.selected_course,
//                        Toast.LENGTH_LONG).show();
//                Toast.makeText(getContext(), FeedbackListFrag.feedback_name,
//                        Toast.LENGTH_LONG).show();
//                Toast.makeText(getContext(), final_response,
//                        Toast.LENGTH_LONG).show();
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("rollno", SaveSharedPreference.getUserName(getContext()));
                //System.out.println(MainActivity.user1);
                postDataParams.put("coursecode", CourseFrag.selected_course);
                //System.out.println(CourseFrag.selected_course);
                postDataParams.put("feedbackname", FeedbackListFrag.feedback_name);
                //System.out.println(FeedbackListFrag.feedback_name);
                postDataParams.put("feedbackdata", final_response);
                final_response="";
                //System.out.println(final_response);
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
            try {
                Toast.makeText(getContext(), result,
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){}
        }
    }


}
