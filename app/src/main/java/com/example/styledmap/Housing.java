package com.example.styledmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;

public class Housing extends LocationSpaces {

    //private File description;
    private String category;

    public Housing(Polygon shape, String name, String category, GoogleMap.OnPolygonClickListener click){
        super.shape = shape;
        super.name = name;
        this.category = category;
        super.type = "h";
        super.click = click;
    }

    //insert file parser/retriever here

    public String getCategory() {
        return category;
    }
}
