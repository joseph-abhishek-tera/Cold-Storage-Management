package com.farmers.coldstorage.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.farmers.coldstorage.PreferenceManager;
import com.farmers.coldstorage.R;
import com.farmers.coldstorage.authentication.StartActivity;
import com.farmers.coldstorage.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {


    ActivityDashboardBinding binding;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        binding.cardAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle("Choose type;");
                builder.setCancelable(false);
                String[] items = {"Chilli","Other Crops"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                startActivity(new Intent(DashboardActivity.this,AddStorageActivity.class)
                                        .putExtra("type","Chilli"));
                                break;

                            case 1:
                                startActivity(new Intent(DashboardActivity.this,AddStorageActivity.class)
                                        .putExtra("type","Other Crops"));
                                break;

                        }
                    }
                });

                builder.setPositiveButton("Close",null);
                builder.create().show();
            }
        });


        binding.cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,UpdateStorageActivity.class));
            }
        });


        binding.cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.clear();
                Intent intent = new Intent(DashboardActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","Farmer");
                startActivity(intent);
                finish();
            }
        });

    }
}