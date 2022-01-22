package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_ChatList;
import com.pthien.project_ciy.Adapter.Adapter_userOnline;
import com.pthien.project_ciy.Adapter.CatAdapter;
import com.pthien.project_ciy.Model.Model_Cate;
import com.pthien.project_ciy.Model.Model_ChatList;
import com.pthien.project_ciy.Model.Model_chat;
import com.pthien.project_ciy.Model.Model_user;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowUserChatActivity extends AppCompatActivity {
    RecyclerView rcv_user_chat1;
    Adapter_ChatList adapterChatList;
    List<Model_ChatList> modelChatLists;
    List<Model_user> userList;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseUser currentUser;
    String MyUid;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recycleruseronline;
    Adapter_userOnline adapterUserOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_chat);

        ImageButton backs = findViewById(R.id.backc);
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
          modelChatLists = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser  = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ChatsList").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 modelChatLists.clear();
                 for (DataSnapshot ds: snapshot.getChildren()){
                     Model_ChatList chatList = ds.getValue(Model_ChatList.class);
                     modelChatLists.add(chatList);
                 }
                 loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });;
        checkUserStatus();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(currentUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String image = ""+ ds.child("image").getValue();
                    ImageView im = findViewById(R.id.avatar_chatss);

                    try {
                        Picasso.get().load(image).into(im);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_addcamera_black).into(im);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
          userList = new ArrayList<>();
         rcv_user_chat1 = findViewById(R.id.rcv_user_chat1);

        recycleruseronline = findViewById(R.id.rcv_useronline);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ShowUserChatActivity.this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycleruseronline.setLayoutManager(layoutManager);


        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_user model_user = ds.getValue(Model_user.class);
                    for (Model_ChatList chatlist: modelChatLists){
                        if (model_user.getUid() != null && model_user.getUid().equals(chatlist.getId())
                                && model_user.getOnlineStatus().equals("Online")){
                            userList.add(model_user);
                            break;
                        }
                    }
                    adapterUserOnline = new Adapter_userOnline(ShowUserChatActivity.this, userList);
                    recycleruseronline.setAdapter(adapterUserOnline);
                    adapterUserOnline.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadChats() {

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Model_user model_user = ds.getValue(Model_user.class);
                     for (Model_ChatList chatlist: modelChatLists){
                         if (model_user.getUid() != null && model_user.getUid().equals(chatlist.getId())){
                             userList.add(model_user);
                             break;
                         }
                     }
                     adapterChatList = new Adapter_ChatList(ShowUserChatActivity.this, userList);
                     rcv_user_chat1.setAdapter(adapterChatList);
                     for (int i = 0; i < userList.size(); i++){
                         lastMessage(userList.get(i).getUid());
                     }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage ="default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    Model_chat chat = ds.getValue(Model_chat.class);
                    if (chat ==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) &&
                    chat.getSender().equals(userid) || chat.getReceiver().equals(userid)
                    && chat.getSender().equals(currentUser.getUid())){
                        theLastMessage = chat.getMessage();

                    }
                }
                adapterChatList.setLastMessageMap(userid, theLastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkStatusOnline(timestamp);

    }

    @Override
    protected void onResume() {
        checkStatusOnline("Online");
        super.onResume();
    }

        private void checkStatusOnline(String status){

        firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference("Users").child(MyUid);
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(MyUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("OnlineStatus", status);
            reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkStatusOnline("Online");
        super.onStart();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            MyUid = user.getUid();
        }else{
            startActivity(new Intent(ShowUserChatActivity.this, MainActivity.class));
            finish();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


