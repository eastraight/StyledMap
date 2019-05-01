package com.example.styledmap;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;

import java.io.File;

public class Housing extends LocationSpaces {

    private File description;
    private String category;

    public Housing(Activity m, GoogleMap mMap, Polygon shape, String name, String category, String descFile){
        super.shape = shape;
        super.name = name;
        this.category = category;
        super.type = "h";
        super.mMap = mMap;
        super.m = m;
        super.clicks = 1;
        description = new File(descFile);
    }

    public void doClick() {
        if (clicks == 1){
            shape.setVisible(true);
            Toast.makeText(m, name+ ": Tap again for more info", Toast.LENGTH_SHORT).show();
            clicks++;
        } else {
            shape.setVisible(false);
            Toast.makeText(m, "Info here: " + name, Toast.LENGTH_SHORT).show();
            clicks--;
        }
    }
}
