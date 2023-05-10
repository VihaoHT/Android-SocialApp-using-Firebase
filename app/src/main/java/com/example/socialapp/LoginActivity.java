package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.socialapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity {
    //binding được dùng để thay thế cho ánh xạ thông thường
    ActivityLoginBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = auth.getCurrentUser();

        //hiện dialog
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Logging in...");

        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.emailET.getText().toString(),
                        password = binding.passwordET.getText().toString();
                //kiểm tra có nhập thiếu hay không
                if (TextUtils.isEmpty(email)) {
                    binding.emailET.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    binding.passwordET.setError("password Required");
                    return;
                }
                dialog.show();

                // Đăng nhập tài khoản bằng email , password
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            //user not exist
                        }
                        //nếu tài khoản đã được xác minh thì có thê đăng nhập
                        else if (user.isEmailVerified()) {
                            FancyToast.makeText(LoginActivity.this, "Login Successful!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        //tài khoản chưa được xác minh thì sẽ  được hệ thống gửi mail để xác minh
                        else {
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FancyToast.makeText(LoginActivity.this, "Check your email for verify !", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FancyToast.makeText(LoginActivity.this, e.getMessage() + "", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(LoginActivity.this, e.getMessage() + "", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }
                });
            }
        });
        binding.goToSinUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //nếu tài khoản đã đăng nhập và đã được xác minh thì sẽ tự  động chuyển sang  màn hình chính. Không cần đăng nhập lại
        if (currentUser != null  && currentUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
