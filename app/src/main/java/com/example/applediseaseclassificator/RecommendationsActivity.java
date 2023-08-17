package com.example.applediseaseclassificator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RecommendationsActivity extends AppCompatActivity {

    final String API_KEY = "8edb0515f032b02779527dc60e0023e4";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/forecast";

    private TextView tvName, tvLatLong, tvDisease;
    private ImageButton btnBack, btnDelete;
    private ImageView ivStartingImage;
    private TextView btnTreatingRecommendation, btnDiseaseCheck, btnWhenToTreatRecommendation;

    private RecommendationSystem recommendationSystem;
    private DiseaseClassificator diseaseClassificator;

    RecyclerView rvRecommendationMessages;
    RecommendationMessagesAdapter recommendationMessagesAdapter;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        Bundle bundle = getIntent().getExtras();
        String recommendationSystemId = bundle.getString("recommendation_system_id");

        tvName = findViewById(R.id.tvName);
        tvLatLong = findViewById(R.id.tvLatLong);
        tvDisease = findViewById(R.id.tvClass);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        ivStartingImage = findViewById(R.id.ivStartingImage);
        btnTreatingRecommendation = findViewById(R.id.btnTreatingRecommendation);
        btnDiseaseCheck = findViewById(R.id.btnDiseaseCheck);
        btnWhenToTreatRecommendation = findViewById(R.id.btnWhenToTreatRecommendation);

        rvRecommendationMessages = findViewById(R.id.rvRecommendationMessages);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("RecommendationSystems").child(user.getUid()).child(recommendationSystemId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RecommendationSystem recommendationSystem = snapshot.getValue(RecommendationSystem.class);
                if (recommendationSystem == null){
                    return;
                }
                setRecommendationSystem(recommendationSystem);
                updateRecommendationMessages(recommendationSystem);

                tvName.setText(recommendationSystem.getName());
                tvLatLong.setText(recommendationSystem.getLatitude() + " / " + recommendationSystem.getLongitude());
                tvDisease.setText(recommendationSystem.getClassifiedDisease());

                Picasso.with(getApplicationContext())
                        .load(recommendationSystem.getStartingImageReference())
                        .fit()
                        .centerCrop()
                        .into(ivStartingImage);


                SimpleMessage lastMessage = recommendationSystem.getRecommendationMessages().get(recommendationSystem.getRecommendationMessages().size()-1);
                if (lastMessage.getRecommendationMessageType() == 10){
                    createTreatmentRecommendation();
                }
                else if (lastMessage.getRecommendationMessageType() == 11){
                    createWeatherRequest(recommendationSystem.getLatitude(), recommendationSystem.getLongitude());
                }
                else if (lastMessage.getRecommendationMessageType() == 11){
                    createWeatherRequest(recommendationSystem.getLatitude(), recommendationSystem.getLongitude());
                }
                else if (lastMessage.getRecommendationMessageType() == 30){
                    createTreatmentEffectivenessAnswer(lastMessage.getDiseaseConfidences());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecommendationsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tab", 3);
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecommendationSystem(recommendationSystem.getId());
            }
        });

        btnTreatingRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTreatmentRecommendationRequest();
            }
        });

        btnDiseaseCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog();
            }
        });

        btnWhenToTreatRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWeatherCheckRequest();
            }
        });

    }


    private void updateRecommendationMessages(RecommendationSystem recommendationSystem) {
        List<SimpleMessage> messages = recommendationSystem.getRecommendationMessages();
        recommendationMessagesAdapter = new RecommendationMessagesAdapter(RecommendationsActivity.this , messages);
        rvRecommendationMessages.setHasFixedSize(true);
        rvRecommendationMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecommendationMessages.setAdapter(recommendationMessagesAdapter);
        rvRecommendationMessages.smoothScrollToPosition(recommendationMessagesAdapter.getItemCount() - 1);

        updateUI(messages.get(messages.size() - 1).getRecommendationMessageType());
    }

    private void updateUI(int lastMsgType) {
        if (lastMsgType == 1){
            btnDiseaseCheck.setVisibility(View.GONE);
            btnTreatingRecommendation.setVisibility(View.GONE);
            btnWhenToTreatRecommendation.setVisibility(View.GONE);
        }
    }

    public void setRecommendationSystem(RecommendationSystem recommendationSystem) {
        this.recommendationSystem = recommendationSystem;
    }

    public void addRecommendationMessage(SimpleMessage message){

        int key = recommendationSystem.getRecommendationMessages().size();

        //reference is already set to the Recommendation system id in the OnCreate method
        databaseReference.child("recommendationMessages").child(String.valueOf(key)).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    //Toast.makeText(RecommendationsActivity.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RecommendationsActivity.this, "Couldn't save data, please try later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void createTreatmentRecommendationRequest(){
        SimpleMessage message = new SimpleMessage("What should I use to treat my plantation?", 10);
        addRecommendationMessage(message);
    }

    private void createTreatmentRecommendation() {
        List<Float> lastDiseaseConfidences = getDiseaseConfidencesFromLastCheck();
        String className = DiseaseClassificator.getDiseaseName(getHighestConfidenceIndex(lastDiseaseConfidences));
        SimpleMessage message = new SimpleMessage("this is treatment recommendation", 0);
        addRecommendationMessage(message);
    }

    private void createDiseaseCheckRequest(String url){
        List<Float> confidences = new ArrayList<>();
        for (float diseaseConfidence : diseaseClassificator.getDiseaseConfidences()){
            confidences.add(diseaseConfidence);
        }
        SimpleMessage message = new SimpleMessage("Is my treatment working", url, confidences, 30);
        addRecommendationMessage(message);
    }

    private void createTreatmentEffectivenessAnswer(List<Float> currentDiseaseConfidences){
        List<Float> pastDiseaseConfidences = getDiseaseConfidencesFromLastCheck();
        String currentDisease = DiseaseClassificator.getDiseaseName(getHighestConfidenceIndex(currentDiseaseConfidences));
        String lastClassifiedDisease = DiseaseClassificator.getDiseaseName(getHighestConfidenceIndex(pastDiseaseConfidences));

        if (currentDisease == "healthy"){
            addRecommendationMessage(new SimpleMessage("Great news! Treatment worked and your plantation is now healthy", 1));
        } else if(currentDisease != lastClassifiedDisease){
            String currentDiseaseDescription = DiseaseClassificator.getDiseaseClassDescription(currentDisease);
            String lastClassifiedDiseaseDescription = DiseaseClassificator.getDiseaseClassDescription(lastClassifiedDisease);
            SimpleMessage message = new SimpleMessage("Looks like your plantation is now infected with:\n\n" + currentDiseaseDescription + "\n\nPreviously it was infected with: " + lastClassifiedDiseaseDescription + "\n\nTreatment didn't work", 2);
            addRecommendationMessage(message);
        }
        else if(currentDisease == lastClassifiedDisease){
            float treatmentEffectivenessInPercentage = getTreatmentEffectiveness(pastDiseaseConfidences.get(DiseaseClassificator.getClassIndex("healthy")), currentDiseaseConfidences.get(DiseaseClassificator.getClassIndex("healthy")));
            SimpleMessage message = new SimpleMessage(getTreatmentEffectivenessMessage(treatmentEffectivenessInPercentage), getTreatmentEffectivenessImageUrl(treatmentEffectivenessInPercentage), 40);
            addRecommendationMessage(message);
        }

    }

    private float getTreatmentEffectiveness(float confidenceBefore, float confidenceNow){

        return 75f;
    }

    private int getHighestConfidenceIndex(List<Float> diseaseConfidences){
        int highestConfidenceIndex = 0;
        float maxConfidence = diseaseConfidences.get(0);
        for (int i = 0; i < diseaseConfidences.size(); i++) {
            if(diseaseConfidences.get(i) > maxConfidence){
                maxConfidence = diseaseConfidences.get(i);
                highestConfidenceIndex = i;
            }
        }
        return highestConfidenceIndex;
    }

    private List<Float> getDiseaseConfidencesFromLastCheck() {

        List<SimpleMessage> messages = recommendationSystem.getRecommendationMessages();

        for(int i = messages.size()-2; i >= 0; i--){
            if (messages.get(i).getRecommendationMessageType() == 30){
                return messages.get(i).getDiseaseConfidences();
            }
        }

        return recommendationSystem.getStartDiseaseConfidences();
    }

    private void createWeatherCheckRequest(){
        SimpleMessage message = new SimpleMessage("When is the right time to treat my plantation?", 11);
        addRecommendationMessage(message);
    }

    private void createWeatherCheckRecommendation(WeatherData weatherData){
        List<Integer> weatherIds = weatherData.getWeatherIdsList();
        List<String> date_time = weatherData.getDateTimeList();
        //24 is 3 days since forecast is in steps of 3 hours
        List<String> treatmentWindow = findTreatmentWindow(weatherIds, date_time, 24);
        int mostOccurring;

        if(treatmentWindow.size() == 0){
            mostOccurring = weatherData.findMostOccurringBadWeatherId(weatherIds);
        }
        else {
            mostOccurring = weatherData.findMostOccurringWeatherId(weatherIds);
        }

        String url = weatherData.getWeatherImageUrl(mostOccurring);
        String forecastMessage = weatherData.getWeatherForecastMessage(mostOccurring);
        SimpleMessage message;

        if(treatmentWindow.size() == 0){
            message = new SimpleMessage("Unfortunately there is no window where plantation can be treated. \nThere will be " + forecastMessage + " in the next 5 days.\n\nPlease check for the treatment window in the few days.", url, 20);

        }
        else {
            message = new SimpleMessage("There is window from: " + treatmentWindow.get(0) + " - " + treatmentWindow.get(treatmentWindow.size() - 1) + "\nIn that period it will be " + forecastMessage + "\n\nIt is recommended to threat your plantation as close to the first date as possible for the best results.", url, 20);
        }
        addRecommendationMessage(message);
    }

    public static List<String> findTreatmentWindow(List<Integer> weatherIds, List<String> date_times, int treatingWindowSize) {
        List<String> treatingWindow = new ArrayList<>();

        for (int i = 0; i < weatherIds.size(); i++) {
            int currentValue = weatherIds.get(i);
            String currentString = date_times.get(i);

            if (currentValue >= 800) {
                treatingWindow.add(currentString);
            } else {
                if(treatingWindow.size() < treatingWindowSize){
                    treatingWindow.clear();
                }
                else{
                    break;
                }
            }
        }

        if(treatingWindow.size() < treatingWindowSize){
            treatingWindow.clear();
        }

        return treatingWindow;
    }

    private void showOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_loading_options, null);
        builder.setView(dialogView);

        Button btnTakePicture = dialogView.findViewById(R.id.btnTakePictureToCheckDisease);
        Button btnLoadImage = dialogView.findViewById(R.id.btnLoadImageToCheckDisease);

        AlertDialog dialog = builder.create();

        btnTakePicture.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });

        btnLoadImage.setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });

        dialog.show();
    }

    private void openCamera() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,3);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        DiseaseClassificator diseaseClassificator = new DiseaseClassificator(RecommendationsActivity.this);
        int imageSize = diseaseClassificator.getImageSize();

        if (requestCode == 3){
            Bitmap image = null;
            try {
                image = (Bitmap) data.getExtras().get("data");
            } catch (RuntimeException runtimeException ) {
                //In case picture is not loaded so that app doesn't crash
                return;
            }

            //Because neural network was trained on square images
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            Bitmap originalImage  = image;

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            diseaseClassificator.classifyDisease(image);
            this.diseaseClassificator = diseaseClassificator;
            saveImage(originalImage);

        }
        else{
            Uri dat = null;
            try {
                dat = data.getData();
            } catch (RuntimeException runtimeException ) {
                //In case picture is not loaded so that app doesn't crash
                return;
            }

            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            Bitmap originalImage = image;

            diseaseClassificator.classifyDisease(image);
            this.diseaseClassificator = diseaseClassificator;
            saveImage(originalImage);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void saveImage(Bitmap image){
        Uri uriImage = getImageUri(image, Bitmap.CompressFormat.JPEG, 100);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(user.getUid()).child(recommendationSystem.getId());

        if(uriImage != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uriImage));
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(RecommendationsActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            createDiseaseCheckRequest(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RecommendationsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(RecommendationsActivity.this, "Couldn't get the image.", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Bitmap source, Bitmap.CompressFormat format, int quality){
        Context context = getApplicationContext();
        File imageFile = new File(context.getCacheDir(), "image.jpg");

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            source.compress(format, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void createWeatherRequest(String latitude, String longitude){
        RequestParams params = new RequestParams();

        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("units", "metric");
        params.put("appid", API_KEY);

        fetchData(params, WEATHER_URL);
    }

    private void fetchData(RequestParams params, String URL){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //Toast.makeText(RecommendationsActivity.this, "Data fetched successfully ", Toast.LENGTH_SHORT).show();
                WeatherData weatherData = WeatherData.fromJson(response);
                createWeatherCheckRecommendation(weatherData);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(RecommendationsActivity.this, "Couldn't fetch weather data, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRecommendationSystem(String id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning")
                .setMessage("Are you sure you want to delete this recommendation System?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecommendationSystems").child(user.getUid()).child(id);

                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(RecommendationsActivity.this, "Recommendation system deleted successfully", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                   intent.putExtra("tab", 3);
                                   startActivity(intent);
                                   finish();

                               } else{
                                   Toast.makeText(RecommendationsActivity.this, "Recommendation system can't be deleted currently, please try later.", Toast.LENGTH_SHORT).show();
                                   finish();
                               }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private String getTreatmentEffectivenessImageUrl(float effectiveness){
        String url = "";
        if (effectiveness > 50F){
            url = "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fbetter.png?alt=media&token=b0ea3ee0-e4bd-4928-bcba-6d129a1204b6";
        }
        else if (effectiveness > 5F && effectiveness <= 50F){
            url = "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fslightly_better.png?alt=media&token=72e36a2d-0d8f-4d98-b061-9557b2f3e831";
        }
        else if (effectiveness >= -5F && effectiveness <= 5F){
            url = "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fsame.png?alt=media&token=8ac07a2f-1f5d-4295-af6b-857bb51c3a81";
        }
        else if (effectiveness < -5F && effectiveness >= -50F){
            url = "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fslightly_worse.png?alt=media&token=e042739a-0eb1-4697-b1a7-f1bab3d835f1";
        }
        else if (effectiveness < -50F){
            url = "https://firebasestorage.googleapis.com/v0/b/applediseaseclassificator.appspot.com/o/predefined%2Fworse.png?alt=media&token=2af7e447-c022-483c-a508-2ce1441d011a";
        }
        return url;
    }

    private String getTreatmentEffectivenessMessage(float effectiveness){
        String message = "";
        if (effectiveness > 50F){
            message = "A lot better";
        }
        else if (effectiveness > 5F && effectiveness <= 50F){
            message = "better";
        }
        else if (effectiveness >= -5F && effectiveness <= 5F){
            message = "Roughly the same, no progress";
        }
        else if (effectiveness < -5F && effectiveness >= -50F){
            message = "worse";
        }
        else if (effectiveness < -50F){
            message = "A lot worse";
        }
        return message;
    }
}