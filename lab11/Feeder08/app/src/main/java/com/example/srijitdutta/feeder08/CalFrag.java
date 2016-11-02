package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 30-Oct-16.
 */
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import static android.R.color.black;
import static android.R.color.holo_green_light;

public class CalFrag extends Fragment {
    MaterialCalendarView materialCalendarView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view=inflater.inflate(R.layout.calfrag, container, false);
        materialCalendarView=(MaterialCalendarView)view.findViewById(R.id.calendarView);
        materialCalendarView.setOnDateChangedListener(new Clickdate());
        materialCalendarView.addDecorator(new EventDecorator(black));
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
            if(chec)
            {
                Snackbar.make(getView(),"Had a snack at Snackbar", Snackbar.LENGTH_LONG).make(getView(),"Had a snack at Snackbar", Snackbar.LENGTH_LONG)

                        .setActionTextColor(Color.RED)
                        .show();
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                Dialog dialog=new Dialog(getContext());
                dialog.setContentView(R.layout.pop_up_event);
                dialog.addContentView(tw("Event"),layoutParams);
                dialog.show();
            }
        }
    }

    private TextView tw(String s)
    {
        final TextView textView=new TextView(getContext());
        textView.setText(s);
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
            float radius=8;
            view.addSpan(new DotSpan(radius));
        }
    }

}




