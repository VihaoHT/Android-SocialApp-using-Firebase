package com.example.socialapp.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialapp.R;
import com.example.socialapp.databinding.FragmentAddBinding;
import com.example.socialapp.models.PostModel;
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

import java.util.Date;


public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    ActivityResultLauncher<String> mTakePhoto;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    public AddFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Post Uploading");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        //hiển thị avatar , name ,profession
        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.useravatar)
                                .into(binding.profileImage);
                        binding.name.setText(user.getName());
                        binding.profession.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.postDesciption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String desciption = binding.postDesciption.getText().toString();
                //nếu description không trống thì nút sẽ đổi màu
                if (!desciption.isEmpty()) {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn_bg));
                    binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postBtn.setEnabled(true);
                } else {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_active_btn));
                    binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.gray));
                    binding.postBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //lấy ảnh
        mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //nếu không chọn ảnh gì thì return
                        if (result == null) {
                            progressDialog.dismiss();
                            return;
                        }
                        binding.postImage.setImageURI(result);
                        binding.postImage.setVisibility(View.VISIBLE);

                        binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn_bg));
                        binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                        binding.postBtn.setEnabled(true);
                        //lưu  ảnh lên  Storage firebase
                        binding.postBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progressDialog.show();
                                final StorageReference reference = storage.getReference().child("posts")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child(new Date().getTime() + "");
                                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                PostModel postModel = new PostModel();
                                                postModel.setPostImage(uri.toString());
                                                postModel.setPostedBy(FirebaseAuth.getInstance().getUid());
                                                postModel.setPostDescription(binding.postDesciption.getText().toString());
                                                postModel.setPostedAt(new Date().getTime());

                                                database.getReference().child("posts")
                                                        .push()
                                                        .setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                progressDialog.dismiss();
                                                                FancyToast.makeText(getContext(), "Posted Succesful!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                }
        );
        binding.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTakePhoto.launch("image/*");
            }
        });


        return binding.getRoot();
    }
}



