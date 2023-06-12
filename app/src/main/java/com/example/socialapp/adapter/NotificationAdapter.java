package com.example.socialapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.CommentActivity;
import com.example.socialapp.R;
import com.example.socialapp.databinding.NotificationRvDesignBinding;
import com.example.socialapp.models.Notification;
import com.example.socialapp.models.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>{

    ArrayList<Notification> list;
    Context context;

    public NotificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_design,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notification notification = list.get(position);

        String type  = notification.getType();
        //hiên thời gian người đăng bài đã đăng bài được bao lâu
        String time = TimeAgo.using(notification.getNotificationAt());
        holder.binding.time.setText(time);
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.useravatar)
                                .into(holder.binding.notificationProfile);
                        holder.binding.time.setText(time);
                        if(type.equals("like")){
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName() + "</b>" + " liked your post"));
                        }else if (type.equals("comment")){
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName() + "</b>" + " commented your post"));
                        }
                        //type.equals("follow")
                        else{
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName() + "</b>" + " start following you"));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!type.equals("follow")){

                    FirebaseDatabase.getInstance().getReference()
                            .child("notification")
                            .child(notification.getPostedBy())
                            .child(notification.getNotificationId())
                            .child("checkOpen")
                            .setValue(true);

                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId",notification.getPostId());
                    intent.putExtra("postedBy",notification.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{

                }

            }
        });
        Boolean checkOpen  = notification.isCheckOpen();
        if(checkOpen == true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else{

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        NotificationRvDesignBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationRvDesignBinding.bind(itemView);
        }
    }
}
