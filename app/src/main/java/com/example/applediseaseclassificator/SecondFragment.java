package com.example.applediseaseclassificator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SecondFragment extends Fragment {

    final String API_KEY = "8edb0515f032b02779527dc60e0023e4";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    final String CITY_LOCATION_URL = "https://api.openweathermap.org/geo/1.0/direct";

    final long MIN_TIME = 300000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String location_provider = LocationManager.GPS_PROVIDER;

    TextView tvCity, tvWeatherState, tvTemperature;
    ImageView ivWeatherState;

    SwitchCompat swUseCurrentLocation;
    EditText etEnterCity;
    Button btnFetchWeather;
    LinearLayout llSetLocation;
    ImageButton ibRefresh;


    LocationManager locationManager;
    LocationListener locationListener;

    LoadingDialog loadingDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }


    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tvWeatherState = view.findViewById(R.id.tvWeatherState);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvCity = view.findViewById(R.id.tvCity);
        ivWeatherState = view.findViewById(R.id.ivWeatherState);
        llSetLocation = view.findViewById(R.id.llSetLocation);
        swUseCurrentLocation = view.findViewById(R.id.swUseCurrentLocation);
        etEnterCity = view.findViewById(R.id.etEnterCity);
        btnFetchWeather = view.findViewById(R.id.btnFetchWeather);
        ibRefresh = view.findViewById(R.id.ibRefresh);

        loadingDialog = new LoadingDialog(getActivity());

        swUseCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    llSetLocation.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Fetching data for current location", Toast.LENGTH_SHORT).show();
                    getWeatherForCurrentLocation();
                }
                else{
                    llSetLocation.setVisibility(View.VISIBLE);
                }

            }
        });

        btnFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etEnterCity.getText().toString())) {
                    etEnterCity.setError("Please enter the City");
                    etEnterCity.requestFocus();
                    return;
                }
                getWeatherForCurrentLocation();
            }
        });

        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeatherForCurrentLocation();
            }
        });

        getWeatherForCurrentLocation();

        return view;
    }

    private void getWeatherForCurrentLocation() {

        if(!swUseCurrentLocation.isChecked()){
            String city = etEnterCity.getText().toString().trim();
            createCityLocationRequest(city);
            return;
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            //test
            @Override
            public void onLocationChanged(@NonNull Location location) {

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                createWeatherRequest(latitude, longitude);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Toast.makeText(getActivity(), "Location turned off", Toast.LENGTH_SHORT).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(location_provider, MIN_TIME, MIN_DISTANCE, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Location on", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
        }
        else{
            Toast.makeText(getActivity(), "Da Fuuuuck", Toast.LENGTH_SHORT).show();
            //User denied permission
        }
    }

    private void fetchData(RequestParams params, String URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();
                loadingDialog.showLoadingDialog();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getActivity(), "Data fetched successfully ", Toast.LENGTH_SHORT).show();
                WeatherData weatherData = WeatherData.fromJson(response);
                updateUI(weatherData);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if(response.length() == 0){
                    Toast.makeText(getActivity(), "Please check if you spelled City name correctly", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity(), "Data fetched successfully ", Toast.LENGTH_SHORT).show();
                CityInformation cityInformation = CityInformation.fromJson(response);
                String latitude = Double.toString(cityInformation.getLatitude());
                String longitude = Double.toString(cityInformation.getLongitude());
                createWeatherRequest(latitude, longitude);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), "Data NOT fetched", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), "Data NOT fetched", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFinish() {
                super.onFinish();
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void createWeatherRequest(String latitude, String longitude){
        RequestParams params = new RequestParams();

        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("units", "metric");
        params.put("appid", API_KEY);

        fetchData(params, WEATHER_URL);
    }

    private  void createCityLocationRequest(String cityName){
        RequestParams params = new RequestParams();
        params.put("q", cityName);
        params.put("limit", 1);
        params.put("appid", API_KEY);

        fetchData(params, CITY_LOCATION_URL);
    }

    private void updateUI(WeatherData weatherData){
        tvTemperature.setText(weatherData.getTemperature());
        tvCity.setText(weatherData.getCity());
        tvWeatherState.setText(weatherData.getWeatherType());
        int resourceId = getResources().getIdentifier(weatherData.getIcon(), "drawable", getActivity().getPackageName());
        ivWeatherState.setImageResource(resourceId);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}