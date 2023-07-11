package com.example.applediseaseclassificator;

import org.json.JSONArray;
import org.json.JSONException;

public class RecommendationSystem {

    private String id, latitude, longitude, name;


    public RecommendationSystem (String id, String latitude, String longitude, String name){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
}
