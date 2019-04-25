package com.example.styledmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;

public class Building extends LocationSpaces {

    //private File description;

    public Building(Polygon shape, String name, GoogleMap.OnPolygonClickListener click){
        super.shape = shape;
        super.name = name;
        super.type = "b";
        super.click = click;
    }

    //insert file parser/retriever here
}
