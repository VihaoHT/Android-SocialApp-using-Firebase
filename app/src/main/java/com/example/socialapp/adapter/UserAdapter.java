package com.example.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.databinding.UserSampleBinding;
import com.example.socialapp.models.FollowModel;
import com.example.socialapp.models.Notification;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder>{

    Context context;
    ArrayList<User> list;
    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder,final int position) {
        //list user hiển thị trong SearchFramgent
        User user = list.get(position);
        Picasso.get()
                .load(user.getProfile())
                .placeholder(R.drawable.useravatar)
                .into(holder.binding.profileImage);
        holder.binding.name.setText(user.getName());
        holder.binding.profession.setText(user.getProfession());


        //Nút follow
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FollowModel followModel = new FollowModel();
                        followModel.setFollowedBy(FirebaseAuth.getInstance().getUid());
                        followModel.setFollowedAt(new Date().getTime());
                        //nếu đã follow sau khi nhấn lại thì unfollow đồng thời count follow giảm đi 1
                        if(snapshot.exists())
                        {
                            holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(user.getUserID())
                                                            .child("followersCount")
                                                            .setValue(user.getFollowersCount() -1 ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void avoid) {
                                                                    holder.binding.followBtn.setEnabled(true);
                                                                    FancyToast.makeText(context,"You've unfollowed " + user.getName(),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
                            holder.binding.followBtn.setText("Following");
                            holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.gray));



                        }
                        else
                        {
                            //nếu  chưa follow thì sau khi nhấn nút follow thì count follow tăng lên 1
                            //đồng thời text và background cũng sẽ thay đổi
                            holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(followModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(user.getUserID())
                                                            .child("followersCount")
                                                            .setValue(user.getFollowersCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void avoid) {
                                                                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
                                                                    holder.binding.followBtn.setText("Following");
                                                                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.gray));
                                                                    holder.binding.followBtn.setEnabled(false);
                                                                    FancyToast.makeText(context,"You've followed " + user.getName(),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setType("follow");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(user.getUserID())
                                                                            .push()
                                                                            .setValue(notification);

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
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
        UserSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserSampleBinding.bind(itemView);
        }
    }
}