package com.pthien.project_ciy.Adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.pthien.project_ciy.Add_Quention_Activity;
import com.pthien.project_ciy.DetailsPostActivity;
import com.pthien.project_ciy.Model.Model_Comments;
import com.pthien.project_ciy.Model.Model_Quentions;
import com.pthien.project_ciy.Model.Model_repQs;
import com.pthien.project_ciy.R;
import com.pthien.project_ciy.ThereProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Adapter_question extends RecyclerView.Adapter<Adapter_question.Myholder>{
    Context context;
    List<Model_Quentions> quentions;
    String myUid;
    private DatabaseReference user;
    private DatabaseReference Qs;
    private DatabaseReference repQs;
    private RecyclerView recyclerView;
    List<Model_repQs> modelRepQs;
    private Adapter_repQs adapter_repQs;
    private String myimage_cmt, myname_cmt;
    EditText editDl;
    View sheetView;
    int countrep = 0;

    public Adapter_question(Context context, List<Model_Quentions> quentions) {
        this.context = context;
        this.quentions = quentions;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repQs = FirebaseDatabase.getInstance().getReference("RepQuestions");
        user = FirebaseDatabase.getInstance().getReference("Users");
    }


    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        String name = quentions.get(position).getUname();
        String time = quentions.get(position).getqTime();
        String title = quentions.get(position).getqTitle();
        String cate = quentions.get(position).getqCate();
        String desc = quentions.get(position).getqDesc();
        String image = quentions.get(position).getqImage();
        String uid = quentions.get(position).getUid();
        String udp = quentions.get(position).getUimg();
        String pid = quentions.get(position).getqId();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();



        holder.title_post.setText(title);
        holder.name_post.setText(name);
        holder.time_post.setText(datetime);
        holder.cat_post.setText("#" +cate);
        holder.des_post.setText(desc);
        repQs.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countrep = (int) snapshot.getChildrenCount();
                    holder.solike.setText(Integer.toString(countrep)+" "+"Câu trả lời");
                }else{
                    holder.solike.setText("0"+" "+"Câu trả lời");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Picasso.get().load(udp).placeholder(R.drawable.ic_face).into(holder.avt_post);
        }catch (Exception e){

        }
        if(image.equals("noImage")){
            holder.img_post.setVisibility(View.GONE);
        }else {
            holder.img_post.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(image).into(holder.img_post);
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
        holder.morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder, holder.morebtn, uid, myUid, pid, image);
            }
        });

        holder.btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomsheetTheme);
                sheetView = LayoutInflater.from(context).inflate(R.layout.repqs_bottomsheet, holder.viewGroup);
                ImageView myavta_cmt = sheetView.findViewById(R.id.myavata_cmt);
                repQs.child(pid).addValueEventListener(new ValueEventListener() {
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
                modelRepQs = new ArrayList<>();
                recyclerView = sheetView.findViewById(R.id.rcv_cmt2);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setHasFixedSize(true);

                loadcomment(pid);

                sheetView.findViewById(R.id.send_cmt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDl = sheetView.findViewById(R.id.text_cmt);
                        String value = editDl.getText().toString().trim();
                        sendcmnt(pid, value);
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });


    }
    private void loadcomment(String pid) {
        repQs.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelRepQs.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_repQs model_repQs = ds.getValue(Model_repQs.class);
                    modelRepQs.add(model_repQs);
                    adapter_repQs = new Adapter_repQs(context, modelRepQs);
                    recyclerView.setAdapter(adapter_repQs);
                    adapter_repQs.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendcmnt(String pid, String value){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",myUid);
        hashMap.put("uname", myname_cmt);
        hashMap.put("uimage", myimage_cmt);
        hashMap.put("qId", pid);
        hashMap.put("repId", timeStamp);
        hashMap.put("repTime", timeStamp);
        hashMap.put("qComment", value);
        Random gn = new Random();
        int in = gn.nextInt();
        repQs.child(pid).child(timeStamp).setValue(hashMap)
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


    @Override
    public int getItemCount() {
        if (quentions != null){
            return quentions.size();
        }

        return 0;
    }



    private void showMoreOptions(Myholder myholder, ImageView morebtn, String uid, String myUid, String pid, String imae) {

            if (uid.equals(myUid)){
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomsheetTheme);
                sheetView = LayoutInflater.from(context).inflate(R.layout.more4question, myholder.more2);
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
                sheetView.findViewById(R.id.cn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Add_Quention_Activity.class);
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
                Query fquery = FirebaseDatabase.getInstance().getReference("Questions").orderByChild("qId").equalTo(pid);
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
        Query fquery = FirebaseDatabase.getInstance().getReference("Questions").orderByChild("qId").equalTo(pid);
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



    class Myholder extends RecyclerView.ViewHolder{
        ImageView avt_post, img_post;
        TextView name_post, time_post, title_post, cat_post, des_post, solike;
        ImageView morebtn;
        Button btn_help;
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
            morebtn = itemView.findViewById(R.id.btn_more);
            solike = itemView.findViewById(R.id.solike);
            detais_user = itemView.findViewById(R.id.details_user);
            viewGroup = itemView.findViewById(R.id.dialog_cmt);

            btn_help = itemView.findViewById(R.id.btn_help);
            more1 = itemView.findViewById(R.id.bottom_sheet3);
            more2 = itemView.findViewById(R.id.bottom_sheet4);


        }
    }
}
