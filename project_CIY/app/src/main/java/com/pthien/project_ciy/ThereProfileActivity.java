package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Adapter.Adapter_Post;
import com.pthien.project_ciy.Adapter.Adapter_user;
import com.pthien.project_ciy.Model.Model_post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {
    TextView nametv, emailtv, phonetv;
    ImageView avatar, coverimg;
    LinearLayoutManager layoutManagertp;
    FirebaseAuth firebaseAuth;
    List<Model_post> postList;
    Adapter_Post adapter_post;
    RecyclerView rcv_they_post;
    String uid;
    private ActionBar actionBar;
    String Hisname, email, myUid;
    TextView flow, flowing;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference friend;
    boolean mProcessfollow = false;
    int flower =0;
    int flowingg =0;
    Button fl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);


        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(email);

        avatar = findViewById(R.id.avatar);
        coverimg = findViewById(R.id.cover);
        emailtv = findViewById(R.id.emailtv);
        phonetv = findViewById(R.id.phonetv);
        nametv = findViewById(R.id.nametv);
        flow = findViewById(R.id.flow);
        flowing = findViewById(R.id.flowing);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference friends = firebaseDatabase.getReference("Friends");
        friends.child(uid).addValueEventListener(new ValueEventListener() {
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
                        if (ds.hasChild(uid)) {
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
        friend = FirebaseDatabase.getInstance().getReference("Friends");
        setfollow();
        fl = findViewById(R.id.fab_follow);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessfollow = true;
                friend.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessfollow){
                            if (snapshot.child(uid).hasChild(myUid)  ){
                                friend.child(uid).child(myUid).removeValue();
                                mProcessfollow = false;
                            }else
                                friend.child(uid).child(myUid).child("id").setValue(myUid);
                            mProcessfollow = false;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        Button nt = findViewById(R.id.fab_nhantin);
        nt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThereProfileActivity.this, ChatActivity.class);
                intent.putExtra("hisUid", uid);
                startActivity(intent);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();


        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                     Hisname = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String image = ""+ ds.child("image").getValue();
                    String cover = ""+ ds.child("cover").getValue();
                    actionBar.setTitle(Hisname);

                    nametv.setText(Hisname);
                    emailtv.setText(email);
                    phonetv.setText(phone);

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_face).into(avatar);
                    }catch (Exception e){
                    }
                    try {
                        Picasso.get().load(cover).placeholder(R.drawable.ic_image).into(coverimg);
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rcv_they_post = findViewById(R.id.rcv_they_post);

        postList = new ArrayList<>();
        adapter_post = new Adapter_Post(ThereProfileActivity.this, postList);
        layoutManagertp = new LinearLayoutManager(this);
        rcv_they_post.setHasFixedSize(true);
        rcv_they_post.setLayoutManager(layoutManagertp);
        rcv_they_post.setAdapter(adapter_post);
         checkUserStatus();
        Loadhispost();
    }
    private void setfollow() {
        friend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).hasChild(myUid) ){
                    fl.setText("Đã Follower");
                }else{
                    fl.setText("Follower");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });;
    }

    private void Loadhispost() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query rs = ref.orderByChild("uid").equalTo(uid);
        rs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_post model_post = ds.getValue(Model_post.class);
                    postList.add(model_post);
                    rcv_they_post.setAdapter(adapter_post);
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
                    rcv_they_post.setAdapter(adapter_post);
                    adapter_post.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String myid = user.getUid();
        if(user != null){

        }else{
            startActivity(new Intent(ThereProfileActivity.this, MainActivity.class));
            finish();
        }
        if (myid.equals(uid)){
            startActivity(new Intent(ThereProfileActivity.this, MyProfileActivity.class));
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
                    Loadhispost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    SearchmyPosst(query);
                }else{
                    Loadhispost();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}