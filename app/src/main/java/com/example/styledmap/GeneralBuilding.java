package com.example.styledmap;

import com.google.android.gms.maps.model.Polygon;

public class GeneralBuilding implements Building {

    private Polygon shape;
    private String name;

    public GeneralBuilding(Polygon shape, String name){
        this.shape = shape;
        this.name = name;
    }

    public Polygon getShape() {
        return shape;
    }

    @Override
    public String getName() {
        return name;
    }
}
