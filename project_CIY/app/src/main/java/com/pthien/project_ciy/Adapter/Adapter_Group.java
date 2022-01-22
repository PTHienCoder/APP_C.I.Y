package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Details_GroupActivity;
import com.pthien.project_ciy.Model.Model_group;
import com.pthien.project_ciy.Model.model_GroupList;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.ThereProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Adapter_Group extends RecyclerView.Adapter<Adapter_Group.ViewHolder> {
    Context context;
    List<Model_group> model_groups;
    String myUid;
    DatabaseReference ref1;
    DatabaseReference ref2;
    boolean mProcesstg = false;
    public Adapter_Group(Context context, List<Model_group> model_groups) {
        this.context = context;
        this.model_groups = model_groups;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref1 = FirebaseDatabase.getInstance().getReference("GroupList");
        ref2= FirebaseDatabase.getInstance().getReference("Users");
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String coverGr = model_groups.get(position).getCoverGr();
        String titleGr = model_groups.get(position).getTitleGr();
        String idGr = model_groups.get(position).getIdGr();
        String descGr = model_groups.get(position).getDescGr();
        String chedo = model_groups.get(position).getChedo();
        holder.tv_name.setText(titleGr);
        holder.tv_desc.setText(descGr);
        holder.tv_sotv.setText(chedo);
        try {
            Picasso.get().load(coverGr).placeholder(R.drawable.ic_face).into(holder.avata);
        }catch (Exception e){
        }
        setthamgia(holder, idGr);
        holder.btn_tg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcesstg = true;
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mProcesstg){
                                if (snapshot.child(idGr).hasChild(myUid)  ){
                                    ref2.child(myUid).child("MyGroup").child(idGr).removeValue();
                                    ref1.child(idGr).child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.btn_tg.setText("Tham gia");
                                            Toast.makeText(context, "Đã huỷ yêu cầu tham gia.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    mProcesstg = false;
                                }else{
                                    HashMap<String, Object> hashMap2 = new HashMap<>();
                                    hashMap2.put("iduser",myUid);
                                    hashMap2.put("check", "false");
                                    hashMap2.put("idGr",idGr);
                                    ref2.child(myUid).child("MyGroup").child(idGr).setValue(hashMap2);
                                    ref1.child(idGr).child(myUid).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.btn_tg.setText("Đã yêu cầu");
                                            Toast.makeText(context, "Đã gửi yêu cầu tham gia.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                   mProcesstg = false;
                                }
                        }
                    }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
               }
            });
//        Query ref3 = FirebaseDatabase.getInstance().getReference("GroupList");

        holder.Intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Details_GroupActivity.class);
                intent.putExtra("idGr", idGr);
                context.startActivity(intent);
            }
        });

    }

    private void setthamgia(ViewHolder holder, String idGr) {
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(idGr).hasChild(myUid) &&
                        snapshot.child(idGr).child(myUid).child("check").getValue().equals("false")){
                    holder.btn_tg.setText("Đã yêu cầu");
                }else if (snapshot.child(idGr).hasChild(myUid) &&
                        snapshot.child(idGr).child(myUid).child("check").getValue().equals("true") ){
                        holder.btn_tg.setVisibility(View.GONE);
                }
                else if (snapshot.child(idGr).hasChild(myUid) &&
                        snapshot.child(idGr).child(myUid).child("check").getValue().equals("0") ){
                    holder.btn_tg.setVisibility(View.GONE);
                }else{
                    holder.btn_tg.setText("Tham gia");
                }
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    String check = ""+ ds.child("check").getValue();
//                    String userid = ""+ ds.child("iduser").getValue();
//                    if (userid.equals(myUid) && check.equals("0") || userid.equals(myUid) && check.equals("true")){
//                        holder.btn_tg.setVisibility(View.GONE);
//                    }
//                    if(userid.equals(myUid) && check.equals("false")) {
//                        holder.btn_tg.setText("Đã yêu cầu");
//                    } else{
//                        holder.btn_tg.setText("Tham gia");
//                        }
//
//                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (model_groups != null) {
            return model_groups.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avata;
        private TextView tv_name,tv_sotv,tv_desc;
        private Button btn_tg;
        LinearLayout Intent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avata =  itemView.findViewById(R.id.avatar_Gr);
            tv_name = itemView.findViewById(R.id.nametv_Gr);
            tv_sotv = itemView.findViewById(R.id.sotv);
            tv_desc = itemView.findViewById(R.id.desctv_Gr);
            btn_tg = itemView.findViewById(R.id.TG);
            Intent = itemView.findViewById(R.id.intent);
        }
    }
}
