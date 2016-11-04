package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 30-Oct-16.
 */
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import android.view.View;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class CalFrag extends Fragment {
    MaterialCalendarView materialCalendarView;
    static JSONArray json_array_of_course;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view=inflater.inflate(R.layout.calfrag, container, false);
        materialCalendarView=(MaterialCalendarView)view.findViewById(R.id.calendarView);
        materialCalendarView.setOnDateChangedListener(new Clickdate());
        materialCalendarView.addDecorator(new EventDecorator(Color.RED));//set color for dots
        materialCalendarView.addDecorator(new OneDayDecorator());
        new SendPostRequest().execute();
        return(view);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Calendar");

    }

    public class Clickdate implements OnDateSelectedListener {
        @Override
        public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected)
        {
            EventDecorator evd=new EventDecorator(0);
            boolean chec=evd.shouldDecorate(date);
            Snackbar snackbar=Snackbar.make(getView(),"Event 1 \nEvent 2 \nEvent 3 ", Snackbar.LENGTH_LONG).
                    setActionTextColor(Color.GREEN).setAction("EXPAND", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                            Dialog dialog=new Dialog(getContext());
                            dialog.setContentView(R.layout.pop_up_event);
                            String str_to_dialog="Event";
                            dialog.addContentView(tw(str_to_dialog),layoutParams);
                            dialog.show();
                        }
                    });
            if(chec)
            {

                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);
                snackbar.show();

            }
            else
            {
                snackbar.dismiss();
            }
        }
    }

    private TextView tw(String s)
    {
        final TextView textView=new TextView(getContext());
        textView.setText(s);
        textView.setTextSize(24);
        textView.setPadding(100,0,15,15);
        return  textView;
    }

    public class EventDecorator implements DayViewDecorator {

        private final int color;
        //private final HashSet<CalendarDay> dates;

        public EventDecorator(int color) {
            this.color = color;
            //this.dates =i;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            int k=day.getDay();
            return (k%5==0);
        }

        @Override
        public void decorate(DayViewFacade view) {
            float radius=5;
            view.addSpan(new DotSpan(radius,color));
        }
    }
    ////////////////mark today////////////////////
    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;private final Drawable highlightDrawable;private final int color = Color.parseColor("#228BC34A");
        public OneDayDecorator() {
            date = CalendarDay.today();
            highlightDrawable = new ColorDrawable(color);
        }
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }
        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(highlightDrawable);
        }
    }

    ///////////////////////////////getting json/////////////////////////////
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            String roll=SaveSharedPreference.getUserName(getContext());//MainActivity.user1;
            String uril="http://10.5.41.211:8008/feeder/"+"getstudentdata/"+roll+"/";
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
                json_array_of_course = jsonObject.getJSONArray("courses");
                for(int i=0;i< json_array_of_course.length() ;i++)
                {
                    JSONObject obj_course = json_array_of_course.getJSONObject(i);
                    String coursecode = obj_course.getString("coursecode");
                    //Toast.makeText(getContext(), coursecode, Toast.LENGTH_LONG).show();
                    JSONArray feedback_deadlines = obj_course.getJSONArray("feedbackforms");
                    for(int j=0;i<feedback_deadlines.length();j++)
                    {
                        JSONObject feedc=feedback_deadlines.getJSONObject(j);
                        String dead=feedc.getString("deadline");
                        //Toast.makeText(getContext(), dead, Toast.LENGTH_LONG).show();
                    }
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




