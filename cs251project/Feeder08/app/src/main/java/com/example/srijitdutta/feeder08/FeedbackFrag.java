package com.example.srijitdutta.feeder08;

/**
 * Created by SRIJIT DUTTA on 31-Oct-16.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

public class FeedbackFrag extends Fragment{

    LinearLayout layout;String response;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.feedbackfrag, container, false);
        layout = (LinearLayout) view.findViewById(R.id.feedbackpage);
        layout.addView(radioGroup("How was the midsem?"));
        layout.addView(radioGroup("How was the endsem?"));
        layout.addView(editText("Other Suggestion"));
//        if(layout.getParent()!=null)
//            ((ViewGroup)layout.getParent()).removeView(layout);
        //layout.addView(editText("Other Suggestions"));
        return(view);
    }

    private RadioGroup radioGroup(String ques)
    {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final RadioGroup rg=new RadioGroup(getContext());
        rg.setOrientation(LinearLayout.HORIZONTAL);
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
            rg.addView(radioButtonView, p);
            //((ViewGroup)layout.getParent()).removeView(layout);
        }

        return rg;
    }

    private OnClickListener mThisButtonListener = new OnClickListener() { ////// for radio button
        public void onClick(View v) {
            response = ((RadioButton) v).getText().toString();
            Toast.makeText(getContext(), response,
                    Toast.LENGTH_LONG).show();
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

}
