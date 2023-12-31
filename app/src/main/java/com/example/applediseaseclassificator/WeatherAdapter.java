package com.example.applediseaseclassificator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private JSONObject[] weatherForecastData;

    public WeatherAdapter(JSONObject[] weatherForecastData){
        this.weatherForecastData = weatherForecastData;
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivWeatherState;
        public TextView tvTemperature, tvWeatherState, tvDate;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            this.ivWeatherState = itemView.findViewById(R.id.ivWeatherState);
            this.tvWeatherState = itemView.findViewById(R.id.tvWeatherState);
            this.tvTemperature = itemView.findViewById(R.id.tvTemperature);
            this.tvDate = itemView.findViewById(R.id.tvDate);

        }
    }


    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.recyvlerview_weather_item, parent, false);

        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(listItem);

        return weatherViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        final JSONObject weatherForecast = weatherForecastData[position];
        try {
            holder.tvTemperature.setText(Integer.toString((int) weatherForecast.getJSONObject("main").getDouble("temp")) + "°C");
            holder.tvWeatherState.setText(weatherForecast.getJSONArray("weather").getJSONObject(0).getString("main"));
            holder.tvDate.setText(weatherForecast.getString("dt_txt"));
            holder.ivWeatherState.setImageResource(android.R.color.transparent);
            holder.ivWeatherState.setImageResource(weatherForecast.getJSONArray("weather").getJSONObject(0).getInt("image_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherForecastData.length;
    }

}
