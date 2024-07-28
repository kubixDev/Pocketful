package com.kubixdev.pocketful.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kubixdev.pocketful.R;
import com.kubixdev.pocketful.fragments.AssetFragment;
import com.kubixdev.pocketful.fragments.GoalFragment;
import com.kubixdev.pocketful.fragments.HomeFragment;
import com.kubixdev.pocketful.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    FirebaseUser currentUser;

    HomeFragment homeFragment = new HomeFragment();
    AssetFragment assetFragment = new AssetFragment();
    GoalFragment goalFragment = new GoalFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // making sure the user is logged in to use the app
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // handles bottom navigation between fragments
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, homeFragment)
                        .commit();
                return true;

            case R.id.assets:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, assetFragment)
                        .commit();
                return true;

            case R.id.goals:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, goalFragment)
                        .commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, profileFragment)
                        .commit();
                return true;
        }
        return false;
    }
}