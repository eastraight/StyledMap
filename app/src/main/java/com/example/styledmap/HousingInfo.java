package com.example.styledmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HousingInfo extends AppCompatActivity {

    private StringBuilder text = new StringBuilder();
    private Button testButton;

    /**
     *Code reused from "Naruto Uzumaki" at
     * "https://stackoverflow.com/questions/33779607/reading-a-txt-file-and-outputing-as-a-textview-in-android"
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_info);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String descFile = intent.getStringExtra("DESC_FILE");

        BufferedReader reader = null;

        try{
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(descFile)));

            String mLine;
            while((mLine = reader.readLine()) != null){
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e){
            System.out.println("File not found: " + descFile);
        } finally {
            if(reader != null){
                try{
                    reader.close();
                } catch (IOException e){
                    System.out.println("Um");
                }
            }
        }

        // Capture the layout's TextView and set the string as its text
        TextView output = findViewById(R.id.textView);
        output.setText(text);

        testButton = findViewById(R.id.button3);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
