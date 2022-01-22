package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.pthien.project_ciy.Adapter.Adapter_Group;
import com.pthien.project_ciy.Adapter.Adapter_thumoiGr;
import com.pthien.project_ciy.Model.Model_ChatList;
import com.pthien.project_ciy.Model.Model_group;
import com.pthien.project_ciy.Model.model_GroupList;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyGroupActivity extends AppCompatActivity {
    TextView btn_taogr, btn_moitv;
    private Dialog dialog;
    private Dialog dialog1;
    ImageView cover_gr;
    EditText name_gr, desc_gr;
    Button btn_ok, btn_cance;
    Uri img_url;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid;
    String storagePath = "Image_Group/";
    ProgressDialog pd;
    private ActionBar actionBar1;
    DatabaseReference databaseReference;
    private StorageReference storageReference;
    Adapter_Group adapterGroup;
    RecyclerView recycler_mygroup, getRecycler_groupother, rcv_thumoi;
    List<Model_group> modelGroups;
    List<Model_group> modelGroupists;
    List<Model_group> modelGroups1;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Spinner spinner;
    Adapter_thumoiGr adapter_thumoiGr;

    List<model_GroupList> groupLists;
    String myid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        actionBar1 = getSupportActionBar();

        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setDisplayHomeAsUpEnabled(true);
        actionBar1.setTitle("Nhóm của bạn");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        pd = new ProgressDialog(MyGroupActivity.this);
        groupLists = new ArrayList<>();

        Query ence = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("MyGroup");
        ence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupLists.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    model_GroupList chatList = ds.getValue(model_GroupList.class);
                    groupLists.add(chatList);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        storageReference = storage1.getReference();
        dialog1 = new Dialog(MyGroupActivity.this);
        dialog1.setContentView(R.layout.diaog_addgroup);
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(false);
        dialog1.getWindow().getAttributes().windowAnimations = R.style.BottomsheetStyle;
        btn_taogr = findViewById(R.id.btn_taoGr);
        spinner = dialog1.findViewById(R.id.spinnergr);
        list = new ArrayList<String>();
        list.add("Công khai");
        list.add("Riêng tư");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        btn_taogr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog1.show();
                cover_gr = dialog1.findViewById(R.id.cover_Gr);
                cover_gr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowImagePic();
                    }
                });

                name_gr = dialog1.findViewById(R.id.name_Gr);
                desc_gr = dialog1.findViewById(R.id.desc_Gr);
                btn_ok = dialog1.findViewById(R.id.btn_ok);
                btn_cance = dialog1.findViewById(R.id.btn_cancel);
                btn_cance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = name_gr.getText().toString().trim();
                        String desc = desc_gr.getText().toString().trim();
                        String chedo = spinner.getSelectedItem().toString().trim();
                        String leaderId = user.getUid();
                        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(desc)) {
                            Toast.makeText(MyGroupActivity.this, "Không được để khoản trống", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UploadCoverPhoto(title, desc, chedo, leaderId);
                        dialog1.dismiss();
                    }
                });
            }
        });

        dialog = new Dialog(MyGroupActivity.this);
        dialog.setContentView(R.layout.model_thumoi);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomsheetStyle;
        btn_moitv = findViewById(R.id.btn_moitv);
        btn_moitv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                showloimoi();

            }
        });
        dialog.findViewById(R.id.out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        checkUserStatus();

        MyGroup();
        OtherGroup();
    }


    private void showloimoi() {
        ////// Group list start /////////////////////////////////
        modelGroupists = new ArrayList<>();
        rcv_thumoi = dialog.findViewById(R.id.rcv_thumoigr);
        LinearLayoutManager layoutManagergr1 = new LinearLayoutManager(this);
        layoutManagergr1.setOrientation(RecyclerView.VERTICAL);
        rcv_thumoi.setLayoutManager(layoutManagergr1);
        DatabaseReference refgr1 = FirebaseDatabase.getInstance().getReference("Groups");
        refgr1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroupists.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_group model = ds.getValue(Model_group.class);
                    for (model_GroupList grlist: groupLists){
                        if (grlist.getCheck().equals("0")){
                            modelGroups1.add(model);
                            break;
                        }

                    }
                    adapter_thumoiGr = new Adapter_thumoiGr(MyGroupActivity.this, modelGroupists);
                    rcv_thumoi.setAdapter(adapter_thumoiGr);
                    adapter_thumoiGr.notifyDataSetChanged();
                    Log.e("hjhttttttttttttll", model.getIdGr());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void MyGroup() {
        ////// Group list start /////////////////////////////////
        modelGroups = new ArrayList<>();
        recycler_mygroup = findViewById(R.id.recyclerView_mygr);
        LinearLayoutManager layoutManagergr1 = new LinearLayoutManager(this);
        layoutManagergr1.setOrientation(RecyclerView.VERTICAL);
        recycler_mygroup.setLayoutManager(layoutManagergr1);
        Query refgr = FirebaseDatabase.getInstance().getReference("Groups");
        refgr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroups.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userid = ""+ ds.child("idLeader").getValue();
                    if (userid.equals(user.getUid())){
                        Model_group model = ds.getValue(Model_group.class);
                        modelGroups.add(model);
                    }

                    adapterGroup = new Adapter_Group(MyGroupActivity.this ,modelGroups);
                    recycler_mygroup.setAdapter(adapterGroup);
                    adapterGroup.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void OtherGroup() {
        ////// Group list start /////////////////////////////////
        modelGroups1 = new ArrayList<>();
        getRecycler_groupother = findViewById(R.id.recycler_grother);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(RecyclerView.VERTICAL);
        getRecycler_groupother.setLayoutManager(layout);
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroups1.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_group model = ds.getValue(Model_group.class);
                    for (model_GroupList grlist: groupLists){
                        if (grlist.getCheck().equals("true")){
                            modelGroups1.add(model);

                            break;
                        }

                    }

                    adapter_thumoiGr = new Adapter_thumoiGr(MyGroupActivity.this, modelGroups1);
                    getRecycler_groupother.setAdapter(adapter_thumoiGr);
                    adapter_thumoiGr.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void ShowImagePic() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 3)
                .start(this);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            img_url = result.getUri();
            cover_gr.setImageURI(img_url);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Intent intent = new Intent(this, MyProfileActivity.class);
            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void UploadCoverPhoto(String title, String desc, String chedo, String leaderId) {
        pd.setMessage("Đang tạo nhóm...");
        pd.show();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Groups/" + "cover_" + timeStamp;
        if (cover_gr.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable)cover_gr.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference ref = storageReference.child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String dowloadUri = uriTask.getResult().toString();
                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("idGr",timeStamp);
                                hashMap.put("titleGr", title);
                                hashMap.put("descGr", desc);
                                hashMap.put("idLeader", leaderId);
                                hashMap.put("coverGr", dowloadUri);
                                hashMap.put("chedo", chedo);
                                HashMap<String, Object> hashMap1 = new HashMap<>();
                                hashMap1.put("iduser", myid);
                                hashMap1.put("check", "true");
                                hashMap1.put("idGr", timeStamp);
                                DatabaseReference us = FirebaseDatabase.getInstance().getReference("Users");
                                us.child(uid).child("MyGroup").child(timeStamp).setValue(hashMap1);
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("GroupList");
                                ref1.child(timeStamp).child(leaderId).setValue(hashMap1);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(MyGroupActivity.this, "Tạo nhóm thành công.", Toast.LENGTH_SHORT).show();
                                                name_gr.setText("");
                                                desc_gr.setText("");
                                                cover_gr.setImageURI(null);
                                                img_url = null;

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(MyGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(MyGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("idGr",timeStamp);
            hashMap.put("titleGr", title);
            hashMap.put("descGr", desc);
            hashMap.put("idLeader", leaderId);
            hashMap.put("coverGr", "noImage");
            hashMap.put("chedo", chedo);

            HashMap<String, Object> hashMap2 = new HashMap<>();
            hashMap2.put("iduser",leaderId);
            hashMap2.put("check", "true");
            hashMap2.put("idGr",timeStamp);
            DatabaseReference us = FirebaseDatabase.getInstance().getReference("Users");
            us.child(uid).child("MyGroup").child(timeStamp).setValue(hashMap2);
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("GroupList");
            ref1.child(timeStamp).child(leaderId).setValue(hashMap2);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(MyGroupActivity.this, "Tạo nhóm thành công...", Toast.LENGTH_SHORT).show();
                            name_gr.setText("");
                            desc_gr.setText("");
                            cover_gr.setImageURI(null);
                            img_url = null;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(MyGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }




    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            uid = user.getUid();
        }else{
            startActivity(new Intent(MyGroupActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}