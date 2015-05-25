package com.example.akshaypall.itgsapp;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class CardsActivity extends ActionBarActivity
{
    public static final String TAG = "CardsActivity";
    private ListView cardsGrid;
    private ArrayList<String> mColours;
    ParseQuery<ParseObject> query;
    ArrayList<String> mCardText;
    ArrayList<String> mCardTitle;
    private int mCategoryNum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mCategoryNum = extras.getInt("TAG");
        }

        cardsGrid = (ListView) findViewById(R.id.cards_list);

        mCardText = new ArrayList<>();
        mColours = new ArrayList<>();
        mCardTitle = new ArrayList<>();

        query = ParseQuery.getQuery("CardData");
        query.whereEqualTo("category", mCategoryNum);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("test", "" + parseObjects.size());
                    for (int i = 0; i < parseObjects.size(); i++) {
                        mCardText.add(parseObjects.get(i).getString("text"));
                        mCardTitle.add(parseObjects.get(i).getString("title"));
                        mColours.add("#" + parseObjects.get(i).getString("colourID"));
                    }

                } else {
                    Log.d("Title", "ERROR: " + e.getMessage());
                }
            }
        });
        cardsGrid.setAdapter(new CardAdapter(mCardTitle));

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

        cardsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Intent i = new Intent(CardsActivity.this, InfoCard.class);
                Bundle extras = new Bundle();
                extras.putString("COLOUR", mColours.get(position));
                extras.putString("TAG1", mCardText.get(position));
                i.putExtras(extras);
                startActivity(i);
            }
        });

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

    private class CardAdapter extends ArrayAdapter<String>
    {

        CardAdapter(ArrayList<String> titles)
        {
            super(CardsActivity.this, R.layout.item_list_row, R.id.item_text, titles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = super.getView(position, convertView, parent);
            String currentTitle = getItem(position);

            TextView titleOfCard = (TextView)findViewById(R.id.item_text);
            titleOfCard.setText(currentTitle);

            ImageView dot = (ImageView) convertView.findViewById(R.id.dot);
            dot.setColorFilter(Color.parseColor(mColours.get(position)));

            return convertView;
        }
    }
}
