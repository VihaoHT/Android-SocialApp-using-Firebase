package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ChangePassActivity extends AppCompatActivity {
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        TextInputEditText oldpass = findViewById(R.id.oldPass);
        TextInputEditText newpass = findViewById(R.id.newPass);
        TextInputEditText repass = findViewById(R.id.rePass);
        Button changeBtn = findViewById(R.id.changeBtn);

        dialog = new ProgressDialog(ChangePassActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldpassStr = oldpass.getText().toString();
                String newpassStr = newpass.getText().toString();
                String repassStr = repass.getText().toString();

                if(TextUtils.isEmpty(oldpassStr)){
                    oldpass.setError("Please Enter your old password!");
                }
                else if(TextUtils.isEmpty(newpassStr)){
                    newpass.setError("Please Enter your new password!");
                }
                else if(TextUtils.isEmpty(repassStr)){
                    repass.setError("Please Enter re password!");
                }
                else if(newpassStr.length()<6){
                    newpass.setError("Password at least 6 characters! ");
                }
                else if(repassStr.length()<6){
                    repass.setError("Password at least 6 characters!");
                }
                else if(newpassStr.compareTo(repassStr)!=0){
                    FancyToast.makeText(ChangePassActivity.this,"New pass and Re pass should be same",FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
                }
                else{
                    updatePassword(oldpassStr,newpassStr);
                }
            }
        });

    }
    private void updatePassword(String oldpassStr, String newpassStr) {
        dialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldpassStr);

        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                user.updatePassword(newpassStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        FancyToast.makeText(ChangePassActivity.this,"Password update successful!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        //nếu k có dòng 89 90 thì sẽ bị quay lại màn hình home vì vẫn lưu currentuser nên phải  cho signout
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(ChangePassActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        FancyToast.makeText(ChangePassActivity.this,e.getMessage()+"",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                FancyToast.makeText(ChangePassActivity.this,e.getMessage()+"",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
        });
    }
}