package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pthien.project_ciy.Adapter.Adapter_user;
import com.pthien.project_ciy.Model.Model_user;


import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    RecyclerView rcv_user;
    Adapter_user adapter_user;
    List<Model_user> userList;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_search);

        ImageButton backs = findViewById(R.id.backs);
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, DashboardActivity.class));
            }
        });
        SearchView sv = (SearchView) findViewById(R.id.sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())){
                    searchUsers(query);
                }else{
                    getAlluser();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                if (!TextUtils.isEmpty(query.trim())){
                    searchUsers(query);
                }else{
                    getAlluser();
                }
                return false;
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        rcv_user = findViewById(R.id.rcv_userss);
        rcv_user.setHasFixedSize(true);
        rcv_user.setLayoutManager(new LinearLayoutManager(this));


        userList = new ArrayList<>();
        adapter_user = new Adapter_user(this, userList);
        rcv_user.setAdapter(adapter_user);
        checkUserStatus();
        getAlluser();

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

                    adapter_user.notifyDataSetChanged();
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
                    if(!model_user.getUid().equals(fUser.getUid())) {
                        userList.add(model_user);
                    }
                    adapter_user.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
        }else{
            startActivity(new Intent (SearchActivity.this, MainActivity.class));
            finish();
        }
    }
}


