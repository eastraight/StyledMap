package com.example.styledmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;

public class Parking extends LocationSpaces {

    public Parking(Polygon shape, String name, GoogleMap.OnPolygonClickListener click){
        super.shape = shape;
        super.name = name;
        super.type = "p";
        super.click = click;
    }
}
