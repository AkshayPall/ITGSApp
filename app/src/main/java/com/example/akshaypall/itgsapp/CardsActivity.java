package com.example.akshaypall.itgsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class CardsActivity extends ActionBarActivity
{
    public static final String TAG = "CardsActivity";
    private GridView cardsGrid;
    ParseQuery<ParseObject> query;
    ArrayList<ParseObject> list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        cardsGrid = (GridView) findViewById(R.id.cards_grid);

        list = new ArrayList<ParseObject>();

        query = ParseQuery.getQuery("CardData");
        query.whereExists("title"); //to query ALL cards
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null){
                    Log.d("test", ""+parseObjects.size());
                    list.addAll(parseObjects);

                } else {
                    Log.d ("Title", "ERROR: "+e.getMessage());
                }
            }
        });
        cardsGrid.setAdapter(new CardAdapter(list));

        cardsGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            int previousFirstItemSeen = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > previousFirstItemSeen) {
                    getSupportActionBar().hide();
                } else if (firstVisibleItem < previousFirstItemSeen) {
                    getSupportActionBar().show();
                }
                previousFirstItemSeen = firstVisibleItem;
            }
        });

        /*cardsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject objectClicked = list.get(position);
                Intent intent = new Intent(CardsActivity.this, InfoCard.class);
                intent.putExtra(InfoCard.EXTRA, objectClicked.getString("text"));
                startActivity(intent);
            }
        });   */

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

        return super.onOptionsItemSelected(item);
    }

    private class CardAdapter extends ArrayAdapter<ParseObject>
    {

        CardAdapter(ArrayList<ParseObject> objects)
        {
            super(CardsActivity.this, R.layout.card, R.id.card_list_button, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = super.getView(position, convertView, parent);
            final ParseObject objectShown = getItem(position);
            Log.d("object Loaded", "title is "+objectShown.getString("title"));
            Button buttonToEdit = (Button)findViewById(R.id.card_list_button);
            buttonToEdit.setText("" + objectShown.getString("title"));
            buttonToEdit.setWidth(150);
            buttonToEdit.setHeight(80);
            buttonToEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CardsActivity.this, InfoCard.class);
                    intent.putExtra(InfoCard.EXTRA, objectShown.getString("text"));
                    startActivity(intent);
                }
            });


            return convertView;
        }
    }
}
