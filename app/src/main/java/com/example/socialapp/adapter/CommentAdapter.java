package com.example.socialapp.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.databinding.CommentRvBinding;
import com.example.socialapp.models.CommentModel;
import com.example.socialapp.models.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{
    Context context;
    ArrayList<CommentModel> list;
    private String m_Text = "";
    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_rv,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel commentModel = list.get(position);

        //hiên thời gian người comment đã comment được bao lâu
        String time = TimeAgo.using(commentModel.getCommentedAt());
        holder.binding.time.setText(time);

        //hiện hình ảnh người comment và tên
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(commentModel.getCommentedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.useravatar)
                                .into(holder.binding.profileImage);
                        holder.binding.comment.setText(Html.fromHtml("<b>" + user.getName()+ "</b>" + ": " + commentModel.getCommentBody()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //nếu không phải là người đăng comment thì không thấy được dấu 3 chấm (menu)
        FirebaseDatabase.getInstance().getReference().child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                if (userId.equals(commentModel.getCommentedBy())) {
                    holder.binding.menu.setVisibility(View.VISIBLE);
                }else{
                    holder.binding.menu.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        //show menu to Delete and Edit Comment
//        holder.binding.menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popupMenu = new PopupMenu(context,view);
//                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        if(menuItem.getItemId() == R.id.edit) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setTitle("Edit comment");
//                            final EditText input = new EditText(context);
//                            input.setInputType(InputType.TYPE_CLASS_TEXT);
//                            builder.setView(input);
//                            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                                @Override
//
//                                public void onClick(DialogInterface dialog, int which) {
//                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("comments")
//                                            .child(commentModel.getCommentedBy());
//                                    HashMap<String,Object> commentMap = new HashMap<>();
//                                    commentMap.put("commentBody",m_Text = input.getText().toString());
//                                    ref.updateChildren(commentMap);
//                                    Toast.makeText(context, "Edit comment succesful", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            builder.show();
//                        }
//                        if (menuItem.getItemId() == R.id.delete){
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setTitle("Are you Sure you wanna delete this post ? ");
//                            builder.setMessage("You can't undo when you delete");
//                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    FirebaseDatabase.getInstance().getReference()
//                                            .child("comments").child(commentModel.getCommentedBy()).removeValue();
//                                    Toast.makeText(context, "Delete post succesful", Toast.LENGTH_SHORT).show();
//
//
//                                }
//                            });
//                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            builder.show();
//                        }
//                        return true;
//                    }
//                });
//                popupMenu.show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CommentRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentRvBinding.bind(itemView);
        }
    }
}
