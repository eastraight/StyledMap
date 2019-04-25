package com.example.styledmap;

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

public class SearchActivity extends AppCompatActivity {
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search);
            handleIntent(getIntent());
        }

        protected void onNewIntent(Intent intent){
            handleIntent(intent);
        }

        private void handleIntent(Intent intent){
            if(Intent.ACTION_SEARCH.equals(intent.getAction())){
                String entered = intent.getStringExtra(intent.getAction());
            }
        }
    }

