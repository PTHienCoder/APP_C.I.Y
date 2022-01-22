package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.pthien.project_ciy.Adapter.Adapter_MoiGroup;
import com.pthien.project_ciy.Adapter.Adapter_TvGr;
import com.pthien.project_ciy.Adapter.Adapter_YCGr;
import com.pthien.project_ciy.Adapter.Adapter_userOnline;
import com.pthien.project_ciy.Model.Model_ChatList;
import com.pthien.project_ciy.Model.Model_group;
import com.pthien.project_ciy.Model.Model_user;
import com.pthien.project_ciy.Model.model_GroupList;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Details_GroupActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView name_gr, nametvgr, emailtvdb, ooop, noimg;
    Button moi;
    ImageView addcover;
    ImageButton backs, more;
    ImageView covergr;
    Uri image_url;

    List<Model_user> list;
    RecyclerView recycler_tvgr;
    Adapter_TvGr adapterTvGr;


    List<Model_user> list2;
    RecyclerView recycler_usermoi;
    Adapter_MoiGroup adapter_moiGroup;

    List<Model_user> list3;
    RecyclerView recycler_useryeucau;
    Adapter_YCGr adapterYcGr;



    ProgressDialog pd;
    FirebaseUser user;
    String idGr;
    View sheetView;
    LinearLayout viewGroup;

    List<model_GroupList> groupLists;
    DatabaseReference reference;
    Dialog dialog;
    Dialog dialog2;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_details__group);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseStorage storage1 = FirebaseStorage.getInstance();
        storageReference  = storage1.getReference();
        pd = new ProgressDialog(Details_GroupActivity.this);
        name_gr = findViewById(R.id.name_gr);
        nametvgr = findViewById(R.id.nametvgr);
        emailtvdb = findViewById(R.id.emailtvgr);
        ooop = findViewById(R.id.ooop);
        noimg = findViewById(R.id.textnoimg);
        moi = findViewById(R.id.moi);
        backs = findViewById(R.id.backgr);
        more = findViewById(R.id.btn_moregr);
        covergr = findViewById(R.id.covergr);
        addcover = findViewById(R.id.addcover);
        viewGroup = findViewById(R.id.bottom_sheetgr);





        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Details_GroupActivity.this, R.style.BottomsheetTheme);
        sheetView = LayoutInflater.from(Details_GroupActivity.this).inflate(R.layout.dialog_more_group, viewGroup);
        bottomSheetDialog.setContentView(sheetView);
        sheetView.findViewById(R.id.cn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2 = new Dialog(Details_GroupActivity.this);
                dialog2.setContentView(R.layout.model_yeucaugr);
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog2.setCancelable(false);
                dialog2.getWindow().getAttributes().windowAnimations = R.style.BottomsheetStyle;
                dialog2.show();
                dialog2.findViewById(R.id.out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                loaduserUCTG();
                bottomSheetDialog.dismiss();

            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        addcover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImagePic();
            }
        });

        Intent intent = getIntent();
         idGr = ""+intent.getStringExtra("idGr");
        groupLists = new ArrayList<>();
        Query eferen2 = FirebaseDatabase.getInstance().getReference("GroupList").child(idGr);
        eferen2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupLists.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    model_GroupList chatList = ds.getValue(model_GroupList.class);
                        groupLists.add(chatList);
                    Log.e("iiiiiiiiiiiiiio", chatList.getIduser());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog = new Dialog(Details_GroupActivity.this);
        dialog.setContentView(R.layout.modal_moitvgr);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomsheetStyle;
        dialog.findViewById(R.id.out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        String idGr = getIntent().getStringExtra("idGr");
         moi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Details_GroupActivity.this, SearchActivity.class);
                dialog.show();
                showusermoi();
            }
        });


        showinfGr();
        showuserGr();

    }

    private void loaduserUCTG() {
        list3 = new ArrayList<>();
        recycler_useryeucau = dialog2.findViewById(R.id.rcv_useryeucau);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Details_GroupActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_useryeucau.setLayoutManager(layoutManager);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list3.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_user model_user = ds.getValue(Model_user.class);
                    for (model_GroupList chatlist: groupLists){
                        if (chatlist.getCheck().equals("false") && model_user.getUid().equals(chatlist.getIduser())){
                            list3.add(model_user);
                            break;
                        }
                    }
                    adapterYcGr = new Adapter_YCGr(Details_GroupActivity.this, idGr, list3);
                    recycler_useryeucau.setAdapter(adapterYcGr);
                    adapterYcGr.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showusermoi() {
        list2 = new ArrayList<>();
        recycler_usermoi = dialog.findViewById(R.id.rcv_usermoi);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Details_GroupActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_usermoi.setLayoutManager(layoutManager);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_user model_user = ds.getValue(Model_user.class);
                    for (model_GroupList chatlist: groupLists){
                        if (!model_user.getUid().equals(chatlist.getIduser()) ||
                           model_user.getUid().equals(chatlist.getIduser()) &&
                            !chatlist.getCheck().equals("true") ||
                            model_user.getUid().equals(chatlist.getIduser()) &&
                            !chatlist.getCheck().equals("false")){
                            list2.add(model_user);
                            break;
                        }
                     }

                    adapter_moiGroup = new Adapter_MoiGroup(Details_GroupActivity.this,idGr,list2);
                    recycler_usermoi.setAdapter(adapter_moiGroup);
                    adapter_moiGroup.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showinfGr() {
        Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("idGr").equalTo(idGr);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String coverGr = ""+ ds.child("coverGr").getValue();
                    String descGr = ""+ ds.child("descGr").getValue();
                    String titleGr = ""+ ds.child("titleGr").getValue();
                    String idLeader = ""+ ds.child("idLeader").getValue();
                    String chedo = ""+ ds.child("chedo").getValue();

                    if (!idLeader.equals(user.getUid())){
                        addcover.setVisibility(View.GONE);
                        sheetView.findViewById(R.id.cn4).setVisibility(View.GONE);
                    }


                    if (!coverGr.equals("noImage")){
                        noimg.setVisibility(View.GONE);
                        try {
                            Picasso.get().load(coverGr).into(covergr);
                        } catch (Exception e) {

                        }
                    }
                    ooop.setText(chedo);
                    name_gr.setText(titleGr);
                    nametvgr.setText(titleGr);
                    emailtvdb.setText(descGr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showuserGr() {
        list = new ArrayList<>();
        recycler_tvgr = findViewById(R.id.rcv_tvgr);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Details_GroupActivity.this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycler_tvgr.setLayoutManager(layoutManager);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_user model_user = ds.getValue(Model_user.class);
                    for (model_GroupList chatlist: groupLists){
                        if (chatlist.getCheck().equals("true") && model_user.getUid().equals(chatlist.getIduser())){
                            list.add(model_user);
                            break;
                        }
                    }
                    Log.e("kkkkkkkk", model_user.getUid());
                    adapterTvGr = new Adapter_TvGr(Details_GroupActivity.this, list);
                    recycler_tvgr.setAdapter(adapterTvGr);
                    adapterTvGr.notifyDataSetChanged();
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
            covergr.setImageURI(image_url);
            uploadcover();
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadcover() {
            pd.setMessage("Uploading Post...");
             String filePathAndName = "Groups/" + "cover_" + idGr;
            if (covergr.getDrawable() != null){
                Bitmap bitmap = ((BitmapDrawable)covergr.getDrawable()).getBitmap();
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
                                    hashMap.put("coverGr",dowloadUri);
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                    ref.child(idGr).updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    pd.dismiss();
                                                    Toast.makeText(Details_GroupActivity.this, "Đã ảnh bìa.", Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(Details_GroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Details_GroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

    }


    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
        }else{
            startActivity(new Intent(Details_GroupActivity.this, MainActivity.class));
            finish();
        }
    }
}