package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 03-Nov-16.
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
import android.widget.EditText;
import android.widget.LinearLayout;
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



public class FeedbackListFrag extends Fragment{
    LinearLayout layout;static String feedback_name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view=inflater.inflate(R.layout.feedbacklist_frag, container, false);
        layout=(LinearLayout) view.findViewById(R.id.feedbacklist);
        JSONArray feeds_and_assigns = CalFrag.json_array_of_course;
        String cc=CourseFrag.selected_course;int i;
        Toast.makeText(getContext(),cc, Toast.LENGTH_LONG).show();
        try {
            for ( i = 0; i < feeds_and_assigns.length(); i++) {
                JSONObject obj_course = feeds_and_assigns.getJSONObject(i);
                String coursecode = obj_course.getString("coursecode");
                if(coursecode.equals(cc))
                    break;
            }
            JSONObject abc = feeds_and_assigns.getJSONObject(i);
            JSONArray fb,assn;JSONObject tmp_obj;
            fb =  abc.getJSONArray("feedbackforms");
            assn = abc.getJSONArray("assignments");
            layout.addView(editText("FEEDBACKS"));

            for (i=0;i<fb.length();i++){
                tmp_obj=fb.getJSONObject(i);
                String s=tmp_obj.getString("name");
                String d=tmp_obj.getString("deadline");
                s=s+"         "+d.substring(0,d.indexOf('T'));
                layout.addView(button(s,i));
            }

            layout.addView(editText("\n\nASSIGNMENTS"));
            for (i=0;i<assn.length();i++){
                tmp_obj=assn.getJSONObject(i);
                String s=tmp_obj.getString("name");
                String d=tmp_obj.getString("deadline");
                s=s+"         "+d.substring(0,d.indexOf('T'));
                layout.addView(editText(s));
            }
        }
        catch (Exception e){}

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
                feedback_name=buttid.substring(0,buttid.indexOf(' '));
                ft.commit();

            }
        });
        return butt;
    }

    private TextView editText(String assgn)
    {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv=new TextView(getContext());
        tv.setLayoutParams(p);
        tv.setText(assgn);
        //layout.addView(tv);
        return tv;
    }
}
