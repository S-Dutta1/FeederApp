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
    static String selected_course;
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
        JSONArray courses=CalFrag.json_array_of_course;
        try {
            for (int i = 0; i < courses.length(); i++) {
                JSONObject obj_course = courses.getJSONObject(i);
                String coursecode = obj_course.getString("coursecode");
                linearLayout.addView(button(coursecode,i));
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
                Fragment fragment = new FeedbackListFrag();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.addToBackStack(null);
                selected_course=butt.getText().toString();
                ft.commit();

            }
        });
        return butt;
    }

}
