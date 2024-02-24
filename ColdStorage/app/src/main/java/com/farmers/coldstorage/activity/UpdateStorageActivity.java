package com.farmers.coldstorage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.farmers.coldstorage.R;
import com.farmers.coldstorage.adapter.StorageAdapter;
import com.farmers.coldstorage.databinding.ActivityUpdateStorageBinding;
import com.farmers.coldstorage.model.StorageModel;

import java.util.ArrayList;

public class UpdateStorageActivity extends AppCompatActivity {


    ActivityUpdateStorageBinding binding;

    DatabaseReference reference;

    private ArrayList<StorageModel> list = new ArrayList<>();
    private StorageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateStorageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Storages");


        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        getData();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getData() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        StorageModel model = dataSnapshot.getValue(StorageModel.class);
                        list.add(model);
                    }

                    adapter = new StorageAdapter(UpdateStorageActivity.this,list,"manager");
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else {
                    binding.NoDataLyt.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.NoDataLyt.setVisibility(View.VISIBLE);
                Toast.makeText(UpdateStorageActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}