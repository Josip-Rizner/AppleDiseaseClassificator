package com.example.applediseaseclassificator;

import org.json.JSONException;
import org.json.JSONObject;

public class CityInformation {

    private String name, country;
    private double latitude, longitude;

    public static CityInformation fromJson(JSONObject jsonObject){
        try {
            CityInformation cityInformation = new CityInformation();
            cityInformation.name = jsonObject.getString("name");
            cityInformation.latitude = jsonObject.getDouble("lat");
            cityInformation.longitude = jsonObject.getDouble("long");
            cityInformation.country = jsonObject.getString("country");

            return cityInformation;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
