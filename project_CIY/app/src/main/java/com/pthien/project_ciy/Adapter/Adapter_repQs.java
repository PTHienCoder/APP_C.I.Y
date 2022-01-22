package com.pthien.project_ciy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.pthien.project_ciy.Model.Model_repQs;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_repQs extends  RecyclerView.Adapter<Adapter_repQs.Myholder>{

    Context context;
    List<Model_repQs> modelRepQs;
    private DatabaseReference cmts;
    private DatabaseReference user;
    String myUid;


    public Adapter_repQs(Context context, List<Model_repQs> modelRepQs) {
        this.context = context;
        this.modelRepQs = modelRepQs;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cmts = FirebaseDatabase.getInstance().getReference("repQuestions");
        user = FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemtlqs, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {

        String uname = modelRepQs.get(position).getUname();
        String pcmt = modelRepQs.get(position).getqComment();
        String cmtTime = modelRepQs.get(position).getRepTime();
        String idusercmnt = modelRepQs.get(position).getUid();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(cmtTime));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        holder.tv_name.setText(uname);
        holder.tvtime.setText(datetime);
        holder.tvcmt.setText(pcmt);
        Query query = user.orderByChild("uid").equalTo(idusercmnt);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String img = ""+ ds.child("image").getValue();
                    try {
                        Picasso.get().load(img).placeholder(R.drawable.ic_face).into(holder.img_cmt);
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        try {
//            Picasso.get().load(uimg).placeholder(R.drawable.ic_face).into(holder.img_cmt);
//        }catch (Exception e){
//
//        }

        holder.lu_cmt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!idusercmnt.equals(myUid)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Gỡ bỏ bình luận");
                    builder.setMessage("Bạn không Thể gỡ bỏ bình luận này !");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Gỡ bỏ bình luận");
                    builder.setMessage("Bạn có chắc là muốn gỡ bỏ bình luận này không?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletecmt(position);
                        }
                    });
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                }

                return false;
            }
        });

    }

    private void deletecmt(int position) {
        String pid = modelRepQs.get(position).getqId();
        String time = modelRepQs.get(position).getRepTime();
        cmts.child(pid).child(time).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Đã gỡ bình luận...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Không Thể gỡ bình luận này...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (modelRepQs != null){
            return modelRepQs.size();
        }

        return 0;
    }

    class Myholder extends RecyclerView.ViewHolder{
        ImageView img_cmt;
        LinearLayout lu_cmt;
        TextView tv_name, tvcmt, tvtime;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            img_cmt = itemView.findViewById(R.id.avatar_cmt);
            tv_name = itemView.findViewById(R.id.name_cmt);
            tvcmt = itemView.findViewById(R.id.cmt_tv);
            tvtime = itemView.findViewById(R.id.time_cmt);
            lu_cmt = itemView.findViewById(R.id.lu_cmt);

        }
    }
}
