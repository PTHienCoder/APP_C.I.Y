package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.pthien.project_ciy.Model.BannerModel;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.PlaceViewHolder> {

    private List<BannerModel> bannerModelList;
    private Context context;

    public BannerAdapter(List<BannerModel> bannerModelList, Context context) {
        this.bannerModelList = bannerModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_banner,viewGroup,false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        BannerModel bannerModel = bannerModelList.get(position);
        try {
            Picasso.get().load(bannerModel.getBanner_img()).placeholder(R.drawable.ic_face).into(holder.banner_img);
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return bannerModelList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private ImageView banner_img;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            banner_img =  itemView.findViewById(R.id.banner_img);

        }
    }
}


