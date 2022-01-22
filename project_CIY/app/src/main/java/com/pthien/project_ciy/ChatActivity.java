package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.pthien.project_ciy.Adapter.Adapter_chat;
import com.pthien.project_ciy.Model.Model_chat;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.notification.Data;
import com.pthien.project_ciy.notification.Sender;
import com.pthien.project_ciy.notification.Token;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    RecyclerView rcv_chat;
    TextView name_chat, status_chat, blocktv;
    EditText text_chat;
    RelativeLayout layout_item_chat;
    ImageView send, avatar_chat, send_img, send_camera, send_Volum;
    ImageButton backc;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference UserDbRef;
    ValueEventListener seenListener;
    DatabaseReference userRefForseen;
    List<Model_chat> chatList;
    Adapter_chat adapter_chat;



//    APIService apiService;
    private RequestQueue requestQueue;
    private boolean notify = false;

    String hisUid;
    String MyUid;
    String hisImage;

    private static final  int CAMERA_REQUEST_CODE =100;
    private static final  int STORAGE_REQUEST_CODE =200;
    private static final  int IMAGE_PICK_CAMERA_CODE =300;
    private static final  int IMAGE_PICK_GALLERY_CODE =400;
    Uri image_url = null;
    String cameraPermissions[];
    String storagePermissions[];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toobarchat);
        setSupportActionBar(toolbar);



       cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        layout_item_chat = findViewById(R.id.layout_item_chat);
        rcv_chat = findViewById(R.id.rcv_chat);
        avatar_chat = findViewById(R.id.avatar_chat);
        name_chat = findViewById(R.id.name_chat);
        status_chat = findViewById(R.id.status_chat);
        send = findViewById(R.id.send_chat);
        text_chat = findViewById(R.id.text_chat);
        backc = findViewById(R.id.backc);
        blocktv = findViewById(R.id.tvblock);


        send_img = findViewById(R.id.image_chat);
        send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()){
                    requestStoragePermission();
                }else {
                    pickFromGallery();
                }
            }
        });
        send_camera = findViewById(R.id.camera_chat);
        send_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()){
                    requestCameraPermission();
                }else{
                    pickFromCamera();
                }
            }
        });

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
//      apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
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
      ischeckblock();
      readMessages();
      seenMessage();
    }

    private void ischeckblock() {
        UserDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(hisUid).child("BlockerUser").hasChild(MyUid)){
                    blocktv.setVisibility(View.VISIBLE);
                    blocktv.setText("Bạn đã bị chặn, Không thể nhắn tin !");
                }
                if (snapshot.child(MyUid).child("BlockerUser").hasChild(hisUid)){
                    blocktv.setVisibility(View.VISIBLE);
                    blocktv.setText("Bạn đã chặn, Không thể nhắn tin !");
                }
                else{
                    blocktv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });;
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
          hashMap.put("Type", "text");
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
    private void sendImageMessage(Uri image_url) throws IOException {
        notify = true;
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang gửi ảnh...");
        progressDialog.show();
        String timeStamp = ""+System.currentTimeMillis();
        String fileNameAndPath = "ChatsImage/" + "Post_"+timeStamp;


        Bitmap bitmap = null;
        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String dowloadUri = uriTask.getResult().toString();
                if (uriTask.isSuccessful()){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String , Object> hashMap = new HashMap<>();
                    hashMap.put("sender", MyUid);
                    hashMap.put("receiver", hisUid);
                    hashMap.put("message", dowloadUri);
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "image");
                    hashMap.put("isSeen", "false");

                    databaseReference.child("Chats").push().setValue(hashMap);

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Model_user user = snapshot.getValue(Model_user.class);
                            if (notify){
                                sendNotification(hisUid, user.getName(), "Đã gửi cho bạn một hình ảnh");
                            }
                            notify = false;
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
                Data data = new Data(
                        ""+MyUid,
                        ""+name+": " + ""+message,
                        "New Message",
                        ""+hisUid,
                        "ChatNotification",
                        R.drawable.logo2);
                Sender sender = new Sender(data, token.getToken());
                try {
                    JSONObject senderJsonObject = new JSONObject(new Gson().toJson(sender));
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                         Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("JSON_RESPONSE", "onResponse: "+error.toString());

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> header = new HashMap<>();
                            header.put("Content-Type", "application/json");
                            header.put("Authorization", "key=AAAAYihKrjI:APA91bF_v33z3LDbU8sX8BPZ07T3QJkr1dlhBAcSuHchjOTryG8l9alyphOgUPklDDgGabgWpjtTUit1X4-rtdYpZmuUCOlIkbNSXYPmPkM_YRPEDw8giZpFS_FKQ_MWEQaKkCw3lyjj");

                            return header;
                        }
                    };
                    requestQueue.add(jsonObjectRequest);
                }catch (JSONException e){
                    e.printStackTrace();
                }
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


    /////send image.///////////////////////
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
           if (resultCode == RESULT_OK){
               if ( requestCode == IMAGE_PICK_GALLERY_CODE){
                   image_url = data.getData();
                   try {
                       sendImageMessage(image_url);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                   try {
                       sendImageMessage(image_url);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writestorageAccepted){
                        pickFromCamera();

                    }else{
                        Toast.makeText(this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writestorageAccepted){
                        pickFromGallery();

                    }else{
                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "temp Description");

        image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_url);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent  = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }



}