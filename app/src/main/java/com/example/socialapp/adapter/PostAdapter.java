package com.example.socialapp.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.CommentActivity;
import com.example.socialapp.R;
import com.example.socialapp.databinding.DashboardRvBinding;
import com.example.socialapp.models.Notification;
import com.example.socialapp.models.PostModel;
import com.example.socialapp.models.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{
    ArrayList<PostModel> list;
    Context context;
    ProgressDialog progressDialog;
    private String m_Text = "";
    public PostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv,parent,false);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        return new viewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel model = list.get(position);

        //hiên thời gian người đăng bài đã đăng bài được bao lâu
        String time = TimeAgo.using(model.getPostedAt());
        holder.binding.time.setText(time);

        //hiện ảnh , số like , comment của bài
        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.postImage);
        holder.binding.like.setText(model.getPostLike()+"");
        holder.binding.comment.setText(model.getCommentCount()+"");

        //nếu description trống thì sẽ không hiện lên nút post
        String description = model.getPostDescription();
        if(description.equals("")){
            holder.binding.postDescription.setVisibility(View.GONE);
        }else{
            holder.binding.postDescription.setText(model.getPostDescription());
            holder.binding.postDescription.setVisibility(View.VISIBLE);
        }


        //hiện avatar,tên, profesion của người đăng bài
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile())
                                .placeholder(R.drawable.useravatar)
                                .into(holder.binding.profileImage);
                        holder.binding.userName.setText(user.getName());
                        holder.binding.profession.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //post like
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //nếu data đã tồn tại thì sau khi nhấn lại nút like sẽ hủy bỏ và giảm đi 1
                        if(snapshot.exists()){
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(model.getPostId())
                                                            .child("postLike")
                                                            .setValue(model.getPostLike() - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart, 0, 0, 0);

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                            //đã like
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                        }else{
                            //nếu chưa like thì sau khi nhấn nút like sẽ + lên 1
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(model.getPostId())
                                                            .child("postLike")
                                                            .setValue(model.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostId(model.getPostId());
                                                                    notification.setPostedBy(model.getPostedBy());
                                                                    notification.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(model.getPostedBy())
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

        //truyền dữ liệu qua CommentActivity
        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
                intent.putExtra("postLike",model.getPostLike());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


        // nếu bạn không phải là người post bài thì sẽ không thấy dấu 3 chấm (menu) để xóa hoặc sửa bài
        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                if (userId.equals(model.getPostedBy())) {
                    holder.binding.menu.setVisibility(View.VISIBLE);
                }else{
                    holder.binding.menu.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //show menu to Delete and Edit Post
        holder.binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.edit) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Change description");
                            final EditText input = new EditText(context);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);
                            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId());
                                    HashMap<String,Object> postMap = new HashMap<>();
                                    postMap.put("postDescription",m_Text = input.getText().toString());
                                    ref.updateChildren(postMap);
                                    FancyToast.makeText(context,"Edited",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.show();
                        }
                        if (menuItem.getItemId() == R.id.delete){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Are you Sure you wanna delete this post ? ");
                            builder.setMessage("You can't undo when you delete");
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts").child(model.getPostId()).removeValue();
                                    FancyToast.makeText(context,"Deleted",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();


                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });




    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        DashboardRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvBinding.bind(itemView);

        }
    }

}
