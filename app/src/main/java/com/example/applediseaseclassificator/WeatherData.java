package com.example.applediseaseclassificator;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String currentTemperature, currentIcon, city, currentWeatherType;
    private static JSONArray weatherForecast;

    public static WeatherData fromJson(JSONObject jsonObject){
        try {
            WeatherData weatherData = new WeatherData();
            weatherData.weatherForecast = jsonObject.getJSONArray("list");
            weatherForecast = weatherData.weatherForecast;
            weatherData.city =  jsonObject.getJSONObject("city").getString("name");
            int condition = weatherData.weatherForecast.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.currentWeatherType = weatherData.weatherForecast.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.currentIcon = updateWeatherIcon(condition);
            weatherData.currentTemperature = Integer.toString((int) weatherData.weatherForecast.getJSONObject(0).getJSONObject("main").getDouble("temp"));
            return weatherData;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
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
