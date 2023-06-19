package com.example.applediseaseclassificator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CityInformation {

    private String name, country;
    private double latitude, longitude;

    public static CityInformation fromJson(JSONArray jsonArray){
        try {
            CityInformation cityInformation = new CityInformation();
            cityInformation.name = jsonArray.getJSONObject(0).getString("name");
            cityInformation.latitude = jsonArray.getJSONObject(0).getDouble("lat");
            cityInformation.longitude = jsonArray.getJSONObject(0).getDouble("lon");
            cityInformation.country = jsonArray.getJSONObject(0).getString("country");

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
