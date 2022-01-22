package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    private ActionBar actionBar1;
    Spinner spinner;
    DatabaseReference dbref;
    EditText tvtitle, tvcontent, tvdetails;
    ImageView imagepost;
    Button post_btn;
    ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    private StorageReference storageReference1;
    String uname, email, Uid, udp;
    LinearLayout themha;
    ProgressDialog pd;
    private RequestQueue requestQueue;
  private String Url = "https://fcm.googleapis.com/fcm/send";


    String editTitle, editDesc, editImg, editdetais;
    Uri image_url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        actionBar1 = getSupportActionBar();

        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setDisplayHomeAsUpEnabled(true);
        actionBar1.setSubtitle(email);

        spinner = (Spinner) findViewById(R.id.spinnerdata);

       requestQueue = Volley.newRequestQueue(getApplicationContext());
//       Button btnadd = (Button) findViewById(R.id.btnadd);

        pd = new ProgressDialog(this);

        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        storageReference1  = storage1.getReference();

//        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        dbref = FirebaseDatabase.getInstance().getReference("spinnerdata");

        list=new ArrayList<String>();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        checkUserStatus();



        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    uname = ""+ds.child("name").getValue();
                    udp = ""+ds.child("image").getValue();
                    Uid = ""+ds.child("uid").getValue();
                    email = ""+ds.child("email").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tvtitle = findViewById(R.id.tvtitle);
        tvcontent = findViewById(R.id.tvcontent);
        tvdetails = findViewById(R.id.Details);
        imagepost = findViewById(R.id.image_post_add);
        post_btn = findViewById(R.id.btn_post);
        themha = findViewById(R.id.themha);

         Intent intent = getIntent();
         String isUpdatekey = ""+intent.getStringExtra("key");
         String editposstID = ""+intent.getStringExtra("editPostId");
       if (isUpdatekey.equals("EditPost")){
           actionBar1.setTitle("Cập nhật bài viết.");
           post_btn.setText("Cập nhật bài viết");
           updatePost(editposstID);
       }else {

           actionBar1.setTitle("Đặt vấn đề.");
           post_btn.setText("Hoàn thành");

       }


        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tvtitle.getText().toString().trim();
                String desc = tvcontent.getText().toString().trim();
                String cate = spinner.getSelectedItem().toString().trim();
                String content = tvdetails.getText().toString().trim();

                if (TextUtils.isEmpty(title)){
                    Toast.makeText(AddPostActivity.this, "Enter title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(desc)){
                    Toast.makeText(AddPostActivity.this, "Enter description...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content.equals("")){
                  content = "None";
                }
                if(isUpdatekey.equals("EditPost")){
                    beginUpdatepost(title, desc, cate, content, editposstID);
                }else{
                    uploadData(title, desc, cate, content);
                }
            }
        });

        imagepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImagePic();
            }
        });
        themha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImagePic();
            }
        });

