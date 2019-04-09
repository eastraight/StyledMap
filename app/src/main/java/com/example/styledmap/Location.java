package com.example.styledmap;

import com.google.android.gms.maps.model.Polygon;

public abstract class Location {

    protected Polygon shape;
    protected String name;

    public Polygon getShape(){
        return shape;
    }
    public String getName(){
        return name;
    }
}
