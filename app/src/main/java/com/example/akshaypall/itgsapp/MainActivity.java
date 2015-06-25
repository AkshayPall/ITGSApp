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
import android.widget.*;
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
    String SEI_PIN_LABEL = "SEIs";
    String CATEGORY_PIN_LABEL = "Categories";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, );

        mItems = new ArrayList<>();
        mColours = new ArrayList<>();
        mSEIs = new ArrayList<>();

        TextView allButtonText = (TextView) findViewById(R.id.all_title_row);
        allButtonText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MainActivity.this, CardsActivity.class);
                startActivity(i);
            }
        });

        ParseQuery<ParseObject> seiQuery = ParseQuery.getQuery("SEI");
        seiQuery.whereExists("title");
        seiQuery.orderByAscending("SEIid");
        seiQuery.fromLocalDatastore();
        seiQuery.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null)
                {
                    ParseObject.pinAllInBackground(SEI_PIN_LABEL, parseObjects);
                    mSEIIds = new int[parseObjects.size()];
                    Log.d("category", "Retrieved " + parseObjects.size() + " parseObjects");
                    for (int i = 0; i < parseObjects.size(); i++)
                    {
                        String sei = parseObjects.get(i).getString("title");
                        mSEIs.add(sei);
                        int id = parseObjects.get(i).getInt("SEIid");
                        mSEIIds[i] = id;
                        Log.d("SEI ids", "id passed is " + id);
                    }
                    System.out.println("PARSE SEI QUERY DONE");
                    final ArrayAdapter<String> mAdapter = new SEIAdapter(mSEIs);
                    SEIListview = (ListView) findViewById(R.id.sei_list);
                    SEIListview.setAdapter(mAdapter);
                    SEIListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            Toast toast = Toast.makeText(MainActivity.this, mSEIs.get(position) + " selected", Toast.LENGTH_SHORT);
                            toast.show();
                            int seiIdToPass = mSEIIds[position];
                            Log.d("passed ID", "here: " + seiIdToPass);
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
                // Release any objects previously pinned for this query.
                ParseObject.unpinAllInBackground(SEI_PIN_LABEL, parseObjects, new DeleteCallback()
                {
                    public void done(ParseException e)
                    {
                        if (e != null)
                        {
                            Log.d("SEI query", "SEI unpinning error " + e);
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        ParseObject.pinAllInBackground(SEI_PIN_LABEL, parseObjects);
                    }
                });
            }
        });

        ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery("CardCategories");
        categoryQuery.whereExists("category");
        categoryQuery.whereExists("color");
        categoryQuery.fromLocalDatastore();
        categoryQuery.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e)
            {

                if (e == null)
                {
                    ParseObject.pinAllInBackground(CATEGORY_PIN_LABEL, parseObjects);

                    Log.d("category", "Retrieved " + parseObjects.size() + " parseObjects");
                    for (int i = 0; i < parseObjects.size(); i++)
                    {
                        Log.d("Array loading", "Array loading... index " + i);
                        if (i == parseObjects.size() - 1)
                            Log.d("Array loading", "Array loading... done");

                        String category = parseObjects.get(i).getString("category");
                        String color = parseObjects.get(i).getString("color");
                        mItems.add(category);
                        mColours.add("#" + color);
                    }
                    final ArrayAdapter<String> mAdapter = new ColorCategoryAdapter(mItems);
                    listview = (ListView) findViewById(R.id.list);
                    listview.setAdapter(mAdapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
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

                else
                {
                    Log.d("category", "*Error*: " + e.getMessage());
                }

                // Release any objects previously pinned for this query.
                ParseObject.unpinAllInBackground(CATEGORY_PIN_LABEL, parseObjects, new DeleteCallback()
                {
                    public void done(ParseException e)
                    {
                        if (e != null)
                        {
                            Log.d("SEI query", "SEI unpinning error " + e);
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        ParseObject.pinAllInBackground(CATEGORY_PIN_LABEL, parseObjects);
                    }
                });
            }
        });
    }

    private class ColorCategoryAdapter
            extends ArrayAdapter<String>
    {
        ColorCategoryAdapter(ArrayList<String> items)
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

    private class SEIAdapter
            extends ArrayAdapter<String>
    {
        SEIAdapter(ArrayList<String> items)
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
