package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_Comment;
import com.pthien.project_ciy.Adapter.Adapter_Post;
import com.pthien.project_ciy.Model.Model_Comments;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DetailsPostActivity extends AppCompatActivity {

    TextView title, time, desc, content, noimg, ooop, seenCmt;
    ImageView avatar, imageView;
    RelativeLayout rslu;
    FirebaseAuth firebaseAuth;
    private ActionBar actionBar;
    RelativeLayout viewGroup;
    View sheetView;
    FirebaseUser user;
    EditText editDl;


    List<Model_Comments> modelComments;
    Adapter_Comment adapterComment;
    RecyclerView rcv_cmnt;
    String myid, myimage_cmt, myname_cmt;
    DatabaseReference users;
    DatabaseReference cmts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_post);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        title = findViewById(R.id.nametvdb);
        time = findViewById(R.id.emailtvdb);
        desc = findViewById(R.id.mota);
        content = findViewById(R.id.content);
        avatar = findViewById(R.id.avatar_db);
        imageView = findViewById(R.id.coverdb);
        noimg = findViewById(R.id.textnoimg);
        ooop = findViewById(R.id.ooop);
        rslu = findViewById(R.id.nocontent);
        seenCmt = findViewById(R.id.xemcmt);
        viewGroup = findViewById(R.id.dialog_cmt);
        Intent intent = getIntent();
        cmts = FirebaseDatabase.getInstance().getReference("Comments");
        users = FirebaseDatabase.getInstance().getReference("Users");
        String  idpost = ""+intent.getStringExtra("postid");
        seenCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailsPostActivity.this, R.style.BottomsheetTheme);
                sheetView = LayoutInflater.from(DetailsPostActivity.this).inflate(R.layout.comment_dialogbottom, viewGroup);
                ImageView myavta_cmt = sheetView.findViewById(R.id.myavata_cmt);
                cmts.child(idpost).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            sheetView.findViewById(R.id.nocmnt).setVisibility(View.GONE);
                        }else{
                            sheetView.findViewById(R.id.nocmnt).setVisibility(View.VISIBLE);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query = users.orderByChild("uid").equalTo(user.getUid());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds: snapshot.getChildren()){
                            myimage_cmt = ""+ ds.child("image").getValue();
                            myname_cmt = ""+ ds.child("name").getValue();
                            try {
                                Picasso.get().load(myimage_cmt).placeholder(R.drawable.ic_face).into(myavta_cmt);
                            }catch (Exception e){
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
                ///view rcv cmt
                modelComments = new ArrayList<>();
                rcv_cmnt = sheetView.findViewById(R.id.rcv_cmt);
                rcv_cmnt.setLayoutManager(new LinearLayoutManager(DetailsPostActivity.this));
                rcv_cmnt.setHasFixedSize(true);
                sheetView.findViewById(R.id.send_cmt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDl = sheetView.findViewById(R.id.text_cmt);
                        String value = editDl.getText().toString().trim();
                        if (TextUtils.isEmpty(value)){
                            Toast.makeText(DetailsPostActivity.this, "Enter comment...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sendcmnt(idpost, value);

                    }
                });
                loadComment(idpost);


            }
        });



        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(idpost);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String pCate = ""+ ds.child("pCate").getValue();
                    String pTitle = ""+ ds.child("pTitle").getValue();
                    String pTime = ""+ ds.child("pTime").getValue();
                    String pDesc = ""+ ds.child("pDesc").getValue();
                    String pDetails = ""+ ds.child("pDetails").getValue();
                    String uDp = ""+ ds.child("uDp").getValue();
                    String pimage = ""+ ds.child("pImage").getValue();


                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTime));
                    String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    if (!pimage.equals("noImage")){
                        noimg.setVisibility(View.GONE);
                        try {
                            Picasso.get().load(pimage).into(imageView);
                        } catch (Exception e) {

                        }
                    }
                    if (!pDetails.equals("None")){
                        rslu.setVisibility(View.GONE);
                        content.setText("");
                    }

                    ooop.setText(pCate);
                    content.setText(pDetails);
                     title.setText(pTitle);
                     desc.setText(pDesc);
                     time.setText(datetime);

                    try {
                        Picasso.get().load(uDp).placeholder(R.drawable.ic_face).into(avatar);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_addcamera_black).into(avatar);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadComment(String idpost) {
        cmts.child(idpost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelComments.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_Comments comments = ds.getValue(Model_Comments.class);
                    modelComments.add(comments);

                    adapterComment = new Adapter_Comment(DetailsPostActivity.this, modelComments);
                    rcv_cmnt.setAdapter(adapterComment);
                    adapterComment.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void sendcmnt(String idpost, String value){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", user.getUid());
        hashMap.put("uname", myname_cmt);
        hashMap.put("uimage", myimage_cmt);
        hashMap.put("pId", idpost);
        hashMap.put("cmtId", timeStamp);
        hashMap.put("cmtTime", timeStamp);
        hashMap.put("pComment", value);
        Random gn = new Random();
        int in = gn.nextInt();

        cmts.child(idpost).child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailsPostActivity.this, "Bình luận đã được đưa lên cộng đồng.", Toast.LENGTH_SHORT).show();
                        editDl.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsPostActivity.this, "Bình luận thất bại.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
        }else{
            startActivity(new Intent (DetailsPostActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}