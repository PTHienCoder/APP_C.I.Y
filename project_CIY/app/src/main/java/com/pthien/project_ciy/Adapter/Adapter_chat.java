package com.pthien.project_ciy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Model.Model_chat;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Adapter_chat extends RecyclerView.Adapter<Adapter_chat.myHolder>{
  private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Model_chat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    public Adapter_chat(Context context, List<Model_chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
            return new myHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_letf, parent, false);
            return new myHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, final int position) {
       String message = chatList.get(position).getMessage();
       String timestamp = chatList.get(position).getTimestamp();
        String type = chatList.get(position).getType();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();


        holder.timeTV.setText(datetime);
        try {
            Picasso.get().load(imageUrl).into(holder.avatar_chat);

        }catch (Exception e){

        }
        if(type.equals("text")){
            holder.mesage.setText(message);
            holder.mesage.setVisibility(View.VISIBLE);
            holder.send_imgae.setVisibility(View.GONE);
        }else if (type.equals("image") && message.equals("Tin nh???n n??y ???? ???????c thu h???i.")){
            holder.send_imgae.setVisibility(View.GONE);
            holder.mesage.setVisibility(View.VISIBLE);
            holder.mesage.setText("H??nh ???nh ???? ???????c g??? b???.");
        }else if (type.equals("image")){
            Picasso.get().load(message).placeholder(R.drawable.ic_image).into(holder.send_imgae);
            holder.send_imgae.setVisibility(View.VISIBLE);
            holder.mesage.setVisibility(View.GONE);
        }

        holder.layout_item_chat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("G??? b??? tin nh???n");
                builder.setMessage("B???n c?? ch???c l?? mu???n g??? b??? tin nh???n n??y kh??ng?");
                builder.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      deletemessage(position);
                    }
                });
                builder.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
              builder.create().show();
              return false;
            }
        });
     if (position==chatList.size()-1){
          if (chatList.get(position).isSeen()){
              holder.isSeentv.setText("Seen.");
          }else{
              holder.isSeentv.setText("Delivered.");
          }
     }else {
         holder.isSeentv.setVisibility(View.GONE);
     }

    }

    private void deletemessage(int position) {
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(myUID)){
//                        ds.getRef().removeValue();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message","Tin nh???n n??y ???? ???????c thu h???i.");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "??ang xo?? tin nh???n...", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "B???n kh??ng th??? xo?? tin nh???n n??y...", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
         if (chatList.get(position).getSender().equals(fUser.getUid())){
             return MSG_TYPE_RIGHT;
         }else{
             return MSG_TYPE_LEFT;
         }

    }

    class myHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout_item_chat;
          ImageView avatar_chat, send_imgae;
          TextView mesage, timeTV, isSeentv;
        public myHolder(@NonNull View itemView) {
            super(itemView);

            avatar_chat = itemView.findViewById(R.id.avatar_chat);
            mesage = itemView.findViewById(R.id.chat_tv);
            isSeentv = itemView.findViewById(R.id.isSeen);
            timeTV = itemView.findViewById(R.id.time_chat);
            send_imgae = itemView.findViewById(R.id.send_image);
            layout_item_chat = itemView.findViewById(R.id.layout_item_chat);
        }
    }
}
