package com.example.styledmap;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

public class Building extends LocationSpaces {

    private String description;

    public Building(Activity m, GoogleMap mMap, Polygon shape, String name, String descFile) {
        super.shape = shape;
        super.name = name;
        super.type = "b";
        super.mMap = mMap;
        super.clicks = 1;
        description = descFile;
        super.m = m;
    }

    public void doClick() {
        if (clicks == 1){
            shape.setVisible(true);
            Toast.makeText(m, name+ ": Tap again for more info", Toast.LENGTH_LONG).show();
            clicks++;
        } else {
            shape.setVisible(false);
            Toast.makeText(m, "Info here: " + name, Toast.LENGTH_SHORT).show();
            clicks--;
        }
    }
}
