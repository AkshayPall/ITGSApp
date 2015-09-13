package com.example.akshaypall.itgsapp;


import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoCardFragment extends Fragment {

    private TextView mCardText;
    private String mColour;
    private String mCardTextString;
    public String mCardTitle;
    private int mOriginalColour;

    public InfoCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info_card, container, false);

        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

        Bundle extras = getArguments();
        if (extras != null) {
            mCardTextString = extras.getString("TAG1");
            mColour = extras.getString("COLOUR");
            mCardTitle = extras.getString("TITLE");
        }
        mOriginalColour = R.color.primary_dark;
        if (Build.VERSION.SDK_INT >= 21){ //converts colour received to one a bit darker for status bar
            float[] hsv = new float[3];
            int color = Color.parseColor(mColour);
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            color = Color.HSVToColor(hsv);
            setStatusBarBackground(color);
        }
        mCardText = (TextView) v.findViewById(R.id.card_text);
        mCardText.setText(mCardTextString);
        //if actionbar is yellow or white, set title font colour to black
        if(mColour.equals("#FFFAF0") || mColour.equals("#FFFF00")){
            actionBar.setTitle(Html.fromHtml("<font color='#000000'>" + mCardTitle + "</font>"));
        }else {
            actionBar.setTitle(mCardTitle);
        }
        System.out.println(mColour + " is color");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(mColour)));

        return v;
    }


    @Override
    public void onDestroy() {
        setStatusBarBackground(mOriginalColour); //colour would have changed, so sets it back to the original colour for previous activity
        Toast exitToast = Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT);
        exitToast.show();
        super.onDestroy();
    }

    private void setStatusBarBackground(int color) {
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(color);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_info_card, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
