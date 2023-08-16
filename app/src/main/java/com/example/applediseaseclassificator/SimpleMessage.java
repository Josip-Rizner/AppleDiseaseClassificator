package com.example.applediseaseclassificator;

import java.util.ArrayList;
import java.util.List;

public class SimpleMessage {

    //recommendation message types:
    //0 - informative system message
    //1 - Healthy plantation message
    //2 - Treatment effectiveness answer - disease change
    //10 - User Treatment request
    //11 - User weather/when to treat request
    // Following messages are with images
    //20 - weather status answer
    //30 - User checking if treatment works
    //40 - Treatment effectiveness answer


    private int recommendationMessageType;
    private long timestamp;
    private String message;
    String imageReference;
    private List<Float> diseaseConfidences = new ArrayList<>();

    public SimpleMessage(){}

    public SimpleMessage(String message) {
        this.recommendationMessageType = 0;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public SimpleMessage(String message, int recommendationMessageType) {
        this.recommendationMessageType = recommendationMessageType;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public SimpleMessage(String message, String url, int recommendationMessageType) {
        this.imageReference = url;
        this.recommendationMessageType = recommendationMessageType;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public SimpleMessage(String message, String url, List<Float> diseaseConfidences, int recommendationMessageType) {
        this.imageReference = url;
        this.recommendationMessageType = recommendationMessageType;
        this.message = message;
        this.diseaseConfidences = diseaseConfidences;
        this.timestamp = System.currentTimeMillis();
    }

    protected void setRecommendationMessageType(int recommendationMessageType) {
        this.recommendationMessageType = recommendationMessageType;
    }

    public int getRecommendationMessageType() {
        return recommendationMessageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getImageReference() {
        return imageReference;
    }

    public List<Float> getDiseaseConfidences() {
        return diseaseConfidences;
    }
}
