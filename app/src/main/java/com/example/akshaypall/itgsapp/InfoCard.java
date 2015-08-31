package com.example.akshaypall.itgsapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class InfoCard extends ActionBarActivity {

    private TextView mCardText;
    private String mColour;
    private String mCardTextString;
    public String mCardTitle;
    private int mOriginalColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_card);
        Bundle extras = getIntent().getExtras();
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
        mCardText = (TextView) findViewById(R.id.card_text);
        mCardText.setText(mCardTextString);
        //if actionbar is yellow or white, set title font colour to black
        if(mColour.equals("#FFFAF0") || mColour.equals("#FFFF00")){
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>" + mCardTitle + "</font>"));
        }else {
            getSupportActionBar().setTitle(mCardTitle);
        }
        System.out.println(mColour + " is color");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mColour)));
    }

    @Override
    protected void onDestroy() {
        setStatusBarBackground(mOriginalColour); //colour would have changed, so sets it back to the original colour for previous activity
        Toast exitToast = Toast.makeText(InfoCard.this, "Loading...", Toast.LENGTH_SHORT);
        exitToast.show();
        super.onDestroy();
    }

    private void setStatusBarBackground(int color) {
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_card, menu);
        return true;
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
