package com.example.socialapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialapp.R;
import com.example.socialapp.adapter.UserAdapter;
import com.example.socialapp.databinding.FragmentSearchBinding;
import com.example.socialapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    UserAdapter userAdapter;
    private String searchInputText;
    public SearchFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);

        UserAdapter userAdapter = new UserAdapter(getContext(),list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.userRV.setLayoutManager(layoutManager);
        binding.userRV.addItemDecoration(new DividerItemDecoration(binding.userRV.getContext(),DividerItemDecoration.VERTICAL));
        binding.userRV.setAdapter(userAdapter);


//        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchInputText = binding.searchInput.getText().toString();
//                //nếu trồng thì sẽ báo lỗi
//                if(searchInputText.isEmpty())
//                {
//                    binding.searchInput.setError("Pls write name who you wanna looking for");
//                    list.clear();
//                }
//                else
//                {
//                    database.getReference().child("Users").orderByChild("name").startAt(searchInputText)
//                            .endAt(searchInputText+"~").addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                            User user = snapshot.getValue(User.class);
////                            user.setUserID(snapshot.getKey());
////                            // nếu là currentUser thì sẽ ko hiện lên list search fragment
////                            if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
////                                list.add(user);
////
////                            }
////                            userAdapter.notifyDataSetChanged();
//
//                        }
//
//                        @Override
//                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            User user = snapshot.getValue(User.class);
//                            user.setUserID(snapshot.getKey());
//                            // nếu là currentUser thì sẽ ko hiện lên list search fragment
//                            if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
//                                list.add(user);
//
//                            }
//                            userAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                            User user = snapshot.getValue(User.class);
////                            user.setUserID(snapshot.getKey());
////                            // nếu là currentUser thì sẽ ko hiện lên list search fragment
////                            if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
////                                list.add(user);
////
////                            }
////
////                            userAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
////                    //orderbychild là để tìm kiếm name trong bảng users
////                    database.getReference().child("Users").orderByChild("name").startAt(searchInputText).endAt(searchInputText+"~").addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////
////                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
////                            {
////
////                                User user = dataSnapshot.getValue(User.class);
////                                user.setUserID(dataSnapshot.getKey());
////                                // nếu là currentUser thì sẽ ko hiện lên list search fragment
////                                if(!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
////                                    list.add(user);
////
////                                }
////
////                            }
////                            list.clear();
////                            userAdapter.notifyDataSetChanged();
////                        }
////
////                        @Override
////                        public void onCancelled(@NonNull DatabaseError error) {
////
////                        }
////                    });
//                }
//                database.getReference().child("Users").orderByChild("name").startAt(searchInputText)
//                        .endAt(searchInputText+"~").addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                                User user = snapshot.getValue(User.class);
////                                user.setUserID(snapshot.getKey());
////                                // nếu là currentUser thì sẽ ko hiện lên list search fragment
////                                if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
////                                    list.add(user);
////
////                                }
////                                userAdapter.notifyDataSetChanged();
//
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                                User user = snapshot.getValue(User.class);
//                                user.setUserID(snapshot.getKey());
//                                // nếu là currentUser thì sẽ ko hiện lên list search fragment
//                                if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
//                                    list.add(user);
//
//                                }
//                                userAdapter.notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////                                User user = snapshot.getValue(User.class);
////                                user.setUserID(snapshot.getKey());
////                                // nếu là currentUser thì sẽ ko hiện lên list search fragment
////                                if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
////                                    list.add(user);
////
////                                }
////                                list.clear();
////                                userAdapter.notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//            }
//        });

        database.getReference().child("Users").orderByChild("name").startAt(searchInputText)
                .endAt(searchInputText+"~").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        User user = snapshot.getValue(User.class);
                        user.setUserID(snapshot.getKey());
                        // nếu là currentUser thì sẽ ko hiện lên list search fragment
                        if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                            list.add(user);

                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {



                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        User user = snapshot.getValue(User.class);
//                        user.setUserID(snapshot.getKey());
//                        // nếu là currentUser thì sẽ ko hiện lên list search fragment
//                        if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
//                            list.add(user);
//
//                        }
//                        list.clear();
//                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return binding.getRoot();
    }


}