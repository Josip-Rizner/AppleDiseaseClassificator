package com.example.applediseaseclassificator;

import org.json.JSONArray;
import org.json.JSONException;

public class RecommendationSystem {

    private String id, latitude, longitude, name, startingImageReference;
    private long timestamp;

    public RecommendationSystem (){}

    public RecommendationSystem (String id, String latitude, String longitude, String name, String imageReference){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.startingImageReference = imageReference;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
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

    public String getStartingImageReference() {
        return startingImageReference;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
