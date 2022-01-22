package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.DetailsPostActivity;
import com.pthien.project_ciy.Model.Model_post;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_GreatPost extends RecyclerView.Adapter<Adapter_GreatPost.PlaceViewHolder> {
    private List<Model_post> modelPosts;
    private Context context;
    private DatabaseReference Likeref;
    int countlikes = 0;
    public Adapter_GreatPost(List<Model_post> modelPosts, Context context) {
        this.modelPosts = modelPosts;
        this.context = context;
        Likeref = FirebaseDatabase.getInstance().getReference("Likes");
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postgreat,parent,false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.text_title.setText(modelPosts.get(position).getpTitle());

        String time = modelPosts.get(position).getpTime();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.text_description.setText(datetime);

        holder.text_saleoff.setText(modelPosts.get(position).getpCate());
        String poIde = modelPosts.get(position).getpId();

        Likeref.child(poIde).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countlikes = (int) snapshot.getChildrenCount();
                    holder.text_ratting.setText(Integer.toString(countlikes));
                }else{
                    holder.text_ratting.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        try {
            Picasso.get().load(modelPosts.get(position).getpImage()).placeholder(R.drawable.ic_face).into(holder.offer_img);
        }catch (Exception e){

        }
      holder.offer_img.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(context, DetailsPostActivity.class);
              intent.putExtra("postid", poIde);
              context.startActivity(intent);
          }
      });



    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private ImageView offer_img;
        private TextView text_title,text_description,text_saleoff,text_ratting;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            offer_img =  itemView.findViewById(R.id.offer_img);
            text_title = itemView.findViewById(R.id.text_title);
            text_description = itemView.findViewById(R.id.text_description);
            text_saleoff = itemView.findViewById(R.id.text_saleoff);
            text_ratting = itemView.findViewById(R.id.text_ratting);
        }
    }
}
