package com.example.applediseaseclassificator;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.applediseaseclassificator.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DiseaseClassificator {

    private final int imageSize = 128;
    private static final String[] classes = {"complex", "frog_eye_leaf_spot", "frog_eye_leaf_spot complex", "healthy", "powdery_mildew", "powdery_mildew complex", "rust", "rust complex", "rust frog_eye_leaf_spot", "scab", "scab frog_eye_leaf_spot", "scab frog_eye_leaf_spot complex"};
    private Context context;

    private float[] diseaseConfidences;
    private String classifiedClass;
    private int maxConfidencePosition;

    public static final String[] diseaseDescriptions = {"To complex to tell", "Frog eye leaf spot", "It is hard to tell but Frog eye leaf spot is the most likely disease", "Healthy", "Powdery mildew", "It is hard to tell but Powdery mildew is the most likely disease", "Rust", "It is hard to tell but Rust is the most likely disease", "Rust and Frog eye leaf spot", "Scab", "Scab and Frog eye leaf spot", "It is hard to tell but Scab and Frog eye leaf spot are the most likely diseases"};

    public DiseaseClassificator(Context context){
        this.context = context;
    }


    public void classifyDisease(Bitmap image){
        try {
            Model model = Model.newInstance(context);

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

            this.maxConfidencePosition = maxPos;
            this.classifiedClass = classes[maxPos];
            this.diseaseConfidences = rescaleConfidences(confidence);

            // Releases model resources if no longer used.
            model.close();


        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    public float[] rescaleConfidences(float[] inputArray) {
        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;

        // Find the minimum and maximum values in the input array
        for (float value : inputArray) {
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // Scale the values to the [0, 1] interval
        float scaleFactor = maxValue - minValue;
        float[] scaledArray = new float[inputArray.length];

        for (int i = 0; i < inputArray.length; i++) {
            scaledArray[i] = (inputArray[i] - minValue) / scaleFactor;
        }

        return scaledArray;
    }

    public static int getClassIndex(String className){
        for (int i = 0; i < classes.length; i++) {
            if (classes[i] == className) {
                return i;
            }
        }
        return -1; // Element not found
    }

    public String getDiseaseClassDescription(int position){
        return diseaseDescriptions[position];
    }

    public static String getDiseaseClassDescription(String className){
        return diseaseDescriptions[getClassIndex(className)];
    }

    public int getPositionOfMaxConfidence(float[] confidence){
        int position = -1;
        float maxConfidence = confidence[0];
        
        for(int i = 0; i < confidence.length; i++){
            if(confidence[i] > maxConfidence){
                maxConfidence = confidence[i];
                position = i;
            }
        }
        return position;
    }

    public static String getDiseaseName(int position){
        return classes[position];
    }

    public static String getDiseaseTreatmentRecommendation(String className){

        int classIndex = getClassIndex(className);

        final String[] diseaseTreatmentRecommendation = {"Consult with the expert since it is to complex to classify", //to complex to tell
                "Treat with Azoxystrobin and Difenconazole-Based Fungicides and Copper-Based Fungicides\n\nExamples:\n-Delan 700 WDG\n-Captan 50\n-Polyram DF", //frog eye leaf spots
                "It is hard to tell but treating with Azoxystrobin and Difenconazole-Based Fungicides and Copper-Based Fungicides should help\n\nExamples:\n-Chromosul 80", // complex frog eye leaf spots
                "", //healthy
                "Treat with Sulfur\n\nExamples:\n-Chromosul 80", //powdery mildew
                "It is hard to tell but treating with sulfur should help\n\nExamples:\n-Chromosul 80", //complex powdery mildew
                "Treat with Copper-Based and Mancozeb-Based Fungicides\n\nExamples:\n-Strobe\n-Topaz\n-Vectra\n-Tsineba", //rust
                "It is hard to tell but treating with Copper-Based and Mancozeb-Based Fungicides should help\n\nExamples:\n-Strobe\n-Topaz", //complex rust
                "Treat with Copper-Based Fungicides\n\nExamples:\n-Poliram", // rust frog eye leaf spots
                "Treat with Propiconazole-Based and Captan-Based Fungicides\n\nExamples:\n-Kastor\n-Argo\n-Bellis", //scab
                "Treat with the mix of Azoxystrobin and Difenconazole-Based and Propiconazole-Based Fungicides but consult with the expert for the best mix", //scab frog eye leaf spots
                "It is hard to tell but treating with mix of Azoxystrobin and Difenconazole-Based and Propiconazole-Based Fungicides should help. Consult with the expert for the best mix"}; //complex scab frog eye leaf spots


        return diseaseTreatmentRecommendation[classIndex];
    }

    public int getImageSize() {
        return imageSize;
    }

    public String[] getClasses() {
        return classes;
    }

    public Context getContext() {
        return context;
    }

    public float[] getDiseaseConfidences() {
        return diseaseConfidences;
    }

    public String getClassifiedClass() {
        return classifiedClass;
    }

    public int getMaxConfidencePosition() {
        return maxConfidencePosition;
    }
}
