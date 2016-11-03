package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 30-Oct-16.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View.OnClickListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class CourseFrag extends Fragment{
    LinearLayout linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view=inflater.inflate(R.layout.coursefrag, container, false);
        linearLayout=(LinearLayout) view.findViewById(R.id.coursepage);
        String ids[]={"CS207","CS215","CS293","CS251","CS225"};
        for(int i=0;i<5;i++) {
            linearLayout.addView(button(ids[i],i));
        }

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

}
