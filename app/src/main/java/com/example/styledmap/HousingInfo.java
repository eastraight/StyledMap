package com.example.styledmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        setContentView(R.layout.housing_info);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String descFile = intent.getStringExtra("DESC_FILE");

        BufferedReader reader = null;

        try {
            System.out.println("\n\nFile name is: "+descFile+"\n\n");
            reader = new BufferedReader(
                    new InputStreamReader(getApplicationContext().getAssets().open(descFile)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        }catch (FileNotFoundException e){
            System.out.println("File not found: " + descFile);
        } catch (IOException e){
            System.out.println("Er: " + descFile);
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
        output.setMovementMethod(new ScrollingMovementMethod());
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
