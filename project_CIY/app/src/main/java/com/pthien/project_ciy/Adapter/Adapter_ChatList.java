package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;
import java.util.List;

public class Adapter_ChatList extends RecyclerView.Adapter<Adapter_ChatList.Myholder>{
     Context context;
     List<Model_user> userList;
    private DatabaseReference user;
     private HashMap<String, String> lastMessageMap;
     String myUid;

    public Adapter_ChatList(Context context, List<Model_user> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_chat, parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int i) {
        String hisUID = userList.get(i).getUid();
        String userimage = userList.get(i).getImage();
        String username = userList.get(i).getName();
        String lastMessage = lastMessageMap.get(hisUID);
        holder.namechat.setText(username);

        if (lastMessage==null || lastMessage.equals("default")){
            holder.lastmessagechat.setVisibility(View.GONE);
        }else{
            holder.lastmessagechat.setVisibility(View.VISIBLE);
            holder.lastmessagechat.setText(lastMessage);
        }

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
        checkisBolck(holder, hisUID, i);
      checkOnline(holder, i);
        holder.Onlinestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userList.get(i).isIsblock()){
                    UnBlockuser(hisUID);
                }else {
                    Blockuser(hisUID);
                }
            }
        });
    }
    private void checkisBolck(final Myholder holder, final String hisUID, final  int i) {
           user.child(myUid).child("BlockerUser")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(hisUID)) {
                            holder.Onlinestatus.setImageResource(R.drawable.ic_block);
                            userList.get(i).setIsblock(true);
                        }else {
                            userList.get(i).setIsblock(false);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void Blockuser(String hisUID) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", hisUID);
        user.child(myUid).child("BlockerUser").child(hisUID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Chặn thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Chặn thất bại", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void UnBlockuser(String hisUID) {

        user.child(myUid).child("BlockerUser").orderByChild("uid").equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            if (ds.exists()){
                                ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Mở chăn thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkOnline(Myholder myholder ,int i) {
        String online = userList.get(i).getOnlineStatus();
        if (online.equals("Online")){
                myholder.Onlinestatus.setImageResource(R.drawable.bg_online);
        }else{
            myholder.Onlinestatus.setImageResource(R.drawable.bg_offline);
        }
    }
    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        ImageView avatar, Onlinestatus;
        TextView namechat, lastmessagechat;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_useruc);
            Onlinestatus = itemView.findViewById(R.id.notiOnline);
            namechat = itemView.findViewById(R.id.nametv_useruc);
            lastmessagechat = itemView.findViewById(R.id.lasmessgerchat);
        }
    }
}
