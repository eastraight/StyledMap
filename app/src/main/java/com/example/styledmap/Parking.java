package com.example.styledmap;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;

public class Parking extends LocationSpaces {

    public Parking(Activity m, GoogleMap mMap, Polygon shape, String name){
        super.shape = shape;
        super.name = name;
        super.type = "p";
        super.mMap = mMap;
        super.m = m;
    }

    public void doClick(){
        Toast.makeText(m,"Clicked: " + name, Toast.LENGTH_SHORT).show();
    }
}
