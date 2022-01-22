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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_GreatPost;
import com.pthien.project_ciy.Adapter.BannerAdapter;
import com.pthien.project_ciy.Adapter.CatAdapter;
import com.pthien.project_ciy.Model.BannerModel;
import com.pthien.project_ciy.Model.Model_Cate;

import com.pthien.project_ciy.Model.Model_post;
import com.pthien.project_ciy.R;

import java.util.ArrayList;
import java.util.List;


public class DiscoverFragment extends Fragment {
    RecyclerView recyclerViewCategory;
    CatAdapter catAdapter;
    List<Model_Cate> categoryModelList;

    ////banner slider///
    RecyclerView recyclerViewBanner;
    BannerAdapter bannerAdapter;
    List<BannerModel> bannerModelList;
    ///////////////////
    RecyclerView recyclerViewGreat;
    Adapter_GreatPost adapterGreatPost;
    List<Model_post> postList;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
           // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_discover, container, false);

        ////// Category list /////////////////////////////////
        categoryModelList = new ArrayList<>();
        recyclerViewCategory = view.findViewById(R.id.recyclerViewCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewCategory.setLayoutManager(layoutManager);


        Query ref = FirebaseDatabase.getInstance().getReference("spinnerdata");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_Cate modelCate = ds.getValue(Model_Cate.class);
                    categoryModelList.add(modelCate);

                    catAdapter = new CatAdapter(categoryModelList,getContext());
                    recyclerViewCategory.setAdapter(catAdapter);
                    catAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ////// Banner list start /////////////////////////////////
        recyclerViewBanner = view.findViewById(R.id.recyclerViewBanner);
        LinearLayoutManager layoutManagerBanner = new LinearLayoutManager(getContext());
        layoutManagerBanner.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewBanner.setLayoutManager(layoutManagerBanner);

        bannerModelList = new ArrayList<>();
        bannerModelList.add(new BannerModel(R.drawable.baner1));
        bannerModelList.add(new BannerModel(R.drawable.baner2));
        bannerModelList.add(new BannerModel(R.drawable.banner3));

        bannerAdapter = new BannerAdapter(bannerModelList,getContext());
        recyclerViewBanner.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        ////// Banner list end /////////////////////////////////

        postList = new ArrayList<>();
        recyclerViewGreat = view.findViewById(R.id.recyclerViewGreatoffer);
        LinearLayoutManager layoutManagerGreat = new LinearLayoutManager(getContext());
        layoutManagerGreat.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewGreat.setLayoutManager(layoutManagerGreat);
        layoutManagerGreat.setStackFromEnd(true);
        layoutManagerGreat.setReverseLayout(true);

        Query ref3 = FirebaseDatabase.getInstance().getReference("Posts");
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String pImage = ""+ ds.child("pImage").getValue();
                    if (!pImage.equals("noImage")){
                        Model_post model = ds.getValue(Model_post.class);
                        postList.add(model);
                    }



                    adapterGreatPost = new Adapter_GreatPost(postList,getContext());
                    recyclerViewGreat.setAdapter(adapterGreatPost);
                    adapterGreatPost.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        return view;
    }
    public void reloadata(){
        Toast.makeText(getActivity(), "Reload Data", Toast.LENGTH_SHORT).show();
    }
}