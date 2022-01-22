package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_chat;
import com.pthien.project_ciy.Model.Model_chat;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.notification.APIService;
import com.pthien.project_ciy.notification.Client;
import com.pthien.project_ciy.notification.Data;
import com.pthien.project_ciy.notification.Response;
import com.pthien.project_ciy.notification.Sender;
import com.pthien.project_ciy.notification.Token;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    RecyclerView rcv_chat;
    TextView name_chat, status_chat;
    EditText text_chat;
    RelativeLayout layout_item_chat;
    ImageView send, avatar_chat;
    ImageButton backc;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference UserDbRef;
    ValueEventListener seenListener;
    DatabaseReference userRefForseen;
    List<Model_chat> chatList;
    Adapter_chat adapter_chat;



    APIService apiService;
    boolean notify = false;

    String hisUid;
    String MyUid;
    String hisImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toobarchat);
        setSupportActionBar(toolbar);



        layout_item_chat = findViewById(R.id.layout_item_chat);
        rcv_chat = findViewById(R.id.rcv_chat);
        avatar_chat = findViewById(R.id.avatar_chat);
        name_chat = findViewById(R.id.name_chat);
        status_chat = findViewById(R.id.status_chat);
        send = findViewById(R.id.send_chat);
        text_chat = findViewById(R.id.text_chat);
        backc = findViewById(R.id.backc);
        backc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ChatActivity.this, ShowUserChatActivity.class));
                onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        rcv_chat.setHasFixedSize(true);
        rcv_chat.setLayoutManager(linearLayoutManager);


        //create api service
      apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
         Intent intent = getIntent();
         hisUid = intent.getStringExtra("hisUid");
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        UserDbRef = firebaseDatabase.getReference("Users");

        Query userQuery = UserDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = "" +ds.child("name").getValue();
                    hisImage = "" +ds.child("image").getValue();
                    String TyingTo = "" +ds.child("TyingTo").getValue();
                     if (TyingTo.equals(MyUid)){
                         status_chat.setText("Typing...");
                     }else{
                         String onlinestatus = "" +ds.child("OnlineStatus").getValue();
                         if (onlinestatus.equals("Online")){
                             status_chat.setText(onlinestatus);
                         }else{
                             Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                             cal.setTimeInMillis(Long.parseLong(onlinestatus));
                             String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                             status_chat.setText("Last seen at: " +datetime);
                         }

                     }

                    name_chat.setText(name);

                    try {
                        Picasso.get().load(hisImage)
                                .placeholder(R.drawable.ic_face)
                                .into(avatar_chat);
                    } catch (Exception e){


                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      send.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              notify = true;
              String message = text_chat.getText().toString().trim();
              if (TextUtils.isEmpty(message)){
                  Toast.makeText(ChatActivity.this, "Cannot send the empty message...", Toast.LENGTH_SHORT).show();
              }else{
                  sendMessage(message);

              }
              text_chat.setText("");

          }
      });

      text_chat.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (s.toString().trim().length() == 0){
                  checkTyingOnline("noOne");
              }else{
                  checkTyingOnline(hisUid);
              }
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });

      readMessages();
      seenMessage();
    }

    private void seenMessage() {
        userRefForseen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Model_chat chat = ds.getValue(Model_chat.class);
                    if (chat.getReceiver().equals(MyUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                int pLikes = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    Model_chat model_chat = ds.getValue(Model_chat.class);
                    if (model_chat.getReceiver().equals(MyUid) && model_chat.getSender().equals(hisUid)
                    || model_chat.getReceiver().equals(hisUid) && model_chat.getSender().equals(MyUid)){
                        chatList.add(model_chat);
                        pLikes++;
                    }
                    adapter_chat = new Adapter_chat(ChatActivity.this, chatList, hisImage);
                    adapter_chat.notifyDataSetChanged();
                    rcv_chat.setAdapter(adapter_chat);
                }
                Log.e("adhakdja", String.valueOf(pLikes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
          hashMap.put("sender",  MyUid);
          hashMap.put("receiver", hisUid);
          hashMap.put("message", message);
          hashMap.put("timestamp", timestamp);
          hashMap.put("isSeen", false);
          databaseReference.child("Chats").push().setValue(hashMap);

//          String msg = message;
          final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
          database.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  Model_user user = snapshot.getValue(Model_user.class);
                  if (notify){
                      sendNotification(hisUid, user.getName(), message);
                  }
                  notify =false;
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });

       final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatsList")
                .child(MyUid).child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatsList")
                .child(hisUid).child(MyUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(MyUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(final String hisUid,final String name,final String message) {
    DatabaseReference allTonkens = FirebaseDatabase.getInstance().getReference("Tokens");
    Query query = allTonkens.orderByKey().equalTo(hisUid);
    query.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds: snapshot.getChildren()){
                Token token = ds.getValue(Token.class);
                Data data = new Data(MyUid, name+":"+message, "New Message", hisUid, R.drawable.logo2);
                Sender sender = new Sender(data, token.getToken());
                apiService.senNotification(sender).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT ).show();
                    }
                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

          MyUid = user.getUid();
        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkStatusOnline(timestamp);
        checkTyingOnline("noOne");

        userRefForseen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkStatusOnline("Online");
        super.onResume();
    }

    private void checkStatusOnline(String status){
        firebaseDatabase = FirebaseDatabase.getInstance();
        UserDbRef = firebaseDatabase.getReference("Users").child(MyUid);
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(MyUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("OnlineStatus", status);
        UserDbRef.updateChildren(hashMap);
    }

    private void checkTyingOnline(String status){
        firebaseDatabase = FirebaseDatabase.getInstance();
        UserDbRef = firebaseDatabase.getReference("Users").child(MyUid);
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(MyUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("TyingTo", status);
        UserDbRef.updateChildren(hashMap);
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        checkStatusOnline("Online");
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//         menu.findItem(R.id.action_search).setVisible(false);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_logout){
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}