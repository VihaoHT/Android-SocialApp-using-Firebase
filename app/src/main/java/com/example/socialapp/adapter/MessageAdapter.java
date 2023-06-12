package com.example.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.databinding.ChatItemBinding;
import com.example.socialapp.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    List<Message> messageList;


    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSentBy().equals(Message.SENT_BY_ME)){
            holder.binding.leftChatView.setVisibility(View.GONE);
            holder.binding.rightChatView.setVisibility(View.VISIBLE);
            holder.binding.rightChatTextView.setText(message.getMessage());
        }else{
            holder.binding.rightChatView.setVisibility(View.GONE);
            holder.binding.leftChatView.setVisibility(View.VISIBLE);
            holder.binding.leftChatTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ChatItemBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemBinding.bind(itemView);
        }
    }
}
