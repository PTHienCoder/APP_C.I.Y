package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pthien.project_ciy.Adapter.Adapter_Comment;
import com.pthien.project_ciy.Adapter.Adapter_RepComment;
import com.pthien.project_ciy.Model.Model_Comments;
import com.pthien.project_ciy.Model.Model_Repcomment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RepCommentActivity2 extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    ImageButton backs;
    ImageView avata_cmt, myavata, send;
    TextView name_cmt, Timecmt, tl_cmt, cmt_tv;
   private RecyclerView rv_repcmt;
    List<Model_Repcomment> model_repcomments;
     private Adapter_RepComment adapter_repComment;
    private DatabaseReference repcmts;
    EditText editText;
    String IDcmntrep, iduser;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String myname;
    int countcmt = 0;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_comment2);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        repcmts = FirebaseDatabase.getInstance().getReference("RepComment");
        Intent intent = getIntent();

        String  pcmt = ""+intent.getStringExtra("pCmt");
        String  uname = ""+intent.getStringExtra("username");
        String  timecmt = ""+intent.getStringExtra("TimeCmt");
        iduser = ""+intent.getStringExtra("iduser");
        IDcmntrep = ""+intent.getStringExtra("idCmt");

        backs = findViewById(R.id.backs);
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        avata_cmt = findViewById(R.id.avatar_cmt);

        Query queryq = databaseReference.orderByChild("uid").equalTo(iduser);
        queryq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String image = "" + ds.child("image").getValue();
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_face).into(avata_cmt);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_addcamera_black).into(avata_cmt);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myavata = findViewById(R.id.myavata_cmt);
        Query queryqq = databaseReference.orderByChild("uid").equalTo(user.getUid());
        queryqq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String image = "" + ds.child("image").getValue();
                    myname = "" + ds.child("name").getValue();
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_face).into(myavata);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_addcamera_black).into(myavata);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tl_cmt = findViewById(R.id.sotlbl);
        repcmts.child(IDcmntrep).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countcmt = (int) snapshot.getChildrenCount();
                    tl_cmt.setText(Integer.toString(countcmt)+" "+"trả lời");
                }else{
                    tl_cmt.setText("0"+" "+"trả lời");

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        name_cmt = findViewById(R.id.name_cmt);
        name_cmt.setText(uname);
        Timecmt = findViewById(R.id.time_cmt);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timecmt));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        Timecmt.setText(datetime);

        cmt_tv = findViewById(R.id.cmt_tv);
        cmt_tv.setText(pcmt);
        editText = findViewById(R.id.text_cmt);
        send = findViewById(R.id.send_cmt);


        model_repcomments = new ArrayList<>();
        rv_repcmt = findViewById(R.id.rcv_repcmnt);
        rv_repcmt.setLayoutManager(new LinearLayoutManager(this));
        adapter_repComment = new Adapter_RepComment(this, model_repcomments);
        rv_repcmt.setHasFixedSize(true);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendcmnt();
            }
        });
        checkUserStatus();
        loadrepCmt();

    }

    private void loadrepCmt() {
        repcmts.child(IDcmntrep).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model_repcomments.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_Repcomment comments = ds.getValue(Model_Repcomment.class);
                    model_repcomments.add(comments);

                    rv_repcmt.setAdapter(adapter_repComment);
//                    adapter_repComment.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendcmnt() {
        String value = editText.getText().toString().trim();
        if (TextUtils.isEmpty(value)){
            Toast.makeText(RepCommentActivity2.this, "Enter comment...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", user.getUid());
        hashMap.put("uname", myname);
        hashMap.put("userid", iduser);
        hashMap.put("repComment", value);
        hashMap.put("cmtId", IDcmntrep);
        hashMap.put("repcmtId", timeStamp);
        hashMap.put("repcmtTime", timeStamp);
        repcmts.child(IDcmntrep).child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RepCommentActivity2.this, "Đã trả lời bình luận.", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RepCommentActivity2.this, "Trả lời thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            startActivity(new Intent (RepCommentActivity2.this, MainActivity.class));
            finish();
        }
    }
}