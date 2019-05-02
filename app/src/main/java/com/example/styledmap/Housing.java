package com.example.styledmap;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.io.File;
import java.util.List;

public class Housing extends LocationSpaces {

    private File description;
    private String category;

    public Housing(Activity m, GoogleMap mMap, Polygon shape, String name, String category, String descFile){
        super.shape = shape;
        super.name = name;
        this.category = category;
        super.type = "h";
        super.mMap = mMap;
        super.m = m;
        super.clicks = 1;
        description = new File(descFile);
    }

    public void doClick() {
        if (clicks == 1){
            shape.setVisible(true);
            Toast.makeText(m, name+ ": Tap again for more info", Toast.LENGTH_LONG).show();
            clicks++;
        } else if (clicks == 2){
            info();
            clicks++;
        } else {
            clicks = clicks-2;
            shape.setVisible(false);
        }
    }
    private void zoom(){
        List<LatLng> take = shape.getPoints();
        double newLat = (take.get(1).latitude + take.get(3).latitude)/(double)2;
        double newLong = (take.get(1).longitude + take.get(3).longitude)/(double)2;
        LatLng point = new LatLng(newLat, newLong);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 18.0f));
        shape.setVisible(true);
    }

    private void info(){
        Intent intent = new Intent(m, HousingInfo.class);
        intent.putExtra("DESC_FILE", description);
        m.startActivity(intent);

        zoom();
    }
}
