package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.pthien.project_ciy.Adapter.Adapter_Post;
import com.pthien.project_ciy.Model.Model_ChatList;
import com.pthien.project_ciy.Model.Model_chat;
import com.pthien.project_ciy.Model.Model_post;
import com.pthien.project_ciy.fragement.Model_Friends;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {
    TextView nametv, emailtv, phonetv, flow, flowing;
    ImageView avatar, coverimg, avatardl, coverimgdl;
    private Button fabb;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    String storagePath  = "Users_Profile_Cover_Imgs/";
    private ActionBar actionBar;

    private BottomSheetDialog bottomSheetDialog;
    Dialog dialog;
    RecyclerView rcv_my_post;
    List<Model_post> postList;
    Adapter_Post adapter_post;
    String uid;

    private LinearLayoutManager layoutManagermp;
   int flower =0;
    int flowingg =0;

    String Myname, email;
    ProgressDialog pd;
    Uri image_url;
    String profileOrcoverphoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");


        checkUserStatus();
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(email);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference  = storage.getReference();

        avatar = findViewById(R.id.avatar);
        coverimg = findViewById(R.id.cover);
        emailtv = findViewById(R.id.emailtv);
        phonetv = findViewById(R.id.phonetv);
        nametv = findViewById(R.id.nametv);
        fabb = findViewById(R.id.fabb);
        flow = findViewById(R.id.flow);
        flowing = findViewById(R.id.flowing);
        DatabaseReference friends = firebaseDatabase.getReference("Friends");
        friends.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    flower = (int) snapshot.getChildrenCount();
                    flow.setText(Integer.toString(flower));
                }else{
                    flow.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference friends22= firebaseDatabase.getReference("Friends");
        friends22.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        if (ds.hasChild(user.getUid())) {
                            flowingg++;
                        }
                    }
                       flowing.setText(Integer.toString(flowingg));
                     }

                        else{
                        flow.setText("0");
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        pd = new ProgressDialog(MyProfileActivity.this);


        dialog = new Dialog(MyProfileActivity.this);
        dialog.setContentView(R.layout.dialog_layout);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
//        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animaton;
        dialog.getWindow().getAttributes().horizontalMargin = 10;
        avatardl = dialog.findViewById(R.id.avatar);
        coverimgdl = dialog.findViewById(R.id.cover);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    Myname = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String image = ""+ ds.child("image").getValue();
                    String cover = ""+ ds.child("cover").getValue();
                    actionBar.setTitle(Myname);

                    nametv.setText(Myname);
                    emailtv.setText(email);
                    phonetv.setText(phone);
//
//                    try {
//                        Picasso.get().load(image).into(avatar);
//                    }catch (Exception e){
//                        Picasso.get().load(R.drawable.ic_addcamera_black).into(avatar);
//                    }

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_face).into(avatar);
                    }catch (Exception e){

                    }

                    try {
                        Picasso.get().load(cover).into(coverimg);
                    }catch (Exception e){

                    }

                    /// add data dialog
                    try {
                        Picasso.get().load(image).into(avatardl);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_addcamera_black).into(avatardl);
                    }
                    try {
                        Picasso.get().load(cover).into(coverimgdl);
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fabb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog = new BottomSheetDialog(MyProfileActivity.this, R.style.BottomsheetTheme);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                        (ViewGroup) findViewById(R.id.bottom_sheet));
                sheetView.findViewById(R.id.cn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowImagePic();
                        profileOrcoverphoto = "image";
                        bottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.cn3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowImagePic2();
                        profileOrcoverphoto = "cover";
                        bottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.cn4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNamePhoneUpdateDialog("name");
                        bottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.cn5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNamePhoneUpdateDialog("phone");
                        bottomSheetDialog.dismiss();
                    }
                });
                sheetView.findViewById(R.id.cn6).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNamePhoneUpdateDialog("email");
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }

        });

       ////rcv post
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");



        rcv_my_post = findViewById(R.id.rcv_my_post);

        postList = new ArrayList<>();
        adapter_post = new Adapter_Post(MyProfileActivity.this, postList);
        layoutManagermp = new LinearLayoutManager(this);
        rcv_my_post.setHasFixedSize(true);
        layoutManagermp.setStackFromEnd(true);
        layoutManagermp.setReverseLayout(true);
        rcv_my_post.setLayoutManager(layoutManagermp);
        rcv_my_post.setAdapter(adapter_post);


        LoadmMypost();

    }

    private void LoadmMypost() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query rs = ref.orderByChild("uid").equalTo(uid);
        rs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_post model_post = ds.getValue(Model_post.class);
                    postList.add(model_post);
                    adapter_post.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void SearchmyPosst(String SearchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_post model_post = ds.getValue(Model_post.class);
                    if (model_post.getpTitle().toLowerCase().contains(SearchQuery.toLowerCase()) ||
                            model_post.getpDesc().toLowerCase().contains(SearchQuery.toLowerCase()) ||
                            model_post.getpCate().toLowerCase().contains(SearchQuery.toLowerCase())) {
                        postList.add(model_post);
                    }
                    rcv_my_post.setAdapter(adapter_post);
                    adapter_post.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void showNamePhoneUpdateDialog(String key) {
        dialog.show();
        EditText editDl = dialog.findViewById(R.id.tvcc);
        TextView tvdl = dialog.findViewById(R.id.tvemail);
        tvdl.setText("Nhập"+" " + key + " " +" mới");
        editDl.setHint("Nhập " +" "+key+" "+ " mới...");
         Button ok = dialog.findViewById(R.id.btn_ok);
         ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = editDl.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(MyProfileActivity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(MyProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (key.equals("name")){

                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Questions");
                        Query query2 = ref2.orderByChild("uid").equalTo(uid);
                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    String child = ds.getKey();
                                    snapshot.getRef().child(child).child("uname").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                     DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                     Query query = ref.orderByChild("uid").equalTo(uid);
                     query.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             for (DataSnapshot ds: snapshot.getChildren()){
                                 String child = ds.getKey();
                                 snapshot.getRef().child(child).child("uName").setValue(value);
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });

                    }
                }

                dialog.dismiss();
            }
        });
        Button cancel = dialog.findViewById(R.id.btn_cancel);
           cancel.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dialog.dismiss();
                   Toast.makeText(MyProfileActivity.this, "Cancel", Toast.LENGTH_SHORT).show();

               }
           });

    }
    private void ShowImagePic2() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(5, 4)
                .start(this);
    }
    private void ShowImagePic() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .start(this);
    }



    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                image_url = result.getUri();
                avatar.setImageURI(image_url);
                Log.e("lllll", image_url.toString());
                UploadProfileCoverPhoto(image_url);
        }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Intent intent = new Intent(this, MyProfileActivity.class);
                startActivity(intent);
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadProfileCoverPhoto(Uri url) {
        pd.show();
        String filePathAndName = storagePath+ "" +profileOrcoverphoto+ ""+user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(url)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri dowloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> result = new HashMap<>();
                            result.put(profileOrcoverphoto, dowloadUri.toString());
                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(MyProfileActivity.this, "Image Update...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(MyProfileActivity.this, "Error Updating Image...", Toast.LENGTH_SHORT).show();
                                }
                            });

                            if (profileOrcoverphoto.equals("image")){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                Query query = ref.orderByChild("uid").equalTo(uid);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            String child = ds.getKey();
                                            snapshot.getRef().child(child).child("uDp").setValue(dowloadUri.toString());
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //////
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Questions");
                                Query query2 = ref2.orderByChild("uid").equalTo(uid);
                                query2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            String child = ds.getKey();
                                            snapshot.getRef().child(child).child("uimg").setValue(dowloadUri.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                            }

                        }else{
                            pd.dismiss();
                            Toast.makeText(MyProfileActivity.this, " Some error occure", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(MyProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }




    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
              uid = user.getUid();
        }else{
            startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
            finish();
        }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item=  menu.findItem(R.id.action_search);
        SearchView searchViewmp = (SearchView) item.getActionView();
        searchViewmp.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    SearchmyPosst(query);
                }else{
                    LoadmMypost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    SearchmyPosst(query);
                }else{
                    LoadmMypost();
                }
                return false;
            }
        });



        return super.onCreateOptionsMenu(menu);
    }

    //        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
