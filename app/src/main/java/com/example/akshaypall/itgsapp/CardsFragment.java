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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardsFragment extends Fragment {

    public static final String TAG = "CardsActivity";
    public static final String INTENT_PASS_CATEGORY = "isCategory";


    private ListView mCardsGrid;
    private ArrayList<String> mColours;
    ParseQuery<ParseObject> mQuery;
    ArrayList<String> mCardText;
    ArrayList<String> mCardTitle;
    private String mCategoryColourForQuery;
    private int mSEIIdNumberForQuery;
    private CardAdapter mAdapter;
    private static final String TITLE_PIN_LABEL = "Titles";
    private static final String CARD_NUMBER_PIN_LABEL = "Titles";

    public CardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cards, container, false);

        mCardsGrid = (ListView) v.findViewById(R.id.cards_list);
        mCardsGrid.setFastScrollEnabled(true);

        mCardText = new ArrayList<>();
        mColours = new ArrayList<>();
        mCardTitle = new ArrayList<>();

        mQuery = ParseQuery.getQuery("CardData");

        Bundle extras = getArguments();
        if (extras == null) {
            //NOTHING
        } else if (extras.getBoolean(INTENT_PASS_CATEGORY)) {
            //for categories here TODO: populate list wit categories
            mCategoryColourForQuery = extras.getString(TAG);
            mQuery.whereEqualTo("colourID", mCategoryColourForQuery.substring(1));
        } else {
            //for SEIid TODO: have to query cardNumbers from CardSEIs data, then query cards from CardsData
            mSEIIdNumberForQuery = extras.getInt(TAG);
            Log.d("ID was passed: ", "" + mSEIIdNumberForQuery);
            final ParseQuery<ParseObject> seiCardNumQuery = ParseQuery.getQuery("CardsSEIs");
            seiCardNumQuery.whereEqualTo("SEI", mSEIIdNumberForQuery);
            seiCardNumQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> parseObjects, ParseException e) {
                    if (e == null){
                        ParseObject.unpinAllInBackground(CARD_NUMBER_PIN_LABEL, parseObjects, new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.d("SEI query", "SEI unpinning error " + e);
                                    return;
                                }
                                // Add the latest results for this query to the cache.
                                ParseObject.pinAllInBackground(CARD_NUMBER_PIN_LABEL, parseObjects);
                            }
                        });
                    }
                }
            });
            mQuery.whereMatchesKeyInQuery("cardId", "card", seiCardNumQuery);
        }

        if (isConnected()) {
            cardsActivityQuery();
        } else{
            mQuery.fromLocalDatastore();
            cardsActivityQuery();
        }

        return v;
    }

    private void cardsActivityQuery() {
        mQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(TITLE_PIN_LABEL, parseObjects, new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("SEI query", "SEI unpinning error " + e);
                                return;
                            }
                            // Add the latest results for this query to the cache.
                            ParseObject.pinAllInBackground(TITLE_PIN_LABEL, parseObjects);
                        }
                    });

                    Log.d("test", "" + parseObjects.size());
                    for (int i = 0; i < parseObjects.size(); i++) {
                        mCardText.add(parseObjects.get(i).getString("text"));
                        mCardTitle.add(parseObjects.get(i).getString("title"));
                        mColours.add("#" + parseObjects.get(i).getString("colourID"));
                        Log.d("CARD INFO", mCardTitle.get(i) + "----" + mCardText.get(i));
                    }
                    mAdapter = new CardAdapter(mCardTitle);
                    mCardsGrid.setAdapter(mAdapter);
                    mCardsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Intent i = new Intent(getActivity(), InfoCard.class);
                            Bundle extras = new Bundle();
                            extras.putString("COLOUR", mColours.get(position));
                            extras.putString("TAG1", mCardText.get(position));
                            extras.putString("TITLE", mCardTitle.get(position));
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

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return connected;
    }

    @Override
    public void onDestroy() {
        Toast exitToast = Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT);
        exitToast.show();
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cards, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up addButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private class CardAdapter extends ArrayAdapter<String> {
        CardAdapter(ArrayList<String> items) {
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
}
