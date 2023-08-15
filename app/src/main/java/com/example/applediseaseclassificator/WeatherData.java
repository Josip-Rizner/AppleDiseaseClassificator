package com.example.applediseaseclassificator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherData {

    private String currentTemperature, currentIcon, city, currentWeatherType;
    private static JSONArray weatherForecast;

    public static WeatherData fromJson(JSONObject jsonObject){
        try {
            WeatherData weatherData = new WeatherData();
            weatherForecast = jsonObject.getJSONArray("list");
            weatherForecast = weatherForecast;
            weatherData.city =  jsonObject.getJSONObject("city").getString("name");
            int condition = weatherForecast.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.currentWeatherType = weatherForecast.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.currentIcon = updateWeatherIcon(condition);
            weatherData.currentTemperature = Integer.toString((int) weatherForecast.getJSONObject(0).getJSONObject("main").getDouble("temp"));
            return weatherData;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Integer> getWeatherIdsList(){
        List<Integer> weatherIds = new ArrayList<>();

        for (int i = 0; i < getWeatherForecastLength(); i++){
            try {
                weatherIds.add(weatherForecast.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return weatherIds;
    }

    public List<String> getDateTimeList(){
        List<String> date_time = new ArrayList<>();

        for (int i = 0; i < getWeatherForecastLength(); i++){
            try {
                date_time.add(weatherForecast.getJSONObject(i).getString("dt_txt"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return date_time;
    }

    @SuppressLint("NewApi")
    public static int findMostOccurringWeatherId(List<Integer> weatherIds) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        // Count the frequency of each number
        for (int num : weatherIds) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        int mostOccurringValue = 0;
        int maxFrequency = 0;

        // Find the most occurring value
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostOccurringValue = entry.getKey();
            }
        }

        return mostOccurringValue;
    }


    @SuppressLint("NewApi")
    public static int findMostOccurringBadWeatherId(List<Integer> weatherIds) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        // Count the frequency of each number that is not higher than 800
        for (int num : weatherIds) {
            if (num <= 800) {
                frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
            }
        }

        int mostOccurringValue = 0;
        int maxFrequency = 0;

        // Find the most occurring value
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostOccurringValue = entry.getKey();
            }
        }

        return mostOccurringValue;
    }


    public static String updateWeatherIcon(int condition){
        //values are taken from Weather Condition Page/API documentation
        if(condition >= 200 && condition <= 232){
            return "thunderstorm";
        }
        else if(condition >= 300 && condition < 321){
            return "shower_rain";
        }
        else if(condition >= 500 && condition < 504){
            return "rain";
        }
        else if(condition == 511){
            return "snow";
        }
        else if(condition >= 521 && condition < 531){
            return "shower_rain";
        }
        else if(condition >= 600 && condition < 622){
            return "snow";
        }
        else if(condition >= 701 && condition < 781){
            return "mist";
        }
        else if(condition == 800){
            return "clear_sky";
        }
        else if(condition == 801){
            return "few_clouds";
        }
        else if(condition >= 802 && condition <= 804){
            return "scattered_clouds";
        }

        return "not_supported";
    }

    public static String getWeatherForecastMessage(int condition){
        //values are taken from Weather Condition Page/API documentation
        if(condition >= 200 && condition <= 232){
            return "thunderstorm";
        }
        else if(condition >= 300 && condition < 321){
            return "a lot of rain";
        }
        else if(condition >= 500 && condition < 504){
            return "rain";
        }
        else if(condition == 511){
            return "snow";
        }
        else if(condition >= 521 && condition < 531){
            return "a lot of rain";
        }
        else if(condition >= 600 && condition < 622){
            return "snow";
        }
        else if(condition >= 701 && condition < 781){
            return "mist";
        }
        else if(condition == 800){
            return "sunny";
        }
        else if(condition == 801){
            return "partially cloudy";
        }
        else if(condition >= 802 && condition <= 804){
            return "really cloudy";
        }

        return "not_supported";
    }

    public static String getWeatherImageUrl(int condition){
        //values are taken from Weather Condition Page/API documentation
        if(condition >= 200 && condition <= 232){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fthunderstorm.png?alt=media&token=2299189a-319b-4dac-82d7-9227e181fd8c";
        }
        else if(condition >= 300 && condition < 321){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fshower_rain.png?alt=media&token=c543c808-cad7-4d05-9d31-d3303d31a5d2";
        }
        else if(condition >= 500 && condition < 504){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Frain.png?alt=media&token=a779f157-c7f9-4f81-b032-7428b8c1fdc2";
        }
        else if(condition == 511){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fsnow.png?alt=media&token=93bd1476-6ce6-4af9-9692-dde8b42309c6";
        }
        else if(condition >= 521 && condition < 531){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fshower_rain.png?alt=media&token=c543c808-cad7-4d05-9d31-d3303d31a5d2";
        }
        else if(condition >= 600 && condition < 622){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fsnow.png?alt=media&token=93bd1476-6ce6-4af9-9692-dde8b42309c6";
        }
        else if(condition >= 701 && condition < 781){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fmist.png?alt=media&token=df76d25b-9ed5-4f38-b633-0cd588ec8bd5";
        }
        else if(condition == 800){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fclear_sky.png?alt=media&token=5c315b25-3ed2-422d-867b-b5dfe48d4e39";
        }
        else if(condition == 801){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Ffew_clouds.png?alt=media&token=8f6b63d8-02d0-4591-a58a-ec78f3f49b9b";
        }
        else if(condition >= 802 && condition <= 804){
            return "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fscattered_clouds.png?alt=media&token=52b56f27-a27c-4052-af79-785a3aadad75";
        }

        return "not_supported";
    }

    public int getWeatherForecastLength(){
        return weatherForecast.length();
    }

    public JSONObject[] getWeatherForecastAsJSONObjectList(){
        int listLength = getWeatherForecastLength();
        JSONObject[] forecast = new JSONObject[listLength];

        for(int i = 0; i < listLength; i++){
            try {
                forecast[i] = weatherForecast.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return forecast;
    }

    public JSONArray getWeatherForecast() {
        return weatherForecast;
    }

    public String getCurrentTemperature() {
        return currentTemperature + "°C";
    }

    public String getCurrentIcon() {
        return currentIcon;
    }

    public String getCity() {
        return city;
    }

    public String getCurrentWeatherType() {
        return currentWeatherType;
    }
}
