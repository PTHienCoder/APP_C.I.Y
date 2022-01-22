package com.pthien.project_ciy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.pthien.project_ciy.Adapter.Adaptervideo;
import com.pthien.project_ciy.Model.Model_video;

public class testvdActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    Adaptervideo adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testvd);


        viewPager2 = (ViewPager2)findViewById(R.id.vp_video);
         FirebaseRecyclerOptions<Model_video> options =
                new FirebaseRecyclerOptions.Builder<Model_video>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("VideoPost"), Model_video.class)
                        .build();
        adapter =new Adaptervideo(options, testvdActivity.this);
        viewPager2.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}