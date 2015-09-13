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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_card);

        if (getFragmentManager().findFragmentById(R.id.info_card_fragment_container) == null){
            InfoCardFragment infoCardFragment = new InfoCardFragment();
            infoCardFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.info_card_fragment_container, infoCardFragment)
                    .commit();
        }
    }
}
