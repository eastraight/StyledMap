package com.example.styledmap;

import com.google.android.gms.maps.model.Polygon;

public class Parking extends LocationSpaces {

    public Parking(Polygon shape, String name){
        super.shape = shape;
        super.name = name;
    }
}
