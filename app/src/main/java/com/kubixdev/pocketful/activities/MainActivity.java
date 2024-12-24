package com.kubixdev.pocketful.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kubixdev.pocketful.R;
import com.kubixdev.pocketful.fragments.AssetFragment;
import com.kubixdev.pocketful.fragments.GoalFragment;
import com.kubixdev.pocketful.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    FirebaseUser currentUser;

    HomeFragment homeFragment = new HomeFragment();
    AssetFragment assetFragment = new AssetFragment();
    GoalFragment goalFragment = new GoalFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
        }
        return false;
    }
}