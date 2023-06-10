package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.socialapp.databinding.ActivityChangeEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ChangeEmailActivity extends AppCompatActivity {
    ActivityChangeEmailBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(ChangeEmailActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        binding.changeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String Newemail = binding.emailnew.getText().toString();
                if(TextUtils.isEmpty(Newemail)) {
                    binding.emailnew.setError("Please Write Your New Email ");
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(Newemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FancyToast.makeText(ChangeEmailActivity.this,"Change Email Succesful!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                            auth.signOut();
                            Intent intent = new Intent(ChangeEmailActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(ChangeEmailActivity.this,e.getMessage()+"",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                });
                dialog.dismiss();
            }
        });
    }
}