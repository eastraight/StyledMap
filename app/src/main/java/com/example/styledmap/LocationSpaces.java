package com.example.styledmap;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;

public abstract class LocationSpaces {

    protected Polygon shape;
    protected String name;
    protected String type;
    protected GoogleMap mMap;
    protected Activity m;

    public Polygon getShape(){
        return shape;
    }
    public String getName(){
        return name;
    }
    public String getType() { return type; }

    public abstract void doClick();
}
