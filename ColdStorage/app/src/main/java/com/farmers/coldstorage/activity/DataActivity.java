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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.farmers.coldstorage.PreferenceManager;
import com.farmers.coldstorage.adapter.StorageAdapter;
import com.farmers.coldstorage.databinding.ActivityDataBinding;
import com.farmers.coldstorage.model.StorageModel;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {


    ActivityDataBinding binding;

    DatabaseReference reference;
    private final ArrayList<StorageModel> list = new ArrayList<>();
    private StorageAdapter adapter;

    String required_type = "";
    String q_type;
    int required_space = 0;

    PreferenceManager preferenceManager;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Storages");
        preferenceManager = new PreferenceManager(this);

        location = preferenceManager.getString("location");
        if (location !=null){
            binding.txtLocation.setText("Location: "+location);
        }else {
            binding.txtLocation.setText("Location: N/A");
        }

        required_type = getIntent().getStringExtra("type");
        required_space = getIntent().getIntExtra("space",0);
        q_type = getIntent().getStringExtra("q_type");
        if (q_type.equals("KG")){
            q_type = "TON";
        }

        binding.txtRequired.setText("Required space: "+required_space);


        Toast.makeText(this, q_type, Toast.LENGTH_SHORT).show();

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

        Query query = reference.orderByChild("remaining").startAt(required_space);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        StorageModel model = dataSnapshot.getValue(StorageModel.class);
                        assert model != null;
                        if (model.getLocation().equalsIgnoreCase(location)){
                            if (model.getType().toLowerCase().contains(required_type.toLowerCase())){
                                list.add(model);

                            }
                        }
                    }

                    adapter = new StorageAdapter(DataActivity.this,list,"farmer");
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (list.size() == 0){
                        binding.NoDataLyt.setVisibility(View.VISIBLE);
                    }else {
                        binding.NoDataLyt.setVisibility(View.GONE);

                    }

                }else {
                    binding.NoDataLyt.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.NoDataLyt.setVisibility(View.VISIBLE);
                Toast.makeText(DataActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}