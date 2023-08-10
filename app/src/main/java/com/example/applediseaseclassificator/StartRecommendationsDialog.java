package com.example.applediseaseclassificator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class StartRecommendationsDialog extends AppCompatDialogFragment {

    final String API_KEY = "8edb0515f032b02779527dc60e0023e4";
    final String CITY_LOCATION_URL = "https://api.openweathermap.org/geo/1.0/direct";

    final int REQUEST_CODE = 101;

    private EditText etSystemRecommendationName, etLocation;
    private SwitchCompat swUseCurrentLocation;
    private Button btnCreateRecommendationSystem, btnClose;
    private ImageView ivStartingImage;

    private LinearLayout llSetLocation;

    private Bitmap image;
    private String classifiedDisease;
    private List<Float> diseaseConfidences = new ArrayList<>();
    private String latitude = null;
    private String longitude = null;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public StartRecommendationsDialog(Bitmap image, String classifiedDisease, float[] diseaseConfidences){
        this.image = image;
        this.classifiedDisease = classifiedDisease;
        for (float diseaseConfidence : diseaseConfidences){
            this.diseaseConfidences.add(diseaseConfidence);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_start_recommendations, null);

        builder.setView(view);

        etSystemRecommendationName = view.findViewById(R.id.etRecommendationName);
        etLocation = view.findViewById(R.id.etLocation);
        swUseCurrentLocation = view.findViewById(R.id.swUseLocation);
        btnClose = view.findViewById(R.id.btnClose);
        btnCreateRecommendationSystem = view.findViewById(R.id.btnCreateRecommendationSystem);
        ivStartingImage = view.findViewById(R.id.ivStartingImageDialog);
        ivStartingImage.setImageBitmap(image);

        llSetLocation = view.findViewById(R.id.llOtherLocation);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnCreateRecommendationSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open new activity and send recommendation id

                if(TextUtils.isEmpty(etSystemRecommendationName.getText().toString())){
                    etSystemRecommendationName.setError("Name is required");
                    etSystemRecommendationName.requestFocus();
                    return;
                }

                if (swUseCurrentLocation.isChecked()){
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        return;
                    }

                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    setLatLong(location.getLatitude(), location.getLongitude());
                }
                else{
                    if(!(swUseCurrentLocation.isChecked()) && TextUtils.isEmpty(etLocation.getText().toString())){
                        etLocation.setError("City is required");
                        etLocation.requestFocus();
                        return;
                    }
                    else {
                        createCityLocationRequest(etLocation.getText().toString().trim());
                        return;
                    }
                }

                createRecommendationSystem();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        swUseCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    llSetLocation.setVisibility(View.GONE);
                }
                else{
                    llSetLocation.setVisibility(View.VISIBLE);
                }
            }
        });

        return builder.create();
    }

    private void createRecommendationSystem() {
        if (latitude == null || longitude == null){
            Toast.makeText(getActivity(), "Location is not set, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            saveData();
        }
    }

    private void createCityLocationRequest(String cityName){
        RequestParams params = new RequestParams();
        params.put("q", cityName);
        params.put("limit", 1);
        params.put("appid", API_KEY);

        fetchData(params, CITY_LOCATION_URL);
    }

    private void setLatLong(Double latitude, Double longitude){
        this.latitude = Double.toString(latitude);
        this.longitude = Double.toString(longitude);
    }

    private void saveData(){
        Uri uriImage = getImageUri(image, Bitmap.CompressFormat.JPEG, 1);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(user.getUid());

        if(uriImage != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uriImage));
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Image upload successful", Toast.LENGTH_SHORT).show();
                    String name = etSystemRecommendationName.getText().toString().trim();

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            saveRecommendationSystemInfo(latitude, longitude, name, uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            Toast.makeText(getActivity(), "Couldn't get the image.", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveRecommendationSystemInfo(String latitude, String longitude, String name, String image_reference){
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("RecommendationSystems");

        String recommendationSystemId = dbReference.push().getKey();
        RecommendationSystem recommendationSystem = new RecommendationSystem(recommendationSystemId ,latitude, longitude, name, image_reference, classifiedDisease, diseaseConfidences);

        dbReference.child(user.getUid()).child(recommendationSystemId).setValue(recommendationSystem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(getActivity(), "Data saved successfully.", Toast.LENGTH_SHORT).show();
                    openCreatedRecommendationSystem(recommendationSystemId);
                }
                else{
                    Toast.makeText(getActivity(), "Couldn't save data, please try later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Uri getImageUri(Bitmap source, Bitmap.CompressFormat format, int quality){
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        source.compress(format, quality, os);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), source, "title", null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openCreatedRecommendationSystem(String recommendationSystemId){
        Intent intent = new Intent(getContext(), RecommendationsActivity.class);
        intent.putExtra("recommendation_system_id", recommendationSystemId);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Location on", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getActivity(), "Can't get current location without permission to use location.", Toast.LENGTH_SHORT).show();
            //User denied permission
        }
    }

    private void fetchData(RequestParams params, String URL){

        String[] position;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if(response.length() == 0){
                    Toast.makeText(getActivity(), "Please check if you spelled City name correctly", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity(), "Data fetched successfully ", Toast.LENGTH_SHORT).show();
                CityInformation cityInformation = CityInformation.fromJson(response);
                setLatLong(cityInformation.getLatitude(), cityInformation.getLongitude());
                createRecommendationSystem();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), "Data NOT fetched", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
