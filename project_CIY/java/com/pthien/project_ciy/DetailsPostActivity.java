package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class DetailsPostActivity extends AppCompatActivity {

    TextView title, time, desc, content, noimg, ooop;
    ImageView avatar, imageView;
    RelativeLayout rslu;
    FirebaseAuth firebaseAuth;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_details_post);
        title = findViewById(R.id.nametvdb);
        time = findViewById(R.id.emailtvdb);
        desc = findViewById(R.id.mota);
        content = findViewById(R.id.content);
        avatar = findViewById(R.id.avatar_db);
        imageView = findViewById(R.id.coverdb);
        noimg = findViewById(R.id.textnoimg);
        ooop = findViewById(R.id.ooop);
        rslu = findViewById(R.id.nocontent);

        Intent intent = getIntent();
        String  idpost = ""+intent.getStringExtra("postid");

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
}