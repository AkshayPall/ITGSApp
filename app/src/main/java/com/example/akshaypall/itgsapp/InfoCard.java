package com.example.akshaypall.itgsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class InfoCard
        extends ActionBarActivity
{

    private TextView mCardText;
    private String mColour;
    private String mCardTextString;
    public String mCardTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mCardTextString = extras.getString("TAG1");
            mColour = extras.getString("COLOUR");
            mCardTitle = extras.getString("TITLE");
        }
        mCardText = (TextView) findViewById(R.id.card_text);
        mCardText.setText(mCardTextString);
        mCardText.setBackgroundColor(Color.parseColor(mColour));
        getSupportActionBar().setTitle(mCardTitle);
    }

    @Override
    protected void onDestroy() {
        Toast exitToast = Toast.makeText(InfoCard.this, "Loading...", Toast.LENGTH_SHORT);
        exitToast.show();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
