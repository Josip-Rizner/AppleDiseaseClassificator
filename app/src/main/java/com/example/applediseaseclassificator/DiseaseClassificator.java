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
    private final String[] classes = {"complex", "frog_eye_leaf_spot", "frog_eye_leaf_spot complex", "healthy", "powdery_mildew", "powdery_mildew complex", "rust", "rust complex", "rust frog_eye_leaf_spot", "scab", "scab frog_eye_leaf_spot", "scab frog_eye_leaf_spot complex"};
    private Context context;

    private float[] diseaseConfidences;
    private String classifiedClass;
    private int maxConfidencePosition;

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
            this.diseaseConfidences = confidence;
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

            // Releases model resources if no longer used.
            model.close();


        } catch (IOException e) {
            // TODO Handle the exception
        }
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
