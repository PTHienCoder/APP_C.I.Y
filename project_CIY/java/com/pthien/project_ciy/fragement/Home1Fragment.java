package com.pthien.project_ciy.fragement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_Post;
import com.pthien.project_ciy.Add_Quention_Activity;
import com.pthien.project_ciy.DashboardActivity;
import com.pthien.project_ciy.MainActivity;
import com.pthien.project_ciy.Model.Model_post;
import com.pthien.project_ciy.MyProfileActivity;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.SearchActivity;
import com.pthien.project_ciy.ShowUserChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class Home1Fragment extends Fragment {
    LinearLayoutManager layoutManager;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<Model_post> postList;
    Adapter_Post adapter_post;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public Home1Fragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        recyclerView = view.findViewById(R.id.rcv_post);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        ImageView imageViewava = view.findViewById(R.id.avatar_home);
        imageViewava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String image = ""+ ds.child("image").getValue();
                    try {
                        Picasso.get().load(image).into(imageViewava);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_addcamera_black).into(imageViewava);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Button btn_qs = view.findViewById(R.id.btn_quention);
//        btn_qs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), Add_Quention_Activity.class);
//                startActivity(intent);
//            }
//        });


        postList = new ArrayList<>();
        loadPosst();
        SearchView sv22 = (SearchView) view.findViewById(R.id.searchpost);
        sv22.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    SearchPosst(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    SearchPosst(query);
                }
                return false;
            }
        });

        return view;
    }

    private void loadPosst() {

        Query ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_post model_post = ds.getValue(Model_post.class);
                    postList.add(model_post);

                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);

                    adapter_post = new Adapter_Post(getActivity(), postList);
                    recyclerView.setAdapter(adapter_post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SearchPosst(String SearchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_post model_post = ds.getValue(Model_post.class);
                    if (model_post.getpTitle().toLowerCase().contains(SearchQuery.toLowerCase()) ||
                            model_post.getpDesc().toLowerCase().contains(SearchQuery.toLowerCase()) ||
                            model_post.getpCate().toLowerCase().contains(SearchQuery.toLowerCase())) {
                        postList.add(model_post);
                    }

                    adapter_post = new Adapter_Post(getActivity(), postList);
                    adapter_post.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter_post);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}






