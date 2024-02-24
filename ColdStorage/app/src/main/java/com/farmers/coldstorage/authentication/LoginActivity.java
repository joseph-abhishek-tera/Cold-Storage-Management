package com.farmers.coldstorage.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.farmers.coldstorage.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.farmers.coldstorage.PreferenceManager;
import com.farmers.coldstorage.activity.DashboardActivity;
import com.farmers.coldstorage.activity.SelectLocationActivity;
import com.farmers.coldstorage.databinding.ActivityLoginBinding;
import com.farmers.coldstorage.databinding.CreateAccountDialogueBinding;
import com.farmers.coldstorage.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    String from;

    FirebaseAuth auth;
    DatabaseReference reference;

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        from = getIntent().getStringExtra("from");
        preferenceManager = new PreferenceManager(this);

        if (from !=null){
            binding.t1.setText("Login as "+from);

        }

        auth = FirebaseAuth.getInstance();
        if (from.equalsIgnoreCase("Farmer")){
            reference = FirebaseDatabase.getInstance().getReference().child("Farmers");

        }else {
            reference = FirebaseDatabase.getInstance().getReference().child("Managers");

        }

        binding.textCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                CreateAccountDialogueBinding dialogueBinding = CreateAccountDialogueBinding.inflate(getLayoutInflater());
                dialog.setCancelable(false);
                dialog.setContentView(dialogueBinding.getRoot());

                dialogueBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String username = dialogueBinding.inputUsername.getText().toString();
                        String phone = dialogueBinding.inputPhone.getText().toString();
                        String email = dialogueBinding.inputEmail.getText().toString();
                        String password = dialogueBinding.inputPassword.getText().toString();

                        if (username.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()){
                            Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                        }else {

                            createNewAccount(username,phone,email,password,dialog);
                        }

                    }
                });

                dialogueBinding.btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();




            }
        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.inputEmail.getText().toString();
                String password = binding.inputPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter required fields!", Toast.LENGTH_SHORT).show();
                }else {
                    login(email,password);
                }
            }
        });




    }

    private void login(String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        if (from.equalsIgnoreCase("Farmer")){
            Query query = FirebaseDatabase.getInstance().getReference().child("Farmers").orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){

                        signIn(email,password,progressDialog);

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "This email is not an farmer!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Query query = FirebaseDatabase.getInstance().getReference().child("Managers").orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){

                        signIn(email,password,progressDialog);

                    }else {
                        progressDialog.dismiss();

                        Toast.makeText(LoginActivity.this, "This email is not an manager!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }

    private void signIn(String email,String password,ProgressDialog progressDialog){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = null;
                    if (from.equalsIgnoreCase("Farmer")){
                        intent = new Intent(LoginActivity.this, MainActivity.class);

                    }else {
                        intent = new Intent(LoginActivity.this, DashboardActivity.class);

                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    preferenceManager.putBoolean("login",true);
                    preferenceManager.putString("type",from);
                    finish();

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewAccount(String username, String phone, String email, String password, BottomSheetDialog dialog) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                FirebaseUser user = auth.getCurrentUser();

                UserModel model = new UserModel();
                model.setUsername(username);
                model.setEmail(email);
                model.setPhone(phone);
                model.setUid(user.getUid());

                reference.child(user.getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        progressDialog.dismiss();
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, from+" account created!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        dialog.dismiss();

                        Toast.makeText(LoginActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }else {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error: "+task.getException()
                        .getMessage(), Toast.LENGTH_SHORT).show();
            }

        });









    }
}