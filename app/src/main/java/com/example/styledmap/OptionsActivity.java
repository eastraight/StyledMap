package com.example.styledmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {
    private Button housesButton;
    private Button underButton;
    private Button upperButton;
    private Button apartButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        housesButton = findViewById(R.id.housingButton);
        housesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent housingIntent = new Intent(OptionsActivity.this, HousingInfo.class);
                housingIntent.putExtra("DESC_FILE", "Houses");
                startActivity(housingIntent);
            }
        });

        underButton = findViewById(R.id.underButton);
        underButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent housingIntent = new Intent(OptionsActivity.this, HousingInfo.class);
                housingIntent.putExtra("DESC_FILE", "UnderClassHousing");
                startActivity(housingIntent);
            }
        });

        upperButton = findViewById(R.id.upperButton);
        upperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent housingIntent = new Intent(OptionsActivity.this, HousingInfo.class);
                housingIntent.putExtra("DESC_FILE", "UpperClassHousing");
                startActivity(housingIntent);
            }
        });

        apartButton = findViewById(R.id.apartButton);
        apartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent housingIntent = new Intent(OptionsActivity.this, HousingInfo.class);
                housingIntent.putExtra("DESC_FILE", "Apartments");
                startActivity(housingIntent);
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
