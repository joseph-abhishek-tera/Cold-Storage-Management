package com.farmers.coldstorage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.farmers.coldstorage.databinding.ActivityAddStorageBinding;
import com.farmers.coldstorage.model.StorageModel;

public class AddStorageActivity extends AppCompatActivity {


    ActivityAddStorageBinding binding;

    DatabaseReference reference;

    int one_ton = 1000;
    String[] items = {"Select Location","Guntur", "Nandigama", "Vijayawada"};

    private String location = "";
    private String type;

    private String qType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStorageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reference = FirebaseDatabase.getInstance().getReference().child("Storages");

        type = getIntent().getStringExtra("type");
        assert type != null;
        if (type.equals("Chilli")){
//            binding.qText.setText("BAGS");
//            binding.oBagsCount.setText("BAGS");
//            binding.rBagsCount.setText("BAGS");
//            binding.inputPerTon.setHint("Per Bag Price");
            qType = "BAG";
        }else {
            qType = "TON";
        }


        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = binding.inputStorageName.getText().toString();
                String capacity = binding.inputCapacity.getText().toString();
                String type = binding.inputType.getText().toString();
                String occupied = binding.inputOccupied.getText().toString();
                String remaining = binding.inputRemaining.getText().toString();
                String price = binding.inputPerTon.getText().toString();


                if (name.isEmpty() || capacity.isEmpty() || type.isEmpty() || occupied.isEmpty() ||
                        remaining.isEmpty() || price.isEmpty()){
                    Toast.makeText(AddStorageActivity.this, "Enter required fields!", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(capacity) == 0){
                    Toast.makeText(AddStorageActivity.this, "Capacity value must be positive!", Toast.LENGTH_SHORT).show();
                }else {


//                    int ton = Integer.parseInt(capacity);
//                    int per_ton = Integer.parseInt(price);
//
//                    int total_price = ton * per_ton;
//
//                    double pricePerKg = (double) total_price / (ton * 1000);

                    double per_item_price = Double.parseDouble(price);
                    double total_price = Integer.parseInt(capacity) * per_item_price;


                    uploadStorage(name,capacity,type,occupied,remaining,per_item_price,total_price);

                }








            }
        });


        binding.inputOccupied.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int input_capacity = Integer.parseInt(binding.inputCapacity.getText().toString());
                int capacity;
                if (type.equals("Chilli")){

                    capacity = Integer.parseInt(binding.inputCapacity.getText().toString());
                }else {
                    capacity = input_capacity * one_ton;
                }
                String occupied = charSequence.toString();
                int valOccupied = Integer.parseInt(occupied);
                if (occupied.isEmpty() || capacity == 0){
                    Toast.makeText(AddStorageActivity.this, "Capacity must be a positive value", Toast.LENGTH_SHORT).show();
                }else if (valOccupied > capacity){
                    Toast.makeText(AddStorageActivity.this, "Occupied be space must be either low or equal to capacity value", Toast.LENGTH_SHORT).show();
                }else if (location.isEmpty()){
                    Toast.makeText(AddStorageActivity.this, "select location", Toast.LENGTH_SHORT).show();
                }
                else {
                    int remaining = capacity - valOccupied;
                    binding.inputRemaining.setText(String.valueOf(remaining));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        spinner();
    }

    private void spinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                location = items[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void uploadStorage(String name, String capacity, String type, String occupied, String remaining, double per_item_price, double total_price) {


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("New Storage Adding..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String id = reference.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        StorageModel model = new StorageModel();
        model.setName(name);
        model.setType(type);
        model.setCapacity(Integer.parseInt(capacity));
        model.setRemaining(Integer.parseInt(remaining));
        model.setOccupied(Integer.parseInt(occupied));
        model.setPrice_per_item(per_item_price);
        model.setPublisher(userId);
        model.setTotal_amount(Double.valueOf(total_price));
        model.setLocation(location);
        model.setId(id);
        model.setQuantityType(qType);
        model.setTimestamp(System.currentTimeMillis());


        assert id != null;
        reference.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(AddStorageActivity.this, "New storage added successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddStorageActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });










    }
}