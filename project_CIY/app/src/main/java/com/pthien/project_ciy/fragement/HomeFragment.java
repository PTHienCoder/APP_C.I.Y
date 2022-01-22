package com.pthien.project_ciy.fragement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_Tab;
import com.pthien.project_ciy.Adapter.Adaptervideo;
import com.pthien.project_ciy.AddPostActivity;

import com.pthien.project_ciy.DashboardActivity;
import com.pthien.project_ciy.Model.Model_video;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.SearchActivity;
import com.pthien.project_ciy.ShowUserChatActivity;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment {
    ActionBarDrawerToggle mToggle;
    private TabLayout tabLayouth;
    private ViewPager viewPagerh;
    Adaptervideo adapter;

    public HomeFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        tabLayouth = view.findViewById(R.id.tab);
        viewPagerh = view.findViewById(R.id.tab_layout);
         Adapter_Tab adapter_tab = new Adapter_Tab(getActivity().getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
         viewPagerh.setAdapter(adapter_tab);
         tabLayouth.setupWithViewPager(viewPagerh);
         viewPagerh.setOffscreenPageLimit(1);



        FirebaseRecyclerOptions<Model_video> options =
                new FirebaseRecyclerOptions.Builder<Model_video>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("VideoPost"), Model_video.class)
                        .build();
        adapter =new Adaptervideo(options, getActivity());
        return view;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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

