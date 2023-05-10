package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.socialapp.databinding.ActivitySignUpBinding;
import com.example.socialapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignUpActivity extends AppCompatActivity {
    //binding được dùng để thay thế cho ánh xạ thông thường
    ActivitySignUpBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //hiện dialog
        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Signing Up...");

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = binding.emailET.getText().toString() ,
                        password = binding.passwordET.getText().toString(),
                        name = binding.nameET.getText().toString(),
                        profession = binding.professionET.getText().toString();
                //kiểm tra ko dc để trống
                if(TextUtils.isEmpty(name)){
                    binding.nameET.setError("Name Required");
                    return;
                }if(TextUtils.isEmpty(profession)){
                    binding.professionET.setError("Profession Required");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    binding.emailET.setError("Email Required");
                    return;
                }if(TextUtils.isEmpty(password)){
                    binding.passwordET.setError("password Required");
                    return;
                }
                dialog.show();
                //tạo tài khoản  bằng email và password
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                //nếu thành công thì sẽ lưu vào realtime database
                                if(task.isSuccessful()) {
                                    User user = new User(name, profession, email, password);
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    FancyToast.makeText(SignUpActivity.this,"Your Register have been succesful!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FancyToast.makeText(SignUpActivity.this,e.getMessage()+"",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                            }
                        });
            }
        });
        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}