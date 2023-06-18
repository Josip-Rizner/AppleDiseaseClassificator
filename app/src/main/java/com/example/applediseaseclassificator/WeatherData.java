package com.example.applediseaseclassificator;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String temperature, icon, city, weatherType;
    private int condition;

    public static WeatherData fromJson(JSONObject jsonObject){
        try {
            WeatherData weatherData = new WeatherData();
            weatherData.city = jsonObject.getString("name");
            weatherData.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.icon = updateWeatherIcon(weatherData.condition);
            weatherData.temperature = Double.toString(jsonObject.getJSONObject("main").getDouble("temp"));
            return weatherData;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int condition){
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
        else if(condition == 803 || condition == 804){
            return "scattered_clouds";
        }

        return "not_supported";
    }

    public String getTemperature() {
        return temperature + "°C";
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
