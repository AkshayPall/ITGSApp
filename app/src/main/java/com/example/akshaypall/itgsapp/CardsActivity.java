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
import android.widget.Toast;

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
    public static final String INTENT_PASS_CATEGORY = "isCategory";


    private ListView cardsGrid;
    private ArrayList<String> mColours;
    ParseQuery<ParseObject> query;
    ArrayList<String> mCardText;
    ArrayList<String> mCardTitle;
    private String mCategoryColourForQuery;
    private int mSEIIdNumberForQuery;
    private ArrayList<Integer> mCardNumberArray;
    private CardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        cardsGrid = (ListView) findViewById(R.id.cards_list);

        mCardText = new ArrayList<>();
        mColours = new ArrayList<>();
        mCardTitle = new ArrayList<>();

        query = ParseQuery.getQuery("CardData");

        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean(INTENT_PASS_CATEGORY)) {
            //for categories here TODO: populate list wit categories
            mCategoryColourForQuery = extras.getString(TAG);
            query.whereEqualTo("colourID", mCategoryColourForQuery.substring(1));


        } else {
            //for SEIid TODO: have to query cardNumbers from CardSEIs data, then query cards from CardsData
            mSEIIdNumberForQuery = extras.getInt(TAG);
            Log.d("ID was passed: ", ""+mSEIIdNumberForQuery);
            ParseQuery<ParseObject> seiCardNumQuery = ParseQuery.getQuery("CardsSEIs");
            seiCardNumQuery.whereEqualTo("SEI", mSEIIdNumberForQuery);
            seiCardNumQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, com.parse.ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            mCardNumberArray.add(list.get(i).getInt("card"));
                        }
                        query.whereContainedIn("cardId", mCardNumberArray);
                    } else {
                        Log.d("Title", "ERROR: " + e.getMessage());
                    }
                }
            });
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("test", "" + parseObjects.size());
                    for (int i = 0; i < parseObjects.size(); i++) {
                        mCardText.add(parseObjects.get(i).getString("text"));
                        mCardTitle.add(parseObjects.get(i).getString("title"));
                        mColours.add("#" + parseObjects.get(i).getString("colourID"));
                        Log.d("CARD INFO", mCardTitle.get(i) + "----" + mCardText.get(i));
                    }
                    mAdapter = new CardAdapter(mCardTitle);
                    cardsGrid.setAdapter(mAdapter);
                    cardsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Intent i = new Intent(CardsActivity.this, InfoCard.class);
                            Bundle extras = new Bundle();
                            extras.putString("COLOUR", mColours.get(position));
                            extras.putString("TAG1", mCardText.get(position));
                            i.putExtras(extras);
                            startActivity(i);
                        }
                    });

                } else {
                    Log.d("Title", "ERROR: " + e.getMessage());
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        Toast exitToast = Toast.makeText(CardsActivity.this, "Loading...", Toast.LENGTH_SHORT);
        exitToast.show();
        super.onDestroy();
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

    private class CardAdapter
            extends ArrayAdapter<String>
    {
        CardAdapter(ArrayList<String> items)
        {
            super(CardsActivity.this, R.layout.item_list_row, R.id.item_text, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = super.getView(position, convertView, parent);
            }

            String item = getItem(position);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.item_text);
            ImageView dot = (ImageView) convertView.findViewById(R.id.dot);

            nameTextView.setText(item);
            dot.setColorFilter(Color.parseColor(mColours.get(position)));


            Log.d("getView", "get view called and finished");
            return convertView;
        }
    }
}
