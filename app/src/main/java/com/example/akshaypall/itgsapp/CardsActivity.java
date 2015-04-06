package com.example.akshaypall.itgsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;


public class CardsActivity
        extends ActionBarActivity
{

    public static final String TAG = "CardsActivity";
    private GridView cardsGrid;
    private ArrayAdapter<Button> cardButtonArrayAdapter;
    private ArrayList<Button> buttonList = new ArrayList<>();
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        cardsGrid = (GridView) findViewById(R.id.cards_grid);

//        cardButtonArrayAdapter = new ArrayAdapter<>(this, R.layout.card, R.id.card_list_button, buttonList);
        cardAdapter = new CardAdapter(buttonList);
        cardsGrid.setAdapter(cardAdapter);
//        cardsGrid.setAdapter(cardButtonArrayAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up addButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.add_button)
        {
            Log.d(TAG, "Add pressed!");

            Button button = new Button(this);
            buttonList.add(button);
            cardAdapter.notifyDataSetChanged();
//            cardButtonArrayAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CardAdapter
            extends ArrayAdapter<Button>
    {

        CardAdapter(ArrayList<Button> buttons)
        {
            super(CardsActivity.this, R.layout.card, R.id.card_list_button, buttons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = super.getView(position, convertView, parent);
//            Button button = getItem(position);
            final Button button = (Button) convertView.findViewById(R.id.card_list_button);
            button.setWidth(300);
            button.setHeight(150);

            /* Do the database query here, then set text. */
            button.setText("Global positioning system");
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.d(TAG, "Item clicked");
                    Intent intent = new Intent(CardsActivity.this, InfoCard.class);
//                    intent.putExtra(InfoCard.EXTRA, cardsGrid.getItemAtPosition(position).toString());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