//        builder.setTitle("Update"+" "+ key);
//        LinearLayout linearLayout = new LinearLayout(MyProfileActivity.this);
//        linearLayout.setOrientation(linearLayout.VERTICAL);
//        linearLayout.setPadding(15, 15, 15, 15 );
//        EditText editText = new EditText(MyProfileActivity.this);
//        editText.setHint("Enter "+ key);
//        linearLayout.addView(editText);
//        builder.setView(linearLayout);

//    private void showEditprofileDialog() {
//        String option[] = {"Edit Avatar Picture", "Edit Daemon Picture", "Edit Name", "Edit Phone", "Edit Email"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
//        builder.setTitle("Choose Action");
//        builder.setItems(option, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0){
//                    pd.setMessage("Cập nhật hình đại diện");
//                    profileOrcoverphoto = "image";
//                    ShowImagePic();
//
//                }else if (which ==1){
//                    pd.setMessage("Updating Daemon Picture");
//                    profileOrcoverphoto = "cover";
//                    ShowImagePic2();
//                }else if (which == 2){
//                    pd.setMessage("Updating Name");
//                    showNamePhoneUpdateDialog("name");
//
//                }else if (which == 3){
//                    pd.setMessage("Updating Phone");
//                    showNamePhoneUpdateDialog("phone");
//
//                }
//                else if (which == 4){
//                    pd.setMessage("Updating Email");
//                    showNamePhoneUpdateDialog("email");
//
//                }
//            }
//
//
//
//        });
//        builder.create().show();
//    }
//    private void ShowImagePicDialog() {
//        String option[] = {"Camea", "Gallery"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
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
//                        Toast.makeText(MyProfileActivity.this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(MyProfileActivity.this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
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
//    private boolean checkStoragePermission(){
//        boolean result = ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                == (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }
//    private void requestStoragePermission(){
//        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
//    }
//
//    private boolean checkCameraPermission(){
//
//        boolean result = ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.CAMERA)
//                == (PackageManager.PERMISSION_GRANTED);
//
//        boolean result1 = ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == (PackageManager.PERMISSION_GRANTED);
//        return result && result1;
//    }
//    private void requestCameraPermission(){
//        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
//    }

}