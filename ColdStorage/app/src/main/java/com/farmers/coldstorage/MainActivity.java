package com.farmers.coldstorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.farmers.coldstorage.authentication.StartActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.farmers.coldstorage.activity.SelectLocationActivity;
import com.farmers.coldstorage.adapter.FarmerAdapter;
import com.farmers.coldstorage.authentication.LoginActivity;
import com.farmers.coldstorage.databinding.ActivityMainBinding;
import com.farmers.coldstorage.databinding.BottomDialogueBinding;
import com.farmers.coldstorage.databinding.CreateAccountDialogueBinding;
import com.farmers.coldstorage.model.FarmerModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DatabaseReference reference;

    private ArrayList<FarmerModel> list = new ArrayList<>();
    private FarmerAdapter adapter;

    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference();

        preferenceManager = new PreferenceManager(this);




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

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                BottomDialogueBinding dialogueBinding = BottomDialogueBinding.inflate(getLayoutInflater());
                dialog.setCancelable(true);
                dialog.setContentView(dialogueBinding.getRoot());



                dialogueBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String type = dialogueBinding.inputType.getText().toString();
                        String space = dialogueBinding.inputRequired.getText().toString();
                        if (type.isEmpty() || space.isEmpty()){
                            Toast.makeText(MainActivity.this, "Required all fields!", Toast.LENGTH_SHORT).show();
                        }else if (Integer.parseInt(space) == 0) {
                            Toast.makeText(MainActivity.this, "Required space must be positive", Toast.LENGTH_SHORT).show();
                        }else {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, SelectLocationActivity.class);
                            intent.putExtra("type",type);
                            intent.putExtra("space",Integer.parseInt(space));
                            intent.putExtra("q_type",dialogueBinding.selectQType.getText().toString());
                            startActivity(intent);
                        }
                    }
                });


                dialogueBinding.selectQType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Select quantity type:");
                        builder.setCancelable(true);
                        String[] items = {"KG","BAG"};
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                switch (i){
                                    case 0:
                                        dialogueBinding.selectQType.setText("KG");
                                        break;

                                    case 1:
                                        dialogueBinding.selectQType.setText("BAG");
                                        break;

                                }
                            }
                        });

                        builder.setPositiveButton("Close",null);
                        builder.create().show();
                    }
                });

                dialog.show();











            }
        });

        binding.imageLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager.clear();
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","Farmer");
                startActivity(intent);
                finish();
            }
        });


    }

    private void getData() {

        reference.child("Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        FarmerModel model = dataSnapshot.getValue(FarmerModel.class);
                        assert model != null;
                        if (model.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            list.add(model);

                        }
                    }

                    adapter = new FarmerAdapter(MainActivity.this,list);
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (list.size() == 0){
                        binding.NoDataLyt.setVisibility(View.VISIBLE);

                    }

                }else {
                    binding.NoDataLyt.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.NoDataLyt.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}