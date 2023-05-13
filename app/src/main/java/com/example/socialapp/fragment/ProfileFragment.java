package com.example.socialapp.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialapp.R;
import com.example.socialapp.adapter.FollowersAdapter;
import com.example.socialapp.databinding.FragmentProfileBinding;
import com.example.socialapp.models.FollowModel;
import com.example.socialapp.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    RecyclerView recyclerView;

    //binding được dùng để thay thế cho ánh xạ thông thường
    FragmentProfileBinding binding;

    ArrayList<FollowModel> list;

    //ActivityResultLauncher dùng để pick hình ảnh từ thư viện
    ActivityResultLauncher<String> mTakePhoto;
    ActivityResultLauncher<String> mTakePhoto2;

    //thư viên của firebase
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog dialog;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);

        //Hiển thị thông tin cá nhân trong profile
        //Gọi Data từ bảng Users để hiển thị thông tin và hình ảnh
        //Picasso or Glide để load hình ảnh đều được
        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //nếu data có tồn tại
                        if(snapshot.exists())
                        {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getCoverPhoto())
                                    .placeholder(R.drawable.placeholder)
                                    .into(binding.coverPhoto);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.useravatar)
                                    .into(binding.profileImage);
                            binding.userName.setText(user.getName());
                            binding.profession.setText(user.getProfession());
                            binding.followers.setText(user.getFollowersCount()+ "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Hiển thị followers đang theo dõi
        list = new ArrayList<>();

        FollowersAdapter followersAdapter = new FollowersAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.friendRV.setLayoutManager(linearLayoutManager);
        binding.friendRV.setAdapter(followersAdapter);

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot :snapshot.getChildren())
                        {
                            FollowModel followModel = dataSnapshot.getValue(FollowModel.class);
                            list.add(followModel);
                        }
                        followersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Lấy ảnh cài đặt vào ảnh nền cá nhân
        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //nếu người dùng k chọn ảnh thì sẽ return
                        if (result == null){
                            dialog.dismiss();
                            return;
                        }

                        //pick ảnh lưu vào Storage
                         binding.coverPhoto.setImageURI(result);
                        final StorageReference reference = storage.getReference().child("cover_photo")
                                .child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                FancyToast.makeText(getContext(),"Cover Photo Saved!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                dialog.dismiss();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(auth.getUid()).child("coverPhoto").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                    }
                }
        );

        //Lấy ảnh cài đặt vào avatar cá nhân
        mTakePhoto2 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result == null){
                            dialog.dismiss();
                            return;
                        }
                        binding.profileImage.setImageURI(result);
                        final StorageReference reference = storage.getReference().child("profile_image")
                                .child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                FancyToast.makeText(getContext(),"Profile Image Saved!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                dialog.dismiss();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(auth.getUid()).child("profile").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                        binding.profileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                    }
                }
        );

        //Lấy ảnh nền
        binding.changeCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTakePhoto.launch("image/*");
                dialog.show();
            }
        });

        //lấy ảnh đại diện
        binding.changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTakePhoto2.launch("image/*");
                dialog.show();
            }
        });
        return binding.getRoot();
    }
}