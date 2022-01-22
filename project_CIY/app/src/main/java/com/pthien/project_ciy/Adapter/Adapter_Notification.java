package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pthien.project_ciy.DetailsPostActivity;
import com.pthien.project_ciy.Model.Model_Notification;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_Notification extends RecyclerView.Adapter<Adapter_Notification.Myholder>{
    Context context;
    List<Model_Notification> modelNotifications;
    private DatabaseReference user;
    String myUid;
    public Adapter_Notification(Context context, List<Model_Notification> modelNotifications) {
        this.context = context;
        this.modelNotifications = modelNotifications;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        //get data
        String hisUID = modelNotifications.get(position).getUid();
        String content = modelNotifications.get(position).getContent();
        String idp = modelNotifications.get(position).getIdp();
        String img = modelNotifications.get(position).getUimg();
        String time = modelNotifications.get(position).getTimeNoti();
        String name = modelNotifications.get(position).getUname();
        holder.contenttv.setText("Vừa bình luận : "+content);
        holder.nametv.setText(name);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.timetv.setText(datetime);
        try {
            Picasso.get().load(img)
                    .placeholder(R.drawable.ic_face)
                    .into(holder.avatar);
        }catch (Exception e){

        }
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(time);
            }
        });
        holder.ul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsPostActivity.class);
                intent.putExtra("postid", idp);
                context.startActivity(intent);
            }
        });
    }

    private void delete(String time) {
        user.child(myUid).child("Notification").child(time).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "xoá thông báo thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "xoá thông báo thất bại", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelNotifications.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        ImageView avatar;
        ImageButton btn_delete;
        TextView nametv, contenttv, timetv;
        RelativeLayout ul;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_useruc);
            nametv = itemView.findViewById(R.id.name_noti);
            btn_delete = itemView.findViewById(R.id.delete);
            contenttv = itemView.findViewById(R.id.tvnoti);
            timetv = itemView.findViewById(R.id.timenoti);
            ul = itemView.findViewById(R.id.ul);


        }
    }
}
