package com.pthien.project_ciy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.pthien.project_ciy.AddPostActivity;
import com.pthien.project_ciy.Model.Model_Comments;
import com.pthien.project_ciy.Model.Model_Repcomment;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.RepCommentActivity2;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;

public class Adapter_Comment extends RecyclerView.Adapter<Adapter_Comment.Myholder>{
    Context context;
    List<Model_Comments> modelComments;
    private DatabaseReference cmts;
    private DatabaseReference user;

    RecyclerView rv_repcmt;
    List<Model_Repcomment> model_repcomments;
    Adapter_RepComment adapter_repComment;
    private DatabaseReference repcmts;
    String myUid;
    int countcmt = 0;

    public Adapter_Comment(Context context, List<Model_Comments> modelComments) {
        this.context = context;
        this.modelComments = modelComments;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cmts = FirebaseDatabase.getInstance().getReference("Comments");
        user = FirebaseDatabase.getInstance().getReference("Users");
        repcmts = FirebaseDatabase.getInstance().getReference("RepComment");

    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cmt, parent, false);
        return new Myholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        String uname = modelComments.get(position).getUname();
        String uimg = modelComments.get(position).getUimage();
        String pcmt = modelComments.get(position).getpComment();
        String cmtTime = modelComments.get(position).getCmtTime();
        String idusercmnt = modelComments.get(position).getUid();
        String idcmnt = modelComments.get(position).getCmtId();

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
        holder.repcmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RepCommentActivity2.class);
                intent.putExtra("idCmt", idcmnt);
                intent.putExtra("pCmt", pcmt);
                intent.putExtra("username", uname);
                intent.putExtra("TimeCmt", cmtTime);
                intent.putExtra("iduser", idusercmnt);
                context.startActivity(intent);
            }
        });
        repcmts.child(idcmnt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countcmt = (int) snapshot.getChildrenCount();
                    holder.repcmnt.setText(Integer.toString(countcmt)+" "+"trả lời");
                }else{
                    holder.repcmnt.setText("0"+" "+"trả lời");

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        model_repcomments = new ArrayList<>();
        rv_repcmt = holder.itemView.findViewById(R.id.rcv_repcmnt2);
        rv_repcmt.setLayoutManager(new LinearLayoutManager(context));
        adapter_repComment = new Adapter_RepComment(context, model_repcomments);
        rv_repcmt.setHasFixedSize(true);
        loadrepCmt(idcmnt);



    }


    private void loadrepCmt(String idcmnt) {
        repcmts.child(idcmnt).limitToFirst(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model_repcomments.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_Repcomment comments = ds.getValue(Model_Repcomment.class);
                    model_repcomments.add(comments);

                    rv_repcmt.setAdapter(adapter_repComment);
                    adapter_repComment.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void deletecmt(int position) {
        String pid = modelComments.get(position).getpId();
        String time = modelComments.get(position).getCmtTime();
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
        if (modelComments != null){
            return modelComments.size();
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
            repcmnt =itemView.findViewById(R.id.repcmt);;

        }
    }
}
