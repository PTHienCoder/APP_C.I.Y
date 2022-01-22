package com.pthien.project_ciy.fragement;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.pthien.project_ciy.Adapter.Adapter_video;
import com.pthien.project_ciy.Adapter.Adaptervideo;
import com.pthien.project_ciy.Model.Model_video;
import com.pthien.project_ciy.R;

import java.util.ArrayList;
import java.util.List;


public class VideosFragment extends Fragment {
    RecyclerView recyclerView;
    List<Model_video> videoList;
    Adapter_video adapter_video;

    ViewPager2 viewPager2;
    Adaptervideo adapter;

    public VideosFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

//        recyclerView = view.findViewById(R.id.rvc_vd);
//        layoutManager = new LinearLayoutManager(getActivity());
//
//        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
//        Loadvd();

        viewPager2 = (ViewPager2)view.findViewById(R.id.vp_video);
         FirebaseRecyclerOptions<Model_video> options =
                new FirebaseRecyclerOptions.Builder<Model_video>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("VideoPost"), Model_video.class)
                        .build();

        viewPager2.setSaveFromParentEnabled(true);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        adapter =new Adaptervideo(options, getActivity());
        viewPager2.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


//    private void Loadvd() {
//        videoList = new ArrayList<>();
//        Query ref = FirebaseDatabase.getInstance().getReference("VideoPost");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                videoList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    Model_video model_post = ds.getValue(Model_video.class);
//                    videoList.add(model_post);
//
//
//                    recyclerView.setLayoutManager(layoutManager);
//                    adapter_video = new Adapter_video(getActivity(), videoList);
//                    recyclerView.setAdapter(adapter_video);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    public void reloadata(){
        Toast.makeText(getActivity(), "Reload Data", Toast.LENGTH_SHORT).show();
    }
}