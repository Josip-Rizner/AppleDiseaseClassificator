package com.example.applediseaseclassificator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, RecommendationSystemRecyclerViewOnClickInterface {

    FirebaseAuth auth;
    FirebaseUser user;
    Button btnLogout;
    TextView tvUser;

    BottomNavigationView bottomNavigationView;

    ClassifyDiseaseFragment classifyDiseaseFragment = new ClassifyDiseaseFragment();
    WeatherForecastFragment weatherForecastFragment = new WeatherForecastFragment();
    RecommendationSystemsFragment recommendationSystemsFragment = new RecommendationSystemsFragment();
    InfoFragment infoFragment = new InfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        btnLogout = findViewById(R.id.btnLogout);
        tvUser = findViewById(R.id.test);

        Bundle bundle = getIntent().getExtras();
        int tab = bundle.getInt("tab");
        bottomNavigationView = findViewById(R.id.bnvBottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        if(tab == 1){
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }
        else if(tab == 2){
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        }
        else if(tab == 3){
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }
        else{
            bottomNavigationView.setSelectedItemId(R.id.tab4);
        }

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            tvUser.setText(user.getEmail());
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.tab1:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, infoFragment).commit();
                return true;

            case R.id.tab2:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, classifyDiseaseFragment).commit();
                return true;

            case R.id.tab3:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, weatherForecastFragment).commit();
                return true;

            case R.id.tab4:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, recommendationSystemsFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public void onRecommendationSystemItemClick(RecommendationSystem recommendationSystem) {
        //This handles on item click from recycler view in recommendationSystemsFragment
        Intent intent = new Intent(this, RecommendationsActivity.class);
        intent.putExtra("recommendation_system_id", recommendationSystem.getId());
        startActivity(intent);
    }
}