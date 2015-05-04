package com.example.akshaypall.itgsapp;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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


public class MainActivity extends ActionBarActivity {


    private ArrayList<String> mItems;
    private ArrayList<String> mColours;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, id1, id2);

        mItems = new ArrayList<>();
        mColours = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<>("CardCategories");
        query.whereExists("category");
        query.whereExists("color");
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null)
                {
                    Log.d("category", "Retrieved " + parseObjects.size() + " parseObjects");
                    for (int i = 0; i < parseObjects.size(); i++)
                    {
                        String category = parseObjects.get(i).getString("category");
                        String color = parseObjects.get(i).getString("color");
                        mItems.add(category);
                        mColours.add("#" + color);
                    }
                }
                else
                {
                    Log.d("category", "*Error*: " + e.getMessage());
                }
            }
        });

        ArrayAdapter<String> mAdapter = new ItemAdapter(mItems);
//        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, R.layout.item_list_row, R.id.item_text, mItems);
        ListView listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(mAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {

            }
        });
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
            convertView = super.getView(position, convertView, parent);

            String item = getItem(position);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.item_text);
            nameTextView.setText(item);

            ImageView dot = (ImageView) convertView.findViewById(R.id.dot);
            dot.setColorFilter(Color.parseColor(mColours.get(position)));

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds mItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
