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

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    FirebaseAuth auth;
    FirebaseUser user;
    Button btnLogout;
    TextView tvUser;

    BottomNavigationView bottomNavigationView;

    ClassifyDiseaseFragment classifyDiseaseFragment = new ClassifyDiseaseFragment();
    WeatherForecastFragment weatherForecastFragment = new WeatherForecastFragment();
    ThirdFragment thirdFragment = new ThirdFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        btnLogout = findViewById(R.id.btnLogout);
        tvUser = findViewById(R.id.test);

        bottomNavigationView = findViewById(R.id.bnvBottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.tab1);


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
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, classifyDiseaseFragment).commit();
                return true;

            case R.id.tab2:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, weatherForecastFragment).commit();
                return true;

            case R.id.tab3:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentCountainer, thirdFragment).commit();
                return true;
        }
        return false;
    }
}