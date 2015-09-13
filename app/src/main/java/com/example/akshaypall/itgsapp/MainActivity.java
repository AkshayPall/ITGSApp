package com.example.akshaypall.itgsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.parse.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity implements MainFragment.Contract{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getFragmentManager().findFragmentById(R.id.main_list_fragment_container) == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.main_list_fragment_container, new MainFragment())
                    .commit();
        }
    }

    @Override
    public void selectedSEIPostion(int position, int seiIdToPass) {
        Intent i = new Intent(this, CardsActivity.class);
        Bundle extras = new Bundle();
        extras.putBoolean(CardsActivity.INTENT_PASS_CATEGORY, false);
        extras.putInt(CardsActivity.TAG, seiIdToPass);
        i.putExtras(extras);
        startActivity(i);
    }

    @Override
    public void selectedCategoryPosition(int position, String colourOfCard) {
        Intent i = new Intent(this, CardsActivity.class);
        i.putExtra(CardsActivity.INTENT_PASS_CATEGORY, true);
        i.putExtra(CardsActivity.TAG, colourOfCard);
        startActivity(i);
    }
}
