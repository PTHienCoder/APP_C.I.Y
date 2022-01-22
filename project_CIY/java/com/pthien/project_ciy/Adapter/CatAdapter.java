package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pthien.project_ciy.R;
import com.pthien.project_ciy.Model.Model_Cate;
import com.squareup.picasso.Picasso;
//import com.bumptech.glide.Glide;
import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.PlaceViewHolder> {

    private List<Model_Cate> categoryModelList;
    private Context context;

    public CatAdapter(List<Model_Cate> categoryModelList, Context context) {
        this.categoryModelList = categoryModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cate,viewGroup,false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Model_Cate categoryModel = categoryModelList.get(position);

        holder.cat_title.setText(categoryModel.getName());

        try {
            Picasso.get().load(categoryModel.getImage()).placeholder(R.drawable.ic_face).into(holder.cat_img);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private ImageView cat_img;
        private TextView cat_title;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            cat_img =  itemView.findViewById(R.id.cat_img);
            cat_title = itemView.findViewById(R.id.textView2);

        }
    }
}

