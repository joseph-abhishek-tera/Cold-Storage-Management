package com.farmers.coldstorage.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.farmers.coldstorage.MainActivity;
import com.farmers.coldstorage.databinding.ItemStorageBinding;
import com.farmers.coldstorage.databinding.UpdateDialogueBinding;
import com.farmers.coldstorage.model.StorageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StorageModel> list = new ArrayList<>();
    private String state;
    int one_ton = 1000;

    String location = "";


    public StorageAdapter(Context context, ArrayList<StorageModel> list, String state) {
        this.context = context;
        this.list = list;
        this.state = state;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStorageBinding binding = ItemStorageBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(list.get(position),state);

        String r_type = ((Activity)context).getIntent().getStringExtra("type");
        int r_space = ((Activity)context).getIntent().getIntExtra("space",0);

        if (state.equals("manager")){
            holder.binding.btnDelete.setOnClickListener(view -> {
                String id = list.get(position).getId();
                FirebaseDatabase.getInstance().getReference().child("Storages")
                        .child(id)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    notifyItemRemoved(position);
                                    list.remove(position);
                                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "Failed: !"+task.getException()
                                            .getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            });

            holder.binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    StorageModel model = list.get(position);
                    String type = model.getQuantityType();

                    BottomSheetDialog dialog = new BottomSheetDialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    UpdateDialogueBinding dialogueBinding = UpdateDialogueBinding.inflate(LayoutInflater.from(context));
                    dialog.setCancelable(true);
                    dialog.setContentView(dialogueBinding.getRoot());

                    if (type.equals("Chilli")){
                        dialogueBinding.qText.setText("BAGS");
                        dialogueBinding.oBagsCount.setText("BAGS");
                        dialogueBinding.rBagsCount.setText("BAGS");
                        dialogueBinding.inputPerTon.setHint("Per Bag Price");
                    }

                    String[] items = {list.get(position).getLocation(),"Other Locations","Guntur", "Nandigama", "Vijayawada"};
                    dialogueBinding.spinner.setSelection(0);

                    dialogueBinding.inputOccupied.setText(String.valueOf(model.getOccupied()));
                    dialogueBinding.inputRemaining.setText(String.valueOf(model.getRemaining()));
                    dialogueBinding.inputStorageName.setText(model.getName());
                    dialogueBinding.inputCapacity.setText(String.valueOf(model.getCapacity()));
                    dialogueBinding.inputType.setText(model.getType());
                    dialogueBinding.inputPerTon.setText(String.valueOf(model.getPrice_per_item()));



                    dialogueBinding.inputOccupied.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            int input_capacity = Integer.parseInt(dialogueBinding.inputCapacity.getText().toString());
                            int capacity;
                            if (type.equals("Chilli")){

                                capacity = Integer.parseInt(dialogueBinding.inputCapacity.getText().toString());
                            }else {
                                capacity = input_capacity * one_ton;
                            }
                            String occupied = charSequence.toString();
                            int valOccupied = Integer.parseInt(occupied);
                            if (occupied.isEmpty() || capacity == 0){
                                Toast.makeText(context, "Capacity must be a positive value", Toast.LENGTH_SHORT).show();
                            }else if (valOccupied > capacity){
                                Toast.makeText(context, "Occupied be space must be either low or equal to capacity value", Toast.LENGTH_SHORT).show();
                            }else if (location.isEmpty()){
                                Toast.makeText(context, "select location", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                int remaining = capacity - valOccupied;
                                dialogueBinding.inputRemaining.setText(String.valueOf(remaining));
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dialogueBinding.spinner.setAdapter(adapter);

                    dialogueBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            location = items[i];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                    //
                    dialogueBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String name = dialogueBinding.inputStorageName.getText().toString();
                            String capacity = dialogueBinding.inputCapacity.getText().toString();
                            String type = dialogueBinding.inputType.getText().toString();
                            String occupied = dialogueBinding.inputOccupied.getText().toString();
                            String remaining = dialogueBinding.inputRemaining.getText().toString();
                            String price = dialogueBinding.inputPerTon.getText().toString();


                            if (name.isEmpty() || capacity.isEmpty() || type.isEmpty() || occupied.isEmpty() ||
                                    remaining.isEmpty() || price.isEmpty()){
                                Toast.makeText(context, "Enter required fields!", Toast.LENGTH_SHORT).show();
                            }else if (Integer.parseInt(capacity) == 0){
                                Toast.makeText(context, "Capacity value must be positive!", Toast.LENGTH_SHORT).show();
                            }else {


                                double per_item_price = Double.parseDouble(price);
                                double total_price = Integer.parseInt(capacity) * per_item_price;


                                uploadStorage(name,capacity,type,occupied,remaining,per_item_price,total_price,position);

                            }








                        }
                    });






















                    dialog.show();











                }
            });

        }else {
            holder.binding.btnGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = list.get(position).getId();
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Processing.....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    double total_price =  r_space * list.get(position).getPrice_per_item();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("storage_id",id);
                    map.put("storage_name",list.get(position).getName());
                    map.put("space_required",r_space);
                    map.put("timestamp",System.currentTimeMillis());
                    map.put("required_type",r_type);
                    map.put("total_price",total_price);
                    map.put("room_id",new Random().nextInt(10000));
                    map.put("quantity_type",list.get(position).getQuantityType());
                    map.put("location",list.get(position).getLocation());
                    map.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Active");

                    String key = reference.push().getKey();
                    reference.child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //update storage
                            progressDialog.dismiss();
                            int newRemainingValue = list.get(position).getRemaining() - r_space;
                            int occupied = list.get(position).getOccupied() + r_space;
                            String id = list.get(position).getId();
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("remaining", newRemainingValue);
                            updateData.put("occupied",occupied);
                            FirebaseDatabase.getInstance().getReference().child("Storages")
                                    .child(id)
                                    .updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Intent intent = new Intent(context, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                                ((Activity)context).finish();
                                                Toast.makeText(context, "Storage activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
        }


    }

    private void uploadStorage(String name, String capacity, String type, String occupied, String remaining,
                               double per_item_price, double total_price, int position) {

       DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Storages");


        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("New Storage Adding..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String id = reference.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("type", type);
        map.put("capacity_tons", Integer.parseInt(capacity));
        map.put("remaining_kg", Integer.parseInt(remaining));
        map.put("occupied_kg", Integer.parseInt(occupied));
        map.put("price_per_item", per_item_price);
        map.put("publisher", userId);
        map.put("total_price", total_price);
        map.put("location", location);
        map.put("id", id);
        map.put("timestamp", System.currentTimeMillis());
        map.put("quantityType", list.get(position).getQuantityType());


        assert id != null;
        reference.child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(context, "Storage updated successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStorageBinding binding;
        public ViewHolder(@NonNull ItemStorageBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        @SuppressLint("SetTextI18n")
        void bind(StorageModel model, String state){
            binding.name.setText(model.getName());
            binding.type.setText(model.getType());
            if (model.getQuantityType().equals("Ton")){
                binding.capacity.setText(model.getCapacity()+" TON's");
                binding.priceInfo.setText("Price ( Per Ton )");

            }else {
                binding.capacity.setText(model.getCapacity()+" Bag's");
                binding.priceInfo.setText("Price ( Per Bag )");

            }
            binding.pricePerTon.setText("₹"+model.getPrice_per_item());

            binding.occupied.setText(model.getOccupied()+" "+model.getQuantityType() +"'s");

            binding.remaining.setText(model.getRemaining()+" "+model.getQuantityType() + "'s");
            binding.location.setText(model.getLocation());

            if (state.equals("manager")){
                binding.buttonsLyt.setVisibility(View.VISIBLE);
                binding.btnGet.setVisibility(View.GONE);


            }else {
                binding.buttonsLyt.setVisibility(View.GONE);
                binding.btnGet.setVisibility(View.VISIBLE);

            }



        }

    }

}
