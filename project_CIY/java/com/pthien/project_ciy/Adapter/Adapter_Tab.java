package com.pthien.project_ciy.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pthien.project_ciy.fragement.Home1Fragment;
import com.pthien.project_ciy.fragement.Home2Fragment;
import com.pthien.project_ciy.fragement.HomeFragment;

public class Adapter_Tab extends FragmentStatePagerAdapter {

    public Adapter_Tab(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Home1Fragment();
            case 1:
                return new Home2Fragment();
            default:
                return new HomeFragment();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
         String Title ="";
         switch (position){
             case 0:
                 Title = "Cộng Đồng";
                 break;
             case 1:
                 Title ="Diễn Đàn";
                 break;
         }
         return Title;

    }

}
