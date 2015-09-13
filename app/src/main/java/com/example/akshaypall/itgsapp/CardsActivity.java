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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CardsActivity extends ActionBarActivity implements CardsFragment.Contract{

    public static final String TAG = CardsFragment.TAG;
    public static final String INTENT_PASS_CATEGORY = CardsFragment.INTENT_PASS_CATEGORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        if (getFragmentManager().findFragmentById(R.id.cards_list_fragment_container) == null){
            CardsFragment cardsFragment = new CardsFragment();
            cardsFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.cards_list_fragment_container, cardsFragment)
                    .commit();
        }
    }

    @Override
    public void selectedPosition(int position, String colour, String cardText, String cardTitle) {
        Intent i = new Intent(this, InfoCard.class);
        Bundle extras = new Bundle();
        extras.putString("COLOUR", colour);
        extras.putString("TAG1", cardText);
        extras.putString("TITLE", cardTitle);
        i.putExtras(extras);
        startActivity(i);
    }
}
