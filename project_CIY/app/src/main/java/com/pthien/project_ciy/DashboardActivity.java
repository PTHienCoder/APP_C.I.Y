package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pthien.project_ciy.Adapter.Adapter_menubottom;
import com.pthien.project_ciy.Adapter.Adaptervideo;
import com.pthien.project_ciy.Model.Model_video;
import com.pthien.project_ciy.fragement.DiscoverFragment;
import com.pthien.project_ciy.fragement.HomeFragment;
import com.pthien.project_ciy.fragement.NoticationsFragment;
import com.pthien.project_ciy.fragement.VideosFragment;
import com.pthien.project_ciy.notification.Token;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    private NavigationView navigationView2;
    String mUID;
    private TextView nametvdb, emailtvdb;
    private ImageView avatardb, coverimgdb;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String myid;


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            finish();
        }else{
            super.onBackPressed();
            finish();
        }
    }
    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (Build.VERSION.SDK_INT>=21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window  = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

//         FirebaseRecyclerOptions<Model_video> options =
//                new FirebaseRecyclerOptions.Builder<Model_video>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("VideoPost"), Model_video.class)
//                        .build();
//        adapter =new Adaptervideo(options, DashboardActivity.this);

//        firebaseAuth = FirebaseAuth.getInstance();
//        myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView2 = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.collapsingtoolbarlayout);
        setSupportActionBar(toolbar);
        toolbar.setTitle("C.I.Y");


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(DashboardActivity.this
        , mDrawerLayout, toolbar, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ImageButton seach = findViewById(R.id.searchicon);
        seach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SearchActivity.class));
            }
        });
        ImageButton mesage = findViewById(R.id.mesicon);
        mesage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ShowUserChatActivity.class);
                startActivity(intent);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        navigationView2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.pro1:
                        Intent intent = new Intent(DashboardActivity.this, MyProfileActivity.class);
                        startActivity(intent);
                        mDrawerLayout.close();
                        break;

                    case  R.id.pro2:
                        intent = new Intent(DashboardActivity.this, MyGroupActivity.class);
                        intent.putExtra("iduser", myid);
                       startActivity(intent);
                        mDrawerLayout.close();

                    break;

                    case R.id.pro3:

                        break;
                    case R.id.pro4:

                        break;
                    case R.id.pro5:

                        break;
                    case R.id.pro6:

                        break;
                    case R.id.ab1:
                        intent = new Intent(DashboardActivity.this, Setting_app.class);
                        startActivity(intent);
                        mDrawerLayout.close();

                        break;
                    case R.id.ab2:

                        break;
                    case R.id.ab3:
                        firebaseAuth.signOut();
                        checkUserStatus();
                        break;

                }
                return false;
            }
        });
        View header= navigationView2.getHeaderView(0);

        /////navigation bottom

        BottomNavigationView navigationView = findViewById(R.id.nav_bottom);
        ViewPager viewPager = findViewById(R.id.viewpagerbottom);
        Adapter_menubottom adapter_ab0 = new Adapter_menubottom(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter_ab0);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        HomeFragment homeFragment = (HomeFragment) viewPager.getAdapter().instantiateItem(viewPager,0);
//                        onStop();
                        homeFragment.reloadata();
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.nav_videos).setChecked(true);
                        VideosFragment profileFragment = (VideosFragment) viewPager.getAdapter().instantiateItem(viewPager,1);
//                        onStart();
//                        onStop();
                        profileFragment.reloadata();
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.nav_discovers).setChecked(true);
                        DiscoverFragment friendsFragment = (DiscoverFragment) viewPager.getAdapter().instantiateItem(viewPager,2);
                        friendsFragment.reloadata();
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        onRestart();
                        break;
                    case R.id.nav_videos:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_discovers:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_notications:
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.addpost:
                       Dialog dialog1 = new Dialog(DashboardActivity.this);
                        dialog1.setContentView(R.layout.model_choose_add);
                        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog1.setCancelable(false);
                        dialog1.getWindow().getAttributes().windowAnimations = R.style.BottomsheetStyle;
                        dialog1.show();
                        dialog1.findViewById(R.id.cn1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DashboardActivity.this, AddPostActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog1.findViewById(R.id.cn2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DashboardActivity.this, Add_VideoActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog1.findViewById(R.id.cn3).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        dialog1.findViewById(R.id.dong).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        break;

                }
                return true;
            }
        });

        avatardb = header.findViewById(R.id.avatar_db);
        coverimgdb = header.findViewById(R.id.coverdb);
        emailtvdb = header.findViewById(R.id.emailtvdb);
        nametvdb = header.findViewById(R.id.nametvdb);

        avatardb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MyProfileActivity.class);
                startActivity(intent);
                mDrawerLayout.close();
            }
        });

        coverimgdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MyProfileActivity.class);
                startActivity(intent);
                mDrawerLayout.close();
            }
        });
        checkUserStatus();
        final Query query1 = databaseReference.orderByChild("uid").equalTo(mUID);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String namedb = ""+ ds.child("name").getValue();
                    String emaildb = ""+ ds.child("email").getValue();
                    String imagedb = ""+ ds.child("image").getValue();
                    String coverdb = ""+ ds.child("cover").getValue();
                    nametvdb.setText(namedb);
                    emailtvdb.setText(emaildb);
                    try {
                        Picasso.get().load(imagedb).into(avatardb);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_addcamera_black).placeholder(R.drawable.ic_face).into(avatardb);
                    }
                    try {
                        Picasso.get().load(coverdb).into(coverimgdb);
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            mUID= user.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            updateToken(FirebaseInstanceId.getInstance().getToken());
        }else{
            startActivity(new Intent (DashboardActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();

    }


    @Override
    protected void onStop() {
        super.onStop();

    }

}


//        BottomNavigationView navigationView = findViewById(R.id.nav_bottom);
//        navigationView.setOnNavigationItemSelectedListener(selectedListener);
//        HomeFragment fragment = new HomeFragment();
//        FragmentTransaction ftl = getSupportFragmentManager().beginTransaction();
//        ftl.replace(R.id.content, fragment, "");
//        ftl.commit();


//    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                   switch (item.getItemId()){
//                       case R.id.nav_home:
//                           HomeFragment fragment = new HomeFragment();
//                           FragmentTransaction ftl = getSupportFragmentManager().beginTransaction();
//                           ftl.replace(R.id.content, fragment, "");
//                           ftl.commit();
//
//                           return true;
//
//                       case R.id.nav_profile:
//                           ProfileFragment fragment1 = new ProfileFragment();
//                           FragmentTransaction ftl1 = getSupportFragmentManager().beginTransaction();
//                           ftl1.replace(R.id.content, fragment1, "");
//                           ftl1.commit();
//
//                           return true;
//                       case R.id.nav_friend:
//                           FriendsFragment fragment2 = new FriendsFragment();
//                           FragmentTransaction ftl2 = getSupportFragmentManager().beginTransaction();
//                           ftl2.replace(R.id.content, fragment2, "");
//                           ftl2.commit();
//
//                           return true;
//
//                       case R.id.nav_mesage:
//                           MessageFragment fragment3 = new MessageFragment();
//                           FragmentTransaction ftl3 = getSupportFragmentManager().beginTransaction();
//                           ftl3.replace(R.id.content, fragment3, "");
//                           ftl3.commit();
//
//                           return true;
//
//                   }
//
//                    return false;
//                }
//            };