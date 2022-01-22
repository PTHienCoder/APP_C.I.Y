package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Model.Model_group;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.ThereProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Adapter_MoiGroup extends RecyclerView.Adapter<Adapter_MoiGroup.ViewHolder> {
    Context context;
    String idGr;
    List<Model_user> userList;
    private DatabaseReference grlist;
    private DatabaseReference grlist1;
    boolean mProcessfollow = false;
    String myUid;

    public Adapter_MoiGroup(Context context, String idGr, List<Model_user> userList) {
        this.context = context;
        this.idGr = idGr;
        this.userList = userList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        grlist = FirebaseDatabase.getInstance().getReference("GroupList");
        grlist1 = FirebaseDatabase.getInstance().getReference("Users");
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
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
                grlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessfollow){
                            if (snapshot.child(idGr).hasChild(hisUID)  ){
                                grlist1.child(hisUID).child("MyGroup").child(idGr).removeValue();
                                grlist.child(idGr).child(hisUID).removeValue();
                                mProcessfollow = false;
                            }else {
                                HashMap<String, Object> hashMap2 = new HashMap<>();
                                hashMap2.put("iduser",hisUID);
                                hashMap2.put("check", "0");
                                hashMap2.put("idGr",idGr);
                                grlist1.child(hisUID).child("MyGroup").child(idGr).setValue(hashMap2);
                                grlist.child(idGr).child(hisUID).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Đã gửi lời mời tham gia.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mProcessfollow = false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
    private void setfollow(ViewHolder holder, String hisUID) {
        grlist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(idGr).hasChild(hisUID) &&
                    snapshot.child(idGr).child(hisUID).child("check").getValue().equals("0")){
                    holder.follow.setText("Đã Mời");
                }else if (snapshot.child(idGr).hasChild(hisUID) &&
                    snapshot.child(idGr).child(hisUID).child("check").getValue().equals("true")){
                    holder.follow.setVisibility(View.GONE);
                }
                else if (snapshot.child(idGr).hasChild(hisUID) &&
                        snapshot.child(idGr).child(hisUID).child("check").getValue().equals("false")){
                    holder.follow.setVisibility(View.GONE);
                }else{
                    holder.follow.setText("Mời");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        Button follow;
        CardView kkk;
        TextView nametv, emailtv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_user);
            nametv = itemView.findViewById(R.id.nametv_user);
            emailtv = itemView.findViewById(R.id.emailtv_user);
            follow = itemView.findViewById(R.id.follow);
            kkk = itemView.findViewById(R.id.kkkl);

        }
    }
}
