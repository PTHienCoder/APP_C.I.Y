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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Adapter_thumoiGr extends RecyclerView.Adapter<Adapter_thumoiGr.ViewHolder> {
    Context context;
    List<Model_group> modelGroup;
    String myUid;
    DatabaseReference ref1;
    DatabaseReference ref2;
    DatabaseReference ref3;
    boolean mProcesstg = false;

    public Adapter_thumoiGr(Context context, List<Model_group> modelGroup) {
        this.context = context;
        this.modelGroup = modelGroup;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref1 = FirebaseDatabase.getInstance().getReference("Users");
        ref3 = FirebaseDatabase.getInstance().getReference("GroupList");
        ref2 = FirebaseDatabase.getInstance().getReference("Groups");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.iteam_thumoi,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String idGrs = modelGroup.get(position).getIdGr();
        String descGr = modelGroup.get(position).getDescGr();
        String chedo = modelGroup.get(position).getChedo();
        String cover = modelGroup.get(position).getCoverGr();

        String title = modelGroup.get(position).getTitleGr();

                    holder.tv_name.setText(title);
                    holder.tv_sotv.setText(chedo);
                    holder.tv_desc.setText(descGr);

                    try {
                        Picasso.get().load(cover).placeholder(R.drawable.ic_face).into(holder.avata);
                    }catch (Exception e){

                    }

        setthamgia(holder, idGrs);
        holder.btn_xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcesstg = true;
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(myUid).child("MyGroup").hasChild(idGrs)  ){
                                ref1.child(myUid).child("MyGroup").child(idGrs).removeValue();
                                ref3.child(idGrs).child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Đã huỷ tham gia.", Toast.LENGTH_SHORT).show();
                                    }});
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.btn_tg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcesstg = true;
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcesstg){
                            if (snapshot.child(idGrs).hasChild(myUid)  ){
                                ref1.child(idGrs).child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.btn_tg.setText("Tham gia");
                                        Toast.makeText(context, "Đã huỷ tham gia.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mProcesstg = false;
                            }else{
                                HashMap<String, Object> hashMap2 = new HashMap<>();
                                hashMap2.put("iduser",myUid);
                                hashMap2.put("check", "true");
                                hashMap2.put("idGr",idGrs);
                                ref1.child(myUid).child("MyGroup").child(idGrs).setValue(hashMap2);
                                ref3.child(idGrs).child(myUid).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.btn_tg.setText("Đã tham gia");
                                        Toast.makeText(context, "Đã tham gia.", Toast.LENGTH_SHORT).show();
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
//        Query ref3 = FirebaseDatabase.getInstance().getReference("Groups");
        holder.Intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Details_GroupActivity.class);
                intent.putExtra("idGr", idGrs);
                context.startActivity(intent);
            }
        });

    }

    private void setthamgia(ViewHolder holder, String idGr) {
//        ref1.orderByChild("idGr").equalTo(idGr);
        ref1.child(myUid).child("MyGroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()){
                    if (snapshot.hasChild(idGr) && snapshot.child(idGr).child("check").getValue().equals("true")){
                        holder.btn_tg.setVisibility(View.GONE);
                        holder.btn_xoa.setVisibility(View.GONE);

//                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (modelGroup != null) {
            return modelGroup.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avata;
        private TextView tv_name,tv_sotv,tv_desc;
        private Button btn_tg, btn_xoa;
        LinearLayout Intent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avata =  itemView.findViewById(R.id.avatar_Gr);
            tv_name = itemView.findViewById(R.id.nametv_Gr);
            tv_sotv = itemView.findViewById(R.id.sotv);
            tv_desc = itemView.findViewById(R.id.desctv_Gr);
            btn_tg = itemView.findViewById(R.id.TG);
            btn_xoa = itemView.findViewById(R.id.TC);
            Intent = itemView.findViewById(R.id.intent);
        }
    }
}
