package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;

public class Add_VideoActivity extends AppCompatActivity {
    DatabaseReference dbref;
    ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Button btn_postvd;
    private ActionBar actionBar1;
    LinearLayout themvd;
    RelativeLayout luvd;
    EditText tvtitle, tvcontent;
    ProgressDialog pd;

    VideoView video_post_add;
    Spinner spinner;
    DatabaseReference databaseReference;
    private static final int PICK_VIDEO_GALLERY_CODE = 101;
    private static final int PICK_VIDEO_CAMERA_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 103;
    private static final int STORAGE_REQUEST_CODE = 104;
    private String[] cameraPermissions;
    private Uri videoUri;
    String uname, email, Uid, udp, myid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__video);

        actionBar1 = getSupportActionBar();
        myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("uid").equalTo(myid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    uname = ""+ds.child("name").getValue();
                    udp = ""+ds.child("image").getValue();
                    Uid = ""+ds.child("uid").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setDisplayHomeAsUpEnabled(true);
        actionBar1.setTitle("Add Video");
        video_post_add = findViewById(R.id.video_post_add);
        btn_postvd = findViewById(R.id.btn_postvd);
        tvtitle = findViewById(R.id.tvtitle);
        tvcontent = findViewById(R.id.tvcontent);
        themvd = findViewById(R.id.themvd);
        luvd = findViewById(R.id.luvd);
        if (videoUri == null){
            luvd.setVisibility(View.GONE);
        }else{
            luvd.setVisibility(View.VISIBLE);
        }
        btn_postvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tvtitle.getText().toString().trim();
                String desc = tvcontent.getText().toString().trim();
                String cate = spinner.getSelectedItem().toString().trim();
                if (TextUtils.isEmpty(title)){
                    Toast.makeText(Add_VideoActivity.this, "Enter title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(desc)){
                    Toast.makeText(Add_VideoActivity.this, "Enter desc...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (videoUri == null){
                    Toast.makeText(Add_VideoActivity.this, "Chưa có video...", Toast.LENGTH_SHORT).show();
                    return;
                }
              Postvd(title, desc, cate);
            }
        });


        spinner = (Spinner) findViewById(R.id.spinnerdata);
        dbref = FirebaseDatabase.getInstance().getReference("spinnerdata");
        themvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};



        list=new ArrayList<String>();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        fetchdata();
        pd = new ProgressDialog(this);
    }

    private void Postvd(String title, String desc, String cate) {
        pd.setMessage("Uploading Video...");
        pd.show();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Postvd/" + "postvd_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference(filePathAndName);
        ref.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri dowloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid",Uid);
                            hashMap.put("uName","" + uname);
                            hashMap.put("uDp","" +  udp);
                            hashMap.put("pId","" +  timeStamp);
                            hashMap.put("pTitle","" +  title);
                            hashMap.put("pDesc","" +  desc);
                            hashMap.put("pCate","" +  cate);
                            hashMap.put("pvd","" +  dowloadUri);
                            hashMap.put("pTime","" + timeStamp);
                            DatabaseReference us = FirebaseDatabase.getInstance().getReference("VideoPost");
                            us.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(Add_VideoActivity.this, "Upload video.", Toast.LENGTH_SHORT).show();
                                            tvtitle.setText("");
                                            tvcontent.setText("");
                                            videoUri = null;
                                            if (videoUri == null){
                                                luvd.setVisibility(View.GONE);
                                            }else{
                                                luvd.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(Add_VideoActivity.this, "Upload video.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Add_VideoActivity.this, "Upload video.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    private void choose() {
        String option[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick video from");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }
                }else if (which ==1){
                    pickFromGallery();
                }
            }

        });
        builder.create().show();



    }

    private void pickFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(cameraIntent, PICK_VIDEO_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent intent  = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO_GALLERY_CODE);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == PICK_VIDEO_GALLERY_CODE && resultCode == RESULT_OK){
                videoUri = data.getData();
                Log.e("ádbakjdha", String.valueOf(videoUri));
                setvideo();
            }else if (requestCode == PICK_VIDEO_CAMERA_CODE && resultCode == RESULT_OK){
                videoUri = data.getData();
                setvideo();
                Log.e("ádbakjdha", String.valueOf(videoUri));
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setvideo() {
        if (videoUri == null){
            luvd.setVisibility(View.GONE);
        }else{
            luvd.setVisibility(View.VISIBLE);
        }
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video_post_add);
        video_post_add.setMediaController(mediaController);
        video_post_add.setVideoURI(videoUri);
        video_post_add.requestFocus();
        video_post_add.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video_post_add.pause();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}