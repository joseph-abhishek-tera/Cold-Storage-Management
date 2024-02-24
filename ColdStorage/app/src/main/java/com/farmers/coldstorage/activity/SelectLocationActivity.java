package com.farmers.coldstorage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.farmers.coldstorage.PreferenceManager;
import com.farmers.coldstorage.R;
import com.farmers.coldstorage.databinding.ActivitySelectLocationBinding;

public class SelectLocationActivity extends AppCompatActivity {

    ActivitySelectLocationBinding binding;

    PreferenceManager preferenceManager;

    String type,qType;
    int space;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        type = getIntent().getStringExtra("type");
        space = getIntent().getIntExtra("space",0);
        qType = getIntent().getStringExtra("q_type");

//        Toast.makeText(this, qType, Toast.LENGTH_SHORT).show();


        binding.itemGuntur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLocationActivity.this, DataActivity.class)
                        .putExtra("type",type)
                        .putExtra("space",space)
                        .putExtra("q_type",qType));
                preferenceManager.putString("location","Guntur");
                finish();
            }
        });


        binding.itemVizag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLocationActivity.this, DataActivity.class)
                        .putExtra("type",type)
                        .putExtra("space",space)
                        .putExtra("q_type",qType));
                preferenceManager.putString("location","Guntur");

                finish();
            }
        });


        binding.itemNandigama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLocationActivity.this, DataActivity.class)
                        .putExtra("type",type)
                        .putExtra("space",space)
                        .putExtra("q_type",qType));
                preferenceManager.putString("location","Nandigama");

                finish();

            }
        });



        binding.itemVijayawada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLocationActivity.this, DataActivity.class)
                        .putExtra("location","Vijayawada"));
                preferenceManager.putString("location","Vijayawada");

                finish();

            }
        });


    }
}