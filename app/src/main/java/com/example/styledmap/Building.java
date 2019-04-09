package com.example.styledmap;

import com.google.android.gms.maps.model.Polygon;

public class Building extends Location {

    //private File description;

    public Building(Polygon shape, String name){
        super.shape = shape;
        super.name = name;
    }

    //insert file parser/retriever here
}
