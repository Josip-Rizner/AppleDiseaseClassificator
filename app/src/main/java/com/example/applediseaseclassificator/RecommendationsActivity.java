package com.example.applediseaseclassificator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecommendationsActivity extends AppCompatActivity {

    private TextView tvTest;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        Bundle bundle = getIntent().getExtras();
        String recommendationSystemId = bundle.getString("recommendation_system_id");

        tvTest = findViewById(R.id.tvTest);
        btnBack = findViewById(R.id.btnBack);
        tvTest.setText(recommendationSystemId);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tab", 3);
                startActivity(intent);
                finish();
            }
        });
    }
}