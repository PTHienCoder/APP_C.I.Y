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
import com.pthien.project_ciy.Model.Model_Comments;
import com.pthien.project_ciy.Model.Model_Repcomment;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_RepComment extends RecyclerView.Adapter<Adapter_RepComment.Myholder>{
    Context context;
    List<Model_Repcomment> modelRepcomments;
    private DatabaseReference user;
    private DatabaseReference repcmt;
    String myUid;

    public Adapter_RepComment(Context context, List<Model_Repcomment> modelRepcomments) {
        this.context = context;
        this.modelRepcomments = modelRepcomments;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseDatabase.getInstance().getReference("Users");
        repcmt = FirebaseDatabase.getInstance().getReference("RepComment");
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_repcmnt, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        String uname = modelRepcomments.get(position).getUname();
        String pcmt = modelRepcomments.get(position).getRepComment();
        String cmtTime = modelRepcomments.get(position).getRepcmtTime();
        String idusercmnt = modelRepcomments.get(position).getUid();
        String idcmnt = modelRepcomments.get(position).getCmtId();

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
        holder.lu_cmt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!idusercmnt.equals(myUid)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Gỡ bỏ bình luận");
                    builder.setMessage("Bạn không thể gỡ bỏ bình luận này !");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Gỡ bỏ trả lời bình luận");
                    builder.setMessage("Bạn có chắc là muốn gỡ bỏ trả lời bình luận này không?");
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
        String pid = modelRepcomments.get(position).getCmtId();
        String time = modelRepcomments.get(position).getRepcmtTime();
        repcmt.child(pid).child(time).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Đã gỡ câu trả lời...", Toast.LENGTH_SHORT).show();
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
        if (modelRepcomments != null){
            return modelRepcomments.size();
        }

        return 0;
    }

    class Myholder extends RecyclerView.ViewHolder{
        ImageView img_cmt;
        LinearLayout lu_cmt;
        TextView tv_name, tvcmt, tvtime,repcmnt;
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
