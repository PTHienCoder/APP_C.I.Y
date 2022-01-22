package com.pthien.project_ciy.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.ChatActivity;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.ThereProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_user extends RecyclerView.Adapter<Adapter_user.Myholder>{
    Context context;
    List<Model_user> userList;
    String myUid;
    private DatabaseReference friend;
    boolean mProcessfollow = false;


    public Adapter_user(Context context, List<Model_user> userList) {
        this.context = context;
        this.userList = userList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friend = FirebaseDatabase.getInstance().getReference("Friends");
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
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", hisUID);
                context.startActivity(intent);
            }
        });
        setfollow(holder, hisUID);
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessfollow = true;
                String hisUid= userList.get(i).getUid();
                friend.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessfollow){
                            if (snapshot.child(hisUid).hasChild(myUid)  ){
                                friend.child(hisUid).child(myUid).removeValue();
                                mProcessfollow = false;
                            }else
                                friend.child(hisUid).child(myUid).child("id").setValue(myUid);
                            mProcessfollow = false;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void setfollow(Adapter_user.Myholder holder, String hisUID) {
        friend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(hisUID).hasChild(myUid) ){
                        holder.follow.setText("Đã Follower");
                }else{
                    holder.follow.setText("Follower");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });;
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
        Button follow;
        TextView nametv, emailtv;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_user);
            nametv = itemView.findViewById(R.id.nametv_user);
            emailtv = itemView.findViewById(R.id.emailtv_user);
            follow = itemView.findViewById(R.id.follow);


        }
    }

}