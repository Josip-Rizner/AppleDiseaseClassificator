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

    Button btnOpenCamera, btnOpenGallery;
    ImageView ivImage;
    TextView tvClass;
    int imageSize = 128;

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
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        if(setImage != null){
            ivImage.setImageBitmap(setImage);
        }

        if(setClass != null){
            tvClass.setText(setClass);
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


        return view;
    }

    public void classifyDisease(Bitmap image){
        try {
            Model model = Model.newInstance(getContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            int pixel = 0;
            //iterate over each pixel and extract R, G and B values. Add those values individually to the byte buffer.
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            float maxConfidence = 0;
            int maxPos = 0;
            for(int i = 0; i < confidence.length; i++){
                if(confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }

            String[] classes = {"complex", "frog_eye_leaf_spot", "frog_eye_leaf_spot complex", "healthy", "powdery_mildew", "powdery_mildew complex", "rust", "rust complex", "rust frog_eye_leaf_spot", "scab", "scab frog_eye_leaf_spot", "scab frog_eye_leaf_spot complex"};
            tvClass.setText(classes[maxPos]);
            setClass = classes[maxPos];

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
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
            ivImage.setImageBitmap(image);
            setImage = image;

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyDisease(image);
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
            ivImage.setImageBitmap(image);
            setImage = image;

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyDisease(image);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}