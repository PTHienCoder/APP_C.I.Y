package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pthien.project_ciy.ChatActivity;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_user_chat extends RecyclerView.Adapter<Adapter_user_chat.Myholder> {
    Context context;
    List<Model_user> userList;

    public Adapter_user_chat(Context context, List<Model_user> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int i) {
        Model_user model_user = userList.get(i);
        if (model_user == null){
            return;
        }
        //get data
        String hisUID = userList.get(i).getUid();
        String userimage = userList.get(i).getImage();
        String username = userList.get(i).getName();
        String useremail = userList.get(i).getEmail();

        holder.nametv.setText(username);
        holder.emailtv.setText(useremail);
        try {
            Picasso.get().load(userimage)
                    .placeholder(R.drawable.ic_face)
                    .into(holder.avatar);
        }catch (Exception e){

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userList != null){
            return userList.size();
        }

        return 0;
    }


    class Myholder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView nametv, emailtv;
        public Myholder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar_user);
            nametv = itemView.findViewById(R.id.nametv_user);
            emailtv = itemView.findViewById(R.id.emailtv_user);

        }
    }
}
