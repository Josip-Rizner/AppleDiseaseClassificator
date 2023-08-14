package com.example.applediseaseclassificator;

import java.util.List;

public class RecommendationSystem {

    private String id, latitude, longitude, name, startingImageReference, classifiedDisease;
    private long timestamp;
    private List<Float> startDiseaseConfidences;
    private List<SimpleMessage> recommendationMessages;

    public RecommendationSystem (){}

    public RecommendationSystem (String id, String latitude, String longitude, String name, String imageReference, String classifiedDisease, List<Float> startDiseaseConfidences, List<SimpleMessage> recommendationMessages){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.startingImageReference = imageReference;
        this.timestamp = System.currentTimeMillis();
        this.classifiedDisease = classifiedDisease;
        this.startDiseaseConfidences = startDiseaseConfidences;
        this.recommendationMessages = recommendationMessages;
    }

    public void addRecommendationMessage(SimpleMessage message){
        this.recommendationMessages.add(message);
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

    public List<SimpleMessage> getRecommendationMessages() {
        return recommendationMessages;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
