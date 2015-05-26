package com.example.akshaypall.itgsapp;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.*;

import java.util.ArrayList;
import java.util.List;


public class MainActivity
        extends ActionBarActivity
{

    private ArrayList<String> mItems;
    private ArrayList<String> mColours;
    private ArrayList<String> mSEIs;
    private int[] mSEIIds;
    private ListView listview;
    private ListView SEIListview;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, mApplicationId, mClientKey);

        mItems = new ArrayList<>();
        mColours = new ArrayList<>();
        mSEIs = new ArrayList<>();

        TextView allButtonText = (TextView)findViewById(R.id.all_title_row);
        allButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CardsActivity.class);
                startActivity(i);
            }
        });

        ParseQuery<ParseObject> seiQuery = ParseQuery.getQuery("SEI");
        seiQuery.whereExists("title");
        seiQuery.orderByAscending("SEIid");
        seiQuery.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null)
                {
                    mSEIIds = new int[parseObjects.size()];
                    Log.d("category", "Retrieved " + parseObjects.size() + " parseObjects");
                    for (int i = 0; i < parseObjects.size(); i++)
                    {
                        String sei = parseObjects.get(i).getString("title");
                        mSEIs.add(sei);
                        int id = parseObjects.get(i).getInt("SEIid");
                        mSEIIds[i]=id;
                        Log.d("SEI ids", "id passed is "+id);
                    }
                    System.out.println("PARSE SEI QUERY DONE");
                    final ArrayAdapter<String> mAdapter = new SEIItemAdapter(mSEIs);
                    SEIListview = (ListView)findViewById(R.id.sei_list);
                    SEIListview.setAdapter(mAdapter);
                    SEIListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast toast = Toast.makeText(MainActivity.this, mSEIs.get(position) + " selected", Toast.LENGTH_SHORT);
                            toast.show();
                            int seiIdToPass = mSEIIds[position];
                            Log.d("passed ID", "here: "+seiIdToPass);
                            Intent i = new Intent(MainActivity.this, CardsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putBoolean(CardsActivity.INTENT_PASS_CATEGORY, false);
                            extras.putInt(CardsActivity.TAG, seiIdToPass);
                            i.putExtras(extras);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    Log.d("error", "Error: " + e);
                }
            }
        });


        ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery("CardCategories");
        categoryQuery.whereExists("category");
        categoryQuery.whereExists("color");
        categoryQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.d("category", "Retrieved " + parseObjects.size() + " parseObjects");
                    for (int i = 0; i < parseObjects.size(); i++) {
                        Log.d("Array loading", "Array loading... index " + i);
                        if (i == parseObjects.size() - 1)
                            Log.d("Array loading", "Array loading... done");

                        String category = parseObjects.get(i).getString("category");
                        String color = parseObjects.get(i).getString("color");
                        mItems.add(category);
                        mColours.add("#" + color);
                    }
                    //adjustDataArrays();
                    /* Temporary cheat method simulating the "loading" animation. This gives time for both queries to finish */
                    loadingData();
                } else {
                    Log.d("category", "*Error*: " + e.getMessage());
                }
            }

            private void loadingData() {
                //mItems.addAll(mItems.size(), mSEIs);
                final ArrayAdapter<String> mAdapter = new ItemAdapter(mItems);
                listview = (ListView) findViewById(R.id.list);
                listview.setAdapter(mAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast toast = Toast.makeText(MainActivity.this, mItems.get(position) + " selected", Toast.LENGTH_SHORT);
                        toast.show();
                        String colourOfCard = mColours.get(position);
                        Intent i = new Intent(MainActivity.this, CardsActivity.class);
                        i.putExtra(CardsActivity.INTENT_PASS_CATEGORY, true);
                        i.putExtra(CardsActivity.TAG, colourOfCard);
                        startActivity(i);
                    }
                });
            }

            /* For categories All, Color, and SEI TODO: delete this method, once both lists are working
            private void adjustDataArrays()
            {
                mItems.add(0, "All");
                mItems.add(1, "COLOR");
                mItems.add(12, "SOCIAL AND ETHICAL ISSUES");
                mColours.add(0, "#ffffff");
                mColours.add(1, "#ffffff");
                mColours.add(12, "#ffffff");
            }*/
        });
        System.out.println("PARSE QUERY EXITED");
    }


    private class ItemAdapter
            extends ArrayAdapter<String>
    {
        ItemAdapter(ArrayList<String> items)
        {
            super(MainActivity.this, R.layout.item_list_row, R.id.item_text, items);
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

    private class SEIItemAdapter
            extends ArrayAdapter<String>
    {
        SEIItemAdapter(ArrayList<String> items)
        {
            super(MainActivity.this, R.layout.sei_list_row, R.id.sei_list_row_title, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = super.getView(position, convertView, parent);
            }

            String item = getItem(position);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.sei_list_row_title);
            nameTextView.setText(item);

            Log.d("getView", "get view called and finished");
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds mItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
