package com.pthien.project_ciy.Adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pthien.project_ciy.AddPostActivity;
import com.pthien.project_ciy.DetailsPostActivity;
import com.pthien.project_ciy.Model.Model_Comments;
import com.pthien.project_ciy.Model.Model_post;

import com.pthien.project_ciy.R;
import com.pthien.project_ciy.ThereProfileActivity;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Adapter_Post extends RecyclerView.Adapter<Adapter_Post.Myholder>{
    Context context;
    List<Model_post> postList;
    String myUid;
    private DatabaseReference Likeref;
    private DatabaseReference cmts;
    private DatabaseReference user;
    RecyclerView recyclerView;
    List<Model_Comments> commentsList;
    Adapter_Comment adapter_comment;
    String myimage_cmt, myname_cmt;
    EditText editDl;
    View sheetView;
    ///repcmt



    int countlikes = 0;
    int countcmt = 0;

    boolean mProcessLike = false;

    public Adapter_Post(Context context, List<Model_post> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Likeref = FirebaseDatabase.getInstance().getReference("Likes");
        cmts = FirebaseDatabase.getInstance().getReference("Comments");
        user = FirebaseDatabase.getInstance().getReference("Users");

    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        String name = postList.get(position).getuName();
        String time = postList.get(position).getpTime();
        String title = postList.get(position).getpTitle();
        String cate = postList.get(position).getpCate();
        String desc = postList.get(position).getpDesc();
        String imae = postList.get(position).getpImage();
        String uid = postList.get(position).getUid();
        String udp = postList.get(position).getuDp();
        String pid = postList.get(position).getpId();

//      String timeStamp = String.valueOf(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        holder.title_post.setText(title);
        holder.name_post.setText(name);
        holder.time_post.setText(datetime);
        holder.cat_post.setText("#" +cate);
        holder.des_post.setText(desc);
        setLikes(holder, pid);
        String postIde= postList.get(position).getpId();
        Likeref.child(postIde).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                     countlikes = (int) snapshot.getChildrenCount();
                    holder.solike.setText(Integer.toString(countlikes)+" "+"likes");
                }else{
                    holder.solike.setText("0"+" "+"likes");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cmts.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countcmt = (int) snapshot.getChildrenCount();
                    holder.socmt.setText(Integer.toString(countcmt)+" "+"comments");
                }else{
                    holder.socmt.setText("0"+" "+"comments");

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomsheetTheme);
                sheetView = LayoutInflater.from(context).inflate(R.layout.comment_dialogbottom, holder.viewGroup);
                ImageView myavta_cmt = sheetView.findViewById(R.id.myavata_cmt);
                cmts.child(pid).addValueEventListener(new ValueEventListener() {
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

                Query query = user.orderByChild("uid").equalTo(myUid);
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

                ///view rcv cmt
                commentsList = new ArrayList<>();
                recyclerView = sheetView.findViewById(R.id.rcv_cmt);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setHasFixedSize(true);

                loadcomment(pid);

                sheetView.findViewById(R.id.send_cmt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDl = sheetView.findViewById(R.id.text_cmt);
                        String value = editDl.getText().toString().trim();
                        if (TextUtils.isEmpty(value)){
                            Toast.makeText(context, "Enter comment...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sendcmnt(holder, pid, value);

                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });


        try {
            Picasso.get().load(udp).placeholder(R.drawable.ic_face).into(holder.avt_post);
        }catch (Exception e){

        }

        holder.morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showMoreOptions(holder, uid, myUid, pid, imae);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                String postIde= postList.get(position).getpId();
                Likeref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if (snapshot.child(postIde).hasChild(myUid)  ){
                                Likeref.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }else
                            Likeref.child(postIde).child(myUid).setValue("Liked");
                            mProcessLike = false;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Share", Toast.LENGTH_SHORT).show();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody =  imae;
                String shareSub = "Try now";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        if(imae.equals("noImage")){
            holder.img_post.setVisibility(View.GONE);
        }else {
            holder.img_post.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(imae).into(holder.img_post);
            } catch (Exception e) {

            }
        }
        holder.detais_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });

        ///lấy id cmt

    }


    private void loadcomment(String pid) {
        cmts.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Model_Comments comments = ds.getValue(Model_Comments.class);
                            commentsList.add(comments);
                        adapter_comment = new Adapter_Comment(context, commentsList);
                        recyclerView.setAdapter(adapter_comment);
                        adapter_comment.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendcmnt(Myholder myholder, String pid, String value){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",myUid);
        hashMap.put("uname", myname_cmt);
        hashMap.put("uimage", myimage_cmt);
        hashMap.put("pId", pid);
        hashMap.put("cmtId", timeStamp);
        hashMap.put("cmtTime", timeStamp);
        hashMap.put("pComment", value);
        Random gn = new Random();
            int in = gn.nextInt();
        cmts.child(pid).child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Bình luận đã được đưa lên cộng đồng.", Toast.LENGTH_SHORT).show();
                        editDl.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Bình luận thất bại.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLikes(Myholder holder, String pid) {
        Likeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pid).hasChild(myUid) ){
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    holder.like.setText("Liked");
                    holder.like.setTextColor(Color.parseColor("#1E90FF"));

                }else{
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    holder.like.setText("Like");
                    holder.like.setTextColor(Color.parseColor("#000000"));

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });;
    }

    private void showMoreOptions(Myholder myholder, String uid, String myUid, String pid, String imae) {

        if (uid.equals(myUid)){
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomsheetTheme);
            sheetView = LayoutInflater.from(context).inflate(R.layout.more_bottomsheet, myholder.more1);
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
            sheetView.findViewById(R.id.cn1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Xem thêm.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, DetailsPostActivity.class);
                    intent.putExtra("postid", pid);
                    context.startActivity(intent);



                    bottomSheetDialog.dismiss();

                }
            });
            sheetView.findViewById(R.id.cn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "EditPost");
                    intent.putExtra("editPostId", pid);
                    context.startActivity(intent);
                    Toast.makeText(context, "Cập nhật bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Xoá bài viết.", Toast.LENGTH_SHORT).show();
                    begindelete(pid, imae);
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Lưu bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Bật thông báo bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Tắt thông báo bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn7).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Báo cáo bài viết có điều bất thường.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });

        }else {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomsheetTheme);
            sheetView = LayoutInflater.from(context).inflate(R.layout.more2_bottomsheet, myholder.more2);
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
            sheetView.findViewById(R.id.cn1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Xem thêm.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, DetailsPostActivity.class);
                    intent.putExtra("postid", pid);
                    context.startActivity(intent);

                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Lưu bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();

                }
            });
            sheetView.findViewById(R.id.cn5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Bật thông báo bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Tắt thông báo bài viết.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            sheetView.findViewById(R.id.cn7).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + "Báo cáo bài viết có điều bất thường.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
        }


    }

    private void begindelete(String pid, String imae) {
        if (imae.equals("noImage")){
            DeletewithNoimg(pid, imae);
        }else{
            Deletewithimg(pid, imae);
        }


    }

    private void Deletewithimg(String pid, String imae) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang Xoá bài viết...");
        StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(imae);
        picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.show();
                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Xoá thành công.", Toast.LENGTH_SHORT).show();
                       pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Xoá thất bại.", Toast.LENGTH_SHORT).show();
                     pd.dismiss();
                    }
                });

            }
        });
    }

    private void DeletewithNoimg(String pid, String imae) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang Xoá bài viết...");
        pd.show();
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Xoá thành công.", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Xoá thất bại.", Toast.LENGTH_SHORT).show();
                ProgressDialog pd = new ProgressDialog(context);
                pd.setMessage("Đang Xoá bài viết...");
                pd.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        if (postList != null){
            return postList.size();
        }

        return 0;
    }

    class Myholder extends RecyclerView.ViewHolder{
           ImageView avt_post, img_post;
           TextView name_post, time_post, title_post, cat_post, des_post, solike, socmt;
           ImageView morebtn;
           Button like, cmt, share, btn_send;
           EditText editText_cmt;
           LinearLayout detais_user, more1, more2;
           RelativeLayout viewGroup;


        public Myholder(@NonNull View itemView) {
            super(itemView);
               avt_post = itemView.findViewById(R.id.avatar_post);
               img_post = itemView.findViewById(R.id.image_post);
               name_post = itemView.findViewById(R.id.name_post);
               cat_post = itemView.findViewById(R.id.cat_post);
               time_post = itemView.findViewById(R.id.time_post);
               title_post = itemView.findViewById(R.id.title_post);
               des_post = itemView.findViewById(R.id.desc_post);
               like = itemView.findViewById(R.id.btn_like);
               cmt = itemView.findViewById(R.id.btn_cmt);
               share = itemView.findViewById(R.id.btn_share);
               morebtn = itemView.findViewById(R.id.btn_more);
               solike = itemView.findViewById(R.id.solike);
               detais_user = itemView.findViewById(R.id.details_user);
               viewGroup = itemView.findViewById(R.id.dialog_cmt);
               socmt = itemView.findViewById(R.id.socmt);
               ///
            editText_cmt = itemView.findViewById(R.id.text_cmt);
            btn_send = itemView.findViewById(R.id.send_cmt);

            ///
            more1 = itemView.findViewById(R.id.bottom_sheet1);
            more2 = itemView.findViewById(R.id.bottom_sheet2);

        }
    }
}
