package com.example.socialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.socialapp.databinding.ActivityChangeInformationBinding;
import com.example.socialapp.fragment.HomeFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;

public class ChangeInformationActivity extends AppCompatActivity {
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        Button changeBtn = findViewById(R.id.changeBtn);
        TextInputEditText newName = findViewById(R.id.newName);
        TextInputEditText newProfession = findViewById(R.id.newProfession);


        dialog = new ProgressDialog(ChangeInformationActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        changeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                HashMap<String,Object> userMap = new HashMap<>();
                userMap.put("name",newName.getText().toString());
                userMap.put("profession",newProfession.getText().toString());
                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userMap);
                FancyToast.makeText(ChangeInformationActivity.this,"Profile Update Successful!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                Intent intent = new Intent(ChangeInformationActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });
    }
}