//
//                btnadd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                insertdata();
//            }
//        });
        fetchdata();


    }

    private void beginUpdatepost(String title, String desc, String cate, String content, String editposstID) {
         pd.setMessage("Cập nhật bài viêt...");
         pd.show();
         if (!editImg.equals("noImage")){
             UpdateWasWithmg(title, desc, cate, content, editposstID);

         } else if (imagepost.getDrawable() !=null){
             UpdatewithNowimg(title, desc, cate, content, editposstID);

             }else{
             Updatewithoutimg(title, desc, cate, content,  editposstID);
         }

    }

    private void Updatewithoutimg(String title, String desc, String cate, String content,  String editposstID) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",Uid);
        hashMap.put("uName", uname);
        hashMap.put("uEmail", email);
        hashMap.put("uDp", udp);
        hashMap.put("pTitle", title);
        hashMap.put("pDesc", desc);
        hashMap.put("pCate", cate);
        hashMap.put("pDetails", content);
        hashMap.put("pImage", "noImage");

        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Posts");
        ref3.child(editposstID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void UpdatewithNowimg(final String title, final String desc, final String cate, final String content, final String editposstID) {

        String timestamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_"+timestamp;

        Bitmap bitmap = ((BitmapDrawable)imagepost.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String dowloadUri = uriTask.getResult().toString();
                if (uriTask.isSuccessful()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid",Uid);
                    hashMap.put("uName", uname);
                    hashMap.put("uEmail", email);
                    hashMap.put("uDp", udp);
                    hashMap.put("pTitle", title);
                    hashMap.put("pDesc", desc);
                    hashMap.put("pDetails", content);
                    hashMap.put("pCate", cate);
                    hashMap.put("pImage", dowloadUri);

                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Posts");
                    ref3.child(editposstID).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(AddPostActivity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void UpdateWasWithmg(final String title, final String desc, final String cate, final String content, final String editposstID) {
        StorageReference mPicRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImg);
        mPicRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String filePathAndName = "Posts/" + "post_"+timestamp;

                Bitmap bitmap = ((BitmapDrawable) imagepost.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String dowloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid",Uid);
                            hashMap.put("uName", uname);
                            hashMap.put("uEmail", email);
                            hashMap.put("uDp", udp);
                            hashMap.put("pTitle", title);
                            hashMap.put("pDesc", desc);
                            hashMap.put("pCate", cate);
                            hashMap.put("pDetails", content);
                            hashMap.put("pImage", dowloadUri);

                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Posts");
                            ref3.child(editposstID).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(AddPostActivity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void updatePost(String editposstID) {
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
       Query udquery = reference.orderByChild("pId").equalTo(editposstID);
       udquery.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot ds: snapshot.getChildren()){
                   editTitle = ""+ds.child("pTitle").getValue();
                   editDesc = ""+ds.child("pDesc").getValue();
                   editImg = ""+ds.child("pImage").getValue();
                    editdetais = ""+ds.child("pDetails").getValue();
               }
               tvtitle.setText(editTitle);
               tvcontent.setText(editDesc);
               tvdetails.setText(editdetais);
               if (!editImg.equals("noImage")){
                   try {
                       Picasso.get().load(editImg).into(imagepost);
                   }catch (Exception e){

                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }
    private void uploadData(String title, String desc,  String cate, String content) {
        pd.setMessage("Uploading Post...");
        pd.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;
        if (imagepost.getDrawable() != null){

            Bitmap bitmap = ((BitmapDrawable)imagepost.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = storageReference1.child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String dowloadUri = uriTask.getResult().toString();
                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",Uid);
                                hashMap.put("uName", uname);
                                hashMap.put("uEmail", email);
                                hashMap.put("uDp", udp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pDesc", desc);
                                hashMap.put("pCate", cate);
                                hashMap.put("pImage", dowloadUri);
                                hashMap.put("pTime",timeStamp);
                                hashMap.put("pDetails", content);
                                hashMap.put("check","false");

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Upload Post.", Toast.LENGTH_SHORT).show();
                                                tvtitle.setText("");
                                                tvcontent.setText("");
                                                imagepost.setImageURI(null);
                                                image_url = null;
                                                prepareNotification(
                                                        "" +timeStamp,
                                                        ""+uname + "vừa mới thêm một ý tưởng mới",
                                                        ""+title+"\n"+desc,
                                                        "PostNotification",
                                                        "POST");
                                                onBackPressed();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",Uid);
            hashMap.put("uName", uname);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", udp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("pDesc", desc);
            hashMap.put("pCate", cate);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("pDetails", content);
            hashMap.put("check","false");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, "Upload Post.", Toast.LENGTH_SHORT).show();
                            tvtitle.setText("");
                            tvcontent.setText("");
                            imagepost.setImageURI(null);
                            image_url = null;
                            prepareNotification(
                                    "" +timeStamp,
                                    ""+uname +": " + "vừa mới thêm một ý tưởng mới",
                                    ""+title+ ": \n"+desc,
                                    "PostNotification",
                                    "POST");
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }


    private void prepareNotification(String pId, String title, String desc, String NotificationsType, String notificationTopic){
        String NOTIFICATION_TOPIC = "/topics/" + notificationTopic;
        String NOTIFICATION_TITLE = title;
        String NOTIFICATION_DESC = desc;
        String NOTIFICATION_TYPE = NotificationsType;


        JSONObject notificationJo = new JSONObject();
        JSONObject noticationBodyJo = new JSONObject();
       try {
           noticationBodyJo.put("notificationType", NOTIFICATION_TYPE);
           noticationBodyJo.put("sender", Uid);
           noticationBodyJo.put("pId", pId);
           noticationBodyJo.put("pTitle", NOTIFICATION_TITLE);
           noticationBodyJo.put("pDesc", NOTIFICATION_DESC);

           notificationJo.put("to", NOTIFICATION_TOPIC);
           notificationJo.put("data", noticationBodyJo);


       }catch (JSONException e) {
           Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
       }
       sendPostNotification(notificationJo);
    }

    private void sendPostNotification(JSONObject notificationJo) {
        JsonObjectRequest request = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
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

        requestQueue.add(request);

    }

    private void ShowImagePic() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(5, 4)
                .start(this);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image_url = result.getUri();
            imagepost.setImageURI(image_url);        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }        super.onActivityResult(requestCode, resultCode, data);
    }



    public void fetchdata() {
        listener = dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                    list.add(ds.child("name").getValue().toString());
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
             email = user.getEmail();
             Uid = user.getUid();
        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }







//        public void insertdata() {
//
//        EditText t1 = (EditText) findViewById(R.id.t1);
//        String data = t1.getText().toString().trim();
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("id","KO");
//            hashMap.put("Name", data);
//            hashMap.put("Image", "noImage");
//            Random gn = new Random();
//            int in = gn.nextInt();
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Cates");
//            ref.child(data).setValue(hashMap)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            pd.dismiss();
//                            Toast.makeText(AddPostActivity.this,  "Post cates", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    pd.dismiss();
//                    Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    }
//    private void ShowImagePicDialog() {
//        String option[] = {"Camea", "Gallery"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Pick Image From");
//        builder.setItems(option, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0){
//                    if (!checkCameraPermission()){
//                        requestCameraPermission();
//                    }else{
//                        pickFromCamera();
//                    }
//                }else if (which ==1){
//                    if (!checkStoragePermission()){
//                        requestStoragePermission();
//                    }else {
//                        pickFromGallery();
//                    }
//                }
//            }
//
//
//        });
//        builder.create().show();
//    }
//    private boolean checkStoragePermission(){
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                == (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }
//    private void requestStoragePermission(){
//        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
//    }
//
//    private boolean checkCameraPermission(){
//
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                == (PackageManager.PERMISSION_GRANTED);
//
//        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == (PackageManager.PERMISSION_GRANTED);
//        return result && result1;
//    }
//    private void requestCameraPermission(){
//        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        switch (requestCode){
//            case CAMERA_REQUEST_CODE:{
//                if(grantResults.length>0){
//                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if (cameraAccepted && writestorageAccepted){
//                        pickFromCamera();
//
//                    }else{
//                        Toast.makeText(this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            break;
//            case STORAGE_REQUEST_CODE:{
//                if(grantResults.length>0){
//                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if (writestorageAccepted){
//                        pickFromGallery();
//
//                    }else{
//                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }
//            break;
//        }
//    }
//
//    private void pickFromCamera() {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, "Temp pick");
//        values.put(MediaStore.Images.Media.DESCRIPTION, "temp Description");
//
//        image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_url);
//        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
//    }
//    private void pickFromGallery() {
//        Intent galleryIntent  = new Intent(Intent.ACTION_PICK);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
//    }
//
//
//

}