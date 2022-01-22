package com.pthien.project_ciy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pthien.project_ciy.Model.Model_video;
import com.pthien.project_ciy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class Adaptervideo extends FirebaseRecyclerAdapter<Model_video, Adaptervideo.myviewholder> {
    String myUid, myimage, myname;
    private DatabaseReference Likevd;
    private DatabaseReference cmtvd;
    private DatabaseReference user;

    int countlikes = 0;
    int countcmt = 0;
    boolean mProcessLike = false;
    Context context;


    public Adaptervideo(@NonNull FirebaseRecyclerOptions<Model_video> options, Context context) {
        super(options);
        this.context = context;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Likevd = FirebaseDatabase.getInstance().getReference("LikeVD");
    }


    @Override
    protected void onBindViewHolder(@NonNull myviewholder myviewholder, int i, @NonNull Model_video model_video) {
        myviewholder.setdata(model_video);
        myviewholder.timvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                Likevd.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if (snapshot.child(model_video.getpId()).hasChild(myUid)  ){
                                Likevd.child(model_video.getpId()).child(myUid).removeValue();
                                mProcessLike = false;
                            }else
                                Likevd.child(model_video.getpId()).child(myUid).setValue("Liked");
                            mProcessLike = false;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        myviewholder.shvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vduri = model_video.getPvd();
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

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView avatar_uservd,fl;
        TextView sotimvd, socmtvd, nameus, ttvd, descvd, timevd, catevd;
        ImageView timvd, cmtvd, shvd;
        ProgressBar progressBar;
        VideoView vd;
        RelativeLayout itemvd;
        public myviewholder(@NonNull View itemView)
        {
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

        void setdata(Model_video obj)
        {


            String time = obj.getpTime();
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(Long.parseLong(time));
            String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

             ttvd.setText(obj.getpTitle());
            timevd.setText(datetime);
            catevd.setText("#" +obj.getpCate());
            descvd.setText(obj.getpDesc());
            Likevd.child(obj.getpId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        countlikes = (int) snapshot.getChildrenCount();
                        sotimvd.setText(Integer.toString(countlikes));
                    }else{
                        sotimvd.setText("0");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ////setlike
            Likevd.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(obj.getpId()).hasChild(myUid) ){
                      timvd.setImageResource(R.drawable.ic_timdd);
                    }else{
                      timvd.setImageResource(R.drawable.ic_tim);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
          shvd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Share", Toast.LENGTH_SHORT).show();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = obj.getPvd();
                    String shareSub = "Try now";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//                    context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
            });
            user = FirebaseDatabase.getInstance().getReference("Users");
            Query query = user.orderByChild("uid").equalTo(obj.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds: snapshot.getChildren()){
                        myimage = ""+ ds.child("image").getValue();
                        myname = ""+ ds.child("name").getValue();

                        try {
                            Picasso.get().load(myimage).placeholder(R.drawable.ic_face).into(avatar_uservd);
                        }catch (Exception e){
                        }
                        nameus.setText(myname);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(vd);


            vd.setMediaController(mediaController);
            vd.setVideoPath(obj.getPvd());
            vd.requestFocus();
            vd.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });
           vd.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what){
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                            progressBar.setVisibility(View.GONE);
                            return true;
                        }
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                           progressBar.setVisibility(View.GONE);
                            return true;
                        }
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                            progressBar.setVisibility(View.GONE);
                            return true;
                        }
                    }
                    return false;
                }
            });
            vd.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
    }
}
