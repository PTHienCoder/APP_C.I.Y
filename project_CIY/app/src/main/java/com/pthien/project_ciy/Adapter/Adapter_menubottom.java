package com.pthien.project_ciy.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pthien.project_ciy.fragement.DiscoverFragment;
import com.pthien.project_ciy.fragement.HomeFragment;
import com.pthien.project_ciy.fragement.NoticationsFragment;
import com.pthien.project_ciy.fragement.VideosFragment;

public class Adapter_menubottom extends FragmentStatePagerAdapter {


    public Adapter_menubottom(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new VideosFragment();
            case 2:
                return new DiscoverFragment();
            case 3:
                return new NoticationsFragment();
            default:
                return new HomeFragment();
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
}