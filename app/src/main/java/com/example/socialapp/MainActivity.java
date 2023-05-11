package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.socialapp.databinding.ActivityMainBinding;
import com.example.socialapp.fragment.AddFragment;
import com.example.socialapp.fragment.HomeFragment;
import com.example.socialapp.fragment.NotificationFragment;
import com.example.socialapp.fragment.ProfileFragment;
import com.example.socialapp.fragment.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {
    //binding được dùng để thay thế cho ánh xạ thông thường
    ActivityMainBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //hiện dialog
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        //set actionbar
        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE); //dòng này mình ẩn actionbar đi và chỉ để hiện cho ProfileFragment ở dòng 74
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();
        //bottom navigation
        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i){
                    case 0:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new HomeFragment());
                        break;
                    case 1:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new NotificationFragment());
                        break;
                    case 2:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new AddFragment());
                        break;
                    case 3:
                        dialog.show();
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new SearchFragment());
                        break;
                    case 4:
                        binding.toolbar.setVisibility(View.VISIBLE); //GONE là cho mất luôn , VISIBLE là hiện ra
                        transaction.replace(R.id.container, new ProfileFragment());
                        break;
                }

                transaction.commit();

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //log out
            case R.id.setting:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}