package com.example.applediseaseclassificator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class RecommendationsActivity extends AppCompatActivity {

    private TextView tvName, tvLatLong, tvDisease;
    private ImageButton btnBack;
    private ImageView ivStartingImage;

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
        ivStartingImage = findViewById(R.id.ivStartingImage);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("RecommendationSystems").child(user.getUid()).child(recommendationSystemId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RecommendationSystem recommendationSystem = snapshot.getValue(RecommendationSystem.class);

                tvName.setText(recommendationSystem.getName());
                tvLatLong.setText(recommendationSystem.getLatitude() + " / " + recommendationSystem.getLongitude());
                tvDisease.setText(recommendationSystem.getClassifiedDisease());

                Picasso.with(getApplicationContext())
                        .load(recommendationSystem.getStartingImageReference())
                        .fit()
                        .centerCrop()
                        .into(ivStartingImage);

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
                intent.putExtra("tab", 4);
                startActivity(intent);
                finish();
            }
        });


    }
}