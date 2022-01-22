package com.pthien.project_ciy.fragement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_user;
import com.pthien.project_ciy.MainActivity;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.R;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {
  RecyclerView rcv_user;

    Adapter_user adapter_user;
    List<Model_user> userList;
    FirebaseAuth firebaseAuth;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        firebaseAuth = FirebaseAuth.getInstance();


        rcv_user = view.findViewById(R.id.rcv_user);
        rcv_user.setHasFixedSize(true);
        rcv_user.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList = new ArrayList<>();

        getAlluser();
        return view;
    }
    private void searchUsers(String query) {

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Model_user model_user = ds.getValue(Model_user.class);
                    if(!model_user.getUid().equals(fUser.getUid())){
                        if (model_user.getName().toLowerCase().contains(query.toLowerCase()) ||
                        model_user.getEmail().toLowerCase().contains(query.toLowerCase())){
                            userList.add(model_user);
                        }
                    }
                    adapter_user = new Adapter_user(getActivity(), userList);
                    adapter_user.notifyDataSetChanged();
                    rcv_user.setAdapter(adapter_user);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAlluser() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Model_user model_user = ds.getValue(Model_user.class);
                   if(!model_user.getUid().equals(fUser.getUid())){
                       userList.add(model_user);
                   }
                   adapter_user = new Adapter_user(getActivity(), userList);
                   rcv_user.setAdapter(adapter_user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        inflater.inflate(R.menu.menu_main, menu);
//        MenuItem item2 = menu.findItem(R.id.action_search).setVisible(false);
//        MenuItem item = menu.findItem(R.id.action_search2);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (!TextUtils.isEmpty(query.trim())){
//                    searchUsers(query);
//                }else{
//                    getAlluser();
//                }
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String query) {
//                if (!TextUtils.isEmpty(query.trim())){
//                    searchUsers(query);
//                }else{
//                    getAlluser();
//                }
//                return false;
//            }
//        });
//        super.onCreateOptionsMenu(menu, inflater);
//    }
    public void reloadata(){
        Toast.makeText(getActivity(), "Reload Data", Toast.LENGTH_SHORT).show();
    }

}