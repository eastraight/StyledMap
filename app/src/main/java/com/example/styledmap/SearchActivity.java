package com.example.styledmap;

import android.app.SearchManager;
import android.app.ListActivity;
import android.app.SearchManager;
import android.widget.TextView;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.R.*;
import android.util.Xml;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.*;
import android.*;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    ListView search_building;
    ArrayAdapter<String> adapter;
    private HashMap<String, LocationSpaces> allLocations;
    String entered = "";

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search);
            handleIntent(getIntent());

            search_building = (ListView) findViewById(R.id.search_building);
            search_building.setAdapter(adapter);

            Intent intent = getIntent();

            allLocations = new HashMap<>();


            ArrayList<String> arrayBuilding = new ArrayList<>();
            arrayBuilding.addAll(Arrays.asList(getResources().getStringArray(R.array.my_building)));
            adapter = new ArrayAdapter<String>(
                    SearchActivity.this,
                    android.R.layout.simple_list_item_1,
                    arrayBuilding
            );
            if(Intent.ACTION_SEARCH.equals(intent.getAction())){
                entered = intent.getStringExtra(SearchManager.QUERY);
                doMySearch(entered);
            }
        }

        protected void onNewIntent(Intent intent){
            setIntent(intent);
            handleIntent(intent);
        }

        private void handleIntent(Intent intent){
            if(Intent.ACTION_SEARCH.equals(intent.getAction())){
                entered = intent.getStringExtra(intent.getAction());
                doMySearch(entered);
            }
        }

        private LocationSpaces doMySearch(String query){
            for(String current : allLocations.keySet()) {
                if (query.toLowerCase().matches(current)) {
                    return allLocations.get(current);
                }
            }
            return null;
        }

        public boolean onSearchRequested(){
            Bundle appData = new Bundle();
            appData.putBoolean(entered, true);
            startSearch(null, false, appData, false);
            return true;
        }
    }

