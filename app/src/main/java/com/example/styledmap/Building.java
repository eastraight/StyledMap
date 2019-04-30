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
        description = descFile;
        super.m = m;
    }

    public void doClick() {
        Toast.makeText(m,"Clicked: " + name, Toast.LENGTH_SHORT).show();
    }
}
