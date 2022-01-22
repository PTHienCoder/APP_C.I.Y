package com.pthien.project_ciy.fragement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_GreatPost;
import com.pthien.project_ciy.Adapter.Adapter_Notification;
import com.pthien.project_ciy.Adapter.Adaptervideo;
import com.pthien.project_ciy.Adapter.CatAdapter;
import com.pthien.project_ciy.Model.Model_Cate;
import com.pthien.project_ciy.Model.Model_Notification;
import com.pthien.project_ciy.Model.Model_post;
import com.pthien.project_ciy.Model.Model_video;
import com.pthien.project_ciy.R;

import java.util.ArrayList;
import java.util.List;


public class NoticationsFragment extends Fragment {

    ///////////////////
    RecyclerView recyclerno;
    Adaptervideo adapter;
    Adapter_Notification adapterNotification;
    List<Model_Notification> notifications;
    public NoticationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notications, container, false);

        ////// Category list /////////////////////////////////
        notifications = new ArrayList<>();
        recyclerno = view.findViewById(R.id.rcv_notification);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerno.setLayoutManager(layoutManager);
       String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                   Model_Notification modelno = ds.getValue(Model_Notification.class);
                    notifications.add(modelno);

                    adapterNotification = new Adapter_Notification(getContext(), notifications);
                    recyclerno.setAdapter(adapterNotification);
                    adapterNotification.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<Model_video> options =
                new FirebaseRecyclerOptions.Builder<Model_video>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("VideoPost"), Model_video.class)
                        .build();
        adapter =new Adaptervideo(options, getActivity());
        return view;
    }
    public void reloadata(){
        Toast.makeText(getActivity(), "Reload Data", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.stopListening();
    }
}