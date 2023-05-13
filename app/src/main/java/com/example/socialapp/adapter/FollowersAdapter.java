package com.example.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.databinding.FriendRvSampleBinding;
import com.example.socialapp.models.FollowModel;
import com.example.socialapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder> {
    ArrayList<FollowModel> list;
    Context context;

    public FollowersAdapter(ArrayList<FollowModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_rv_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FollowModel followModel = list.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(followModel.getFollowedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.useravatar)
                                .into(holder.binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        FriendRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FriendRvSampleBinding.bind(itemView);
        }
    }
}
