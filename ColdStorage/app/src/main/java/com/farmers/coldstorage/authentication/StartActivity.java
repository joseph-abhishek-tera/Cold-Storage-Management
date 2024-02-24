package com.farmers.coldstorage.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.farmers.coldstorage.MainActivity;
import com.farmers.coldstorage.PreferenceManager;
import com.farmers.coldstorage.R;
import com.farmers.coldstorage.activity.DashboardActivity;
import com.farmers.coldstorage.activity.SelectLocationActivity;
import com.farmers.coldstorage.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {


    ActivityStartBinding binding;

    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferenceManager = new PreferenceManager(this);


        binding.itemFarmer.setOnClickListener(view -> {
            if (preferenceManager.getBoolean("login")){
                if (preferenceManager.getString("type").equals("Farmer")){
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();

                }else {
                    startActivity(new Intent(StartActivity.this, DashboardActivity.class));
                    finish();
                }
            }else {
                startActivity(new Intent(StartActivity.this, LoginActivity.class)
                        .putExtra("from", "Farmer"));
            }
        });
        binding.itemManager.setOnClickListener(view -> {
            if (preferenceManager.getBoolean("login")){
                if (preferenceManager.getString("type").equals("Farmer")){
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();

                }else {
                    startActivity(new Intent(StartActivity.this, DashboardActivity.class));
                    finish();
                }
            }else {
                startActivity(new Intent(StartActivity.this, LoginActivity.class)
                        .putExtra("from", "Manager"));
            }

        });

    }
}