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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Add_Quention_Activity extends AppCompatActivity {
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
    ProgressDialog pd;



    String editTitle, editDesc, editImg, editdetais;
    Uri image_url = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__quention_);
        actionBar1 = getSupportActionBar();

        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setDisplayHomeAsUpEnabled(true);
        actionBar1.setSubtitle(email);

        spinner = (Spinner) findViewById(R.id.spinnerdata);



        pd = new ProgressDialog(this);

        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        storageReference1  = storage1.getReference();

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tvtitle = findViewById(R.id.tvtitle);
        tvcontent = findViewById(R.id.tvcontent);
        imagepost = findViewById(R.id.image_post_add);
        post_btn = findViewById(R.id.btn_post);

        Intent intent = getIntent();
        String isUpdatekey = ""+intent.getStringExtra("key");
        String editposstID = ""+intent.getStringExtra("editPostId");
        if (isUpdatekey.equals("EditPost")){
            actionBar1.setTitle("Cập nhật bài viết.");
            post_btn.setText("Cập nhật bài viết");
            updatePost(editposstID);
        }else {

            actionBar1.setTitle("Đặt Vấn đề.");
            post_btn.setText("Hoàn thành");

        }


        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tvtitle.getText().toString().trim();
                String desc = tvcontent.getText().toString().trim();
                String cate = spinner.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(title)){
                    Toast.makeText(Add_Quention_Activity.this, "Enter title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(desc)){
                    Toast.makeText(Add_Quention_Activity.this, "Enter description...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isUpdatekey.equals("EditPost")){
                    beginUpdatepost(title, desc, cate, editposstID);
                }else{
                    uploadData(title, desc, cate);

                }
            }
        });

        imagepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImagePic();
            }
        });


        fetchdata();


    }

    private void beginUpdatepost(String title, String desc, String cate, String editposstID) {
        pd.setMessage("Cập nhật bài viêt...");
        pd.show();
        if (!editImg.equals("noImage")){
            UpdateWasWithmg(title, desc, cate, editposstID);

        } else if (imagepost.getDrawable() !=null){
            UpdatewithNowimg(title, desc, cate, editposstID);

        }else{
            Updatewithoutimg(title, desc, cate,  editposstID);
        }

    }

    private void Updatewithoutimg(String title, String desc, String cate,  String editposstID) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",Uid);
        hashMap.put("uname", uname);
        hashMap.put("uimg", udp);
        hashMap.put("qTitle", title);
        hashMap.put("qDesc", desc);
        hashMap.put("qCate", cate);
        hashMap.put("qImage", "noImage");
        hashMap.put("check","false");

        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Questions");
        ref3.child(editposstID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(Add_Quention_Activity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void UpdatewithNowimg(final String title, final String desc, final String cate, final String editposstID) {

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
                    hashMap.put("uname", uname);
                    hashMap.put("uimg", udp);
                    hashMap.put("qTitle", title);
                    hashMap.put("qDesc", desc);
                    hashMap.put("qCate", cate);
                    hashMap.put("qImage", dowloadUri);
                    hashMap.put("qTime",timestamp);
                    hashMap.put("check","false");

                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Questions");
                    ref3.child(editposstID).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(Add_Quention_Activity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void UpdateWasWithmg(final String title, final String desc, final String cate, final String editposstID) {
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
                            hashMap.put("uname", uname);
                            hashMap.put("uimg", udp);
                            hashMap.put("qTitle", title);
                            hashMap.put("qDesc", desc);
                            hashMap.put("qCate", cate);
                            hashMap.put("qImage", dowloadUri);
                            hashMap.put("qTime",timestamp);
                            hashMap.put("check","false");

                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Question");
                            ref3.child(editposstID).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(Add_Quention_Activity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }



    private void updatePost(String editposstID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Questions");
        Query udquery = reference.orderByChild("qId").equalTo(editposstID);
        udquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    editTitle = ""+ds.child("qTitle").getValue();
                    editDesc = ""+ds.child("qDesc").getValue();
                    editImg = ""+ds.child("qImage").getValue();
                }
                tvtitle.setText(editTitle);
                tvcontent.setText(editDesc);
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



    private void uploadData(String title, String desc,  String cate) {
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
                                hashMap.put("uname", uname);
                                hashMap.put("uimg", udp);
                                hashMap.put("qId", timeStamp);
                                hashMap.put("qTitle", title);
                                hashMap.put("qDesc", desc);
                                hashMap.put("qCate", cate);
                                hashMap.put("qImage", dowloadUri);
                                hashMap.put("qTime",timeStamp);
                                hashMap.put("check","false");

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(Add_Quention_Activity.this, "Đăng tải vấn đề.", Toast.LENGTH_SHORT).show();
                                                tvtitle.setText("");
                                                tvcontent.setText("");
                                                imagepost.setImageURI(null);
                                                image_url = null;
                                                onBackPressed();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",Uid);
            hashMap.put("uname", uname);
            hashMap.put("uimg", udp);
            hashMap.put("qId", timeStamp);
            hashMap.put("qTitle", title);
            hashMap.put("qDesc", desc);
            hashMap.put("qCate", cate);
            hashMap.put("qImage", "noImage");
            hashMap.put("qTime",timeStamp);
            hashMap.put("check","false");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(Add_Quention_Activity.this, "Đăng tải vấn đề.", Toast.LENGTH_SHORT).show();
                            tvtitle.setText("");
                            tvcontent.setText("");
                            imagepost.setImageURI(null);
                            image_url = null;
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Add_Quention_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }





}