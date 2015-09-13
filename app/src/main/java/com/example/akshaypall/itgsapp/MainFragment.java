package com.example.akshaypall.itgsapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ArrayList<String> mItems;
    private ArrayList<String> mColours;
    private ArrayList<String> mSEIs;
    private int[] mSEIIds;
    private ListView mListView;
    private ListView mSEIListview;
    private static final String SEI_PIN_LABEL = "SEIs";
    private static final String CATEGORY_PIN_LABEL = "Categories";

    private String mApplicationID = "";
    private String mClientKey = "";

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        Parse.enableLocalDatastore(getActivity());
        Parse.initialize(getActivity(), mApplicationID, mClientKey);

        mItems = new ArrayList<>();
        mColours = new ArrayList<>();
        mSEIs = new ArrayList<>();

        mListView = (ListView) v.findViewById(R.id.list);
        mSEIListview = (ListView) v.findViewById(R.id.sei_list);

        TextView allButtonText = (TextView) v.findViewById(R.id.all_title_row);
        allButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CardsActivity.class);
                startActivity(i);
            }
        });

        ParseQuery<ParseObject> seiQuery = ParseQuery.getQuery("SEI")
                .whereExists("title")
                .orderByAscending("SEIid");
        //if user has a network connection, the information updates from parse database,
        //otherwise it checks what is already stored.
        if (isConnected()){
            seiQuery(seiQuery);
        }else {
            seiQuery.fromLocalDatastore();
            seiQuery(seiQuery);
        }

        ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery("CardCategories");
        categoryQuery.whereExists("category");
        categoryQuery.whereExists("color");
        if (isConnected()){
            categoryQuery(categoryQuery);
        }else {
            categoryQuery.fromLocalDatastore();
            categoryQuery(categoryQuery);
        }

        return v;
    }

    private void categoryQuery(ParseQuery<ParseObject> query) {
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    // Release any objects previously pinned for this query.
                    ParseObject.unpinAllInBackground(CATEGORY_PIN_LABEL, parseObjects, new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("Category query", "Category unpinning error " + e);
                                return;
                            }

                            // Add the latest results for this query to the cache.
                            ParseObject.pinAllInBackground(CATEGORY_PIN_LABEL, parseObjects);
                        }
                    });

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
                    final ArrayAdapter<String> mAdapter = new ColorCategoryAdapter(mItems);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast toast = Toast.makeText(getActivity(), mItems.get(position) + " selected", Toast.LENGTH_SHORT);
                            toast.show();
                            String colourOfCard = mColours.get(position);
                            Intent i = new Intent(getActivity(), CardsActivity.class);
                            i.putExtra(CardsActivity.INTENT_PASS_CATEGORY, true);
                            i.putExtra(CardsActivity.TAG, colourOfCard);
                            startActivity(i);
                        }
                    });
                } else {
                    Log.d("category", "*Error*: " + e.getMessage());
                }
            }
        });
    }

    private void seiQuery(ParseQuery<ParseObject> query) {
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    // Release any objects previously pinned for this query.
                    ParseObject.unpinAllInBackground(SEI_PIN_LABEL, parseObjects, new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("SEI query", "SEI unpinning error " + e);
                                return;
                            }
                            // Add the latest results for this query to the cache.
                            ParseObject.pinAllInBackground(SEI_PIN_LABEL, parseObjects);
                        }
                    });

                    mSEIIds = new int[parseObjects.size()];
                    Log.d("sei", "Retrieved " + parseObjects.size() + " parseObjects");

                    for (int i = 0; i < parseObjects.size(); i++) {
                        String sei = parseObjects.get(i).getString("title");
                        mSEIs.add(sei);
                        int id = parseObjects.get(i).getInt("SEIid");
                        mSEIIds[i] = id;
                        Log.d("SEI ids", "id passed is " + id);
                    }
                    System.out.println("PARSE SEI QUERY DONE");

                    final ArrayAdapter<String> mAdapter = new SEIAdapter(mSEIs);
                    mSEIListview.setAdapter(mAdapter);
                    mSEIListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast toast = Toast.makeText(getActivity(), mSEIs.get(position) + " selected", Toast.LENGTH_SHORT);
                            toast.show();
                            int seiIdToPass = mSEIIds[position];
                            Log.d("passed ID", "here: " + seiIdToPass);
                            Intent i = new Intent(getActivity(), CardsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putBoolean(CardsActivity.INTENT_PASS_CATEGORY, false);
                            extras.putInt(CardsActivity.TAG, seiIdToPass);
                            i.putExtras(extras);
                            startActivity(i);
                        }
                    });
                } else {
                    Log.d("error", "Error: " + e);
                }
            }
        });
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return connected;
    }

    private class ColorCategoryAdapter extends ArrayAdapter<String> {
        ColorCategoryAdapter(ArrayList<String> items) {
            super(getActivity(), R.layout.item_list_row, R.id.item_text, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
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

    private class SEIAdapter extends ArrayAdapter<String> {
        SEIAdapter(ArrayList<String> items) {
            super(getActivity(), R.layout.sei_list_row, R.id.sei_list_row_title, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
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
