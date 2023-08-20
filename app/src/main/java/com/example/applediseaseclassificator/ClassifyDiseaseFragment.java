package com.example.applediseaseclassificator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.applediseaseclassificator.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class ClassifyDiseaseFragment extends Fragment {

    Button btnOpenCamera, btnOpenGallery, btnStartRecommendationSystem;
    ImageView ivImage;
    TextView tvClass;
    int imageSize;
    DiseaseClassificator diseaseClassificator;

    private Bitmap setImage = null;
    private String setClass = null;


    public ClassifyDiseaseFragment() {
        // Required empty public constructor
    }

    public static ClassifyDiseaseFragment newInstance() {
        ClassifyDiseaseFragment fragment = new ClassifyDiseaseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (diseaseClassificator == null) {
            this.diseaseClassificator = new DiseaseClassificator(getContext());
            this.imageSize = diseaseClassificator.getImageSize();
        }

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        if(setImage != null){
            ivImage.setImageBitmap(setImage);
            btnStartRecommendationSystem.setVisibility(View.VISIBLE);
        }
        else {
            btnStartRecommendationSystem.setVisibility(View.GONE);
        }

        if(setClass != null){
            String classDescription = diseaseClassificator.getDiseaseClassDescription(DiseaseClassificator.getClassIndex(setClass));
            tvClass.setText(classDescription);
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_classify_disease, container, false);

        btnOpenCamera = view.findViewById(R.id.btnTakePicture);
        btnOpenGallery = view.findViewById(R.id.btnOpenGallery);
        tvClass = view.findViewById(R.id.tvClass);
        ivImage = view.findViewById(R.id.ivImage);
        btnStartRecommendationSystem = view.findViewById(R.id.btnCreateRecommendationSystem);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,3);
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });

        btnStartRecommendationSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartRecommendationsDialog startRecommendationsDialog = new StartRecommendationsDialog(setImage, diseaseClassificator.getClassifiedClass(), diseaseClassificator.getDiseaseConfidences());
                startRecommendationsDialog.show(getActivity().getSupportFragmentManager(), "start recommendation dialog");
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 3){
            Bitmap image = null;
            try {
                image = (Bitmap) data.getExtras().get("data");
            } catch (RuntimeException runtimeException ) {
                //In case picture is not loaded so that app doesn't crash
                return;
            }

            //Beacuse neural network was trained on square images
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            setImage(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            diseaseClassificator.classifyDisease(image);
            setClass = diseaseClassificator.getClassifiedClass();
            String classDescription = diseaseClassificator.getDiseaseClassDescription(DiseaseClassificator.getClassIndex(setClass));
            tvClass.setText(classDescription);
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
                image = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), dat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setImage(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            diseaseClassificator.classifyDisease(image);
            setClass = diseaseClassificator.getClassifiedClass();
            String classDescription = diseaseClassificator.getDiseaseClassDescription(DiseaseClassificator.getClassIndex(setClass));
            tvClass.setText(classDescription);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void setImage(Bitmap image){
        ivImage.setImageBitmap(image);
        setImage = image;

        btnStartRecommendationSystem.setVisibility(View.VISIBLE);
    }
}