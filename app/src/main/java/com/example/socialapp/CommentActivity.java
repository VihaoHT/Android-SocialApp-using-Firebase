package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.socialapp.adapter.CommentAdapter;
import com.example.socialapp.databinding.ActivityCommentBinding;
import com.example.socialapp.models.CommentModel;
import com.example.socialapp.models.PostModel;
import com.example.socialapp.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    Intent intent;
    String postId, postedBy;
    Integer postLike;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<CommentModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        //lấy dữ liệu nhận về từ PostAdapter dòng 184
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");
        postLike = intent.getIntExtra("postLike", 0);
        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostModel postModel = snapshot.getValue(PostModel.class);
                        Picasso.get()
                                .load(postModel.getPostImage())
                                .placeholder(R.drawable.placeholder)
                                .into(binding.postImage);
                        binding.description.setText(postModel.getPostDescription());
                        binding.like.setText(postModel.getPostLike() + "");
                        binding.comment.setText(postModel.getCommentCount() + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //hiển thị avatar và tên của người đăng bài
        database.getReference()
                .child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.useravatar)
                                .into(binding.profileImage);
                        binding.name.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //nút  đăng comment
        //nếu có người comment thì số count comment sẽ tăng lên 1
        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentModel commentModel = new CommentModel();
                commentModel.setCommentBody(binding.commentET.getText().toString());
                commentModel.setCommentedAt(new Date().getTime());
                commentModel.setCommentedBy(auth.getUid());

                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if (snapshot.exists()) {
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentCount")
                                                        .setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                binding.commentET.setText("");
                                                                FancyToast.makeText(CommentActivity.this, "Commented", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });
            }
        });

        CommentAdapter commentAdapter = new CommentAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(layoutManager);
        binding.commentRv.setAdapter(commentAdapter);

        //Hiển thị các dòng comment ra RecycleView
        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentModel commentModel = dataSnapshot.getValue(CommentModel.class);
                            list.add(commentModel);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //nếu  ngoài home bạn đã like thì vô phần  comment  cũng sẽ hiện
        FirebaseDatabase.getInstance().getReference().child("posts")
                .child(postId).child("likes").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
