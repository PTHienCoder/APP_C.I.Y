package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Model.Model_post;
import com.pthien.project_ciy.Model.Model_video;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_video extends RecyclerView.Adapter<Adapter_video.myHolder>{

    Context context;
    List<Model_video> videoList;
    String myUid, myimage, myname;
    private DatabaseReference Likevd;
    private DatabaseReference cmtvd;
    private DatabaseReference user;

    int countlikes = 0;
    int countcmt = 0;
    boolean mProcessLike = false;
    public Adapter_video(Context context, List<Model_video> videoList) {
        this.context = context;
        this.videoList = videoList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Likevd = FirebaseDatabase.getInstance().getReference("LikeVD");
        cmtvd = FirebaseDatabase.getInstance().getReference("CommentVD");
        user = FirebaseDatabase.getInstance().getReference("Users");

    }
    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        Model_video modelVideo = videoList.get(position);
        String time = modelVideo.getpTime();
        String title = modelVideo.getpTitle();
        String cate = modelVideo.getpCate();
        String desc = modelVideo.getpDesc();
        String pid = modelVideo.getpId();

        String uid = modelVideo.getUid();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.ttvd.setText(title);
        holder.timevd.setText(datetime);
        holder.catevd.setText("#" +cate);
        holder.descvd.setText(desc);

        setvideo(modelVideo, holder);
        setLikes(holder, pid);

        Query query = user.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    myimage = ""+ ds.child("image").getValue();
                    myname = ""+ ds.child("name").getValue();

                    try {
                        Picasso.get().load(myimage).placeholder(R.drawable.ic_face).into(holder.avatar_uservd);
                    }catch (Exception e){
                    }
                    holder.nameus.setText(myname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Likevd.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countlikes = (int) snapshot.getChildrenCount();
                    holder.sotimvd.setText(Integer.toString(countlikes));
                }else{
                    holder.sotimvd.setText("0");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.timvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                Likevd.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if (snapshot.child(pid).hasChild(myUid)  ){
                                Likevd.child(pid).child(myUid).removeValue();
                                mProcessLike = false;
                            }else
                                Likevd.child(pid).child(myUid).setValue("Liked");
                            mProcessLike = false;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.shvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vduri = modelVideo.getPvd();
                Toast.makeText(context,"Share", Toast.LENGTH_SHORT).show();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = vduri;
                String shareSub = "Try now";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });


    }
    private void setLikes(myHolder holder, String pid) {
        Likevd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pid).hasChild(myUid) ){
                    holder.timvd.setImageResource(R.drawable.ic_timdd);
                }else{
                    holder.timvd.setImageResource(R.drawable.ic_tim);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setvideo(Model_video modelVideo, myHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);

        String vdurl = modelVideo.getPvd();

//        MediaController mediaController = new MediaController(context);
//        mediaController.setAnchorView(holder.vd);
//
//        Uri vduri = Uri.parse(vdurl);
//        holder.vd.setMediaController(mediaController);
//        holder.vd.setVideoURI(vduri);
//        holder.vd.requestFocus();
        holder.vd.setVideoPath(vdurl);
        holder.vd.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        holder.vd.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });
        holder.vd.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (videoList != null){
            return videoList.size();
        }

        return 0;
    }

    class myHolder extends RecyclerView.ViewHolder {
        ImageView avatar_uservd,fl;
        TextView sotimvd, socmtvd, nameus, ttvd, descvd, timevd, catevd;
        ImageView timvd, cmtvd, shvd;
        ProgressBar progressBar;
        VideoView vd;
        RelativeLayout itemvd;
        public myHolder(@NonNull View itemView) {
            super(itemView);

            avatar_uservd = itemView.findViewById(R.id.avatar_uservd);
            fl = itemView.findViewById(R.id.fl);
            sotimvd = itemView.findViewById(R.id.sotimvd);
            socmtvd = itemView.findViewById(R.id.socmtvd);
            timvd = itemView.findViewById(R.id.timvd);
            cmtvd = itemView.findViewById(R.id.cmtvd);
            shvd = itemView.findViewById(R.id.shvd);
            itemvd = itemView.findViewById(R.id.itemvd);
            vd = itemView.findViewById(R.id.videovew);

            progressBar = itemView.findViewById(R.id.progre);
            nameus = itemView.findViewById(R.id.nameus);
            ttvd = itemView.findViewById(R.id.ttvd);
            descvd = itemView.findViewById(R.id.descvd);
            timevd = itemView.findViewById(R.id.timevd);
            catevd = itemView.findViewById(R.id.catevd);

        }
    }
}
