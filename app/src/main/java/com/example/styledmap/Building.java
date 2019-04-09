package com.example.styledmap;

import com.google.android.gms.maps.model.Polygon;

public interface Building {

    public Polygon getShape();
    public String getName();
}
