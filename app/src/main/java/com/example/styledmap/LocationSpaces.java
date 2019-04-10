package com.example.styledmap;

import com.google.android.gms.maps.model.Polygon;

public abstract class LocationSpaces {

    protected Polygon shape;
    protected String name;
    protected String type;

    public Polygon getShape(){
        return shape;
    }
    public String getName(){
        return name;
    }
    public String getType() { return type; }
}
