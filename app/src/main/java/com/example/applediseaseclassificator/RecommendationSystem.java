package com.example.applediseaseclassificator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class RecommendationSystem {

    private String id, latitude, longitude, name, startingImageReference, classifiedDisease;
    private long timestamp;
    private List<Float> startDiseaseConfidences;

    public RecommendationSystem (){}

    public RecommendationSystem (String id, String latitude, String longitude, String name, String imageReference, String classifiedDisease, List<Float> startDiseaseConfidences){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.startingImageReference = imageReference;
        this.timestamp = System.currentTimeMillis();
        this.classifiedDisease = classifiedDisease;
        this.startDiseaseConfidences = startDiseaseConfidences;
    }

    public String getClassifiedDisease() {
        return classifiedDisease;
    }

    public List<Float> getStartDiseaseConfidences() {
        return startDiseaseConfidences;
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
