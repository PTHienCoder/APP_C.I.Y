package com.pthien.project_ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Setting_app extends AppCompatActivity {
    SwitchCompat postSwith;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final String TOPIC_POST_NOTIFICATION = "POST";
  private ActionBar actionBar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_app);

        actionBar1 = getSupportActionBar();

        actionBar1.setDisplayShowHomeEnabled(true);
        actionBar1.setDisplayHomeAsUpEnabled(true);

        postSwith = findViewById(R.id.postswitch);
         sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
         boolean isPostEnable = sp.getBoolean(""+TOPIC_POST_NOTIFICATION, false);
      if (isPostEnable){
          postSwith.setChecked(true);
      }else {
          postSwith.setChecked(false);
      }
        postSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();
                if (isChecked){
                    BatthongbaoPost();
                }else{
                    TatthongbaoPost();
                }
            }
        });
    }

    private void TatthongbaoPost() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Bạn sẽ không nhận thông báo khi có bài viết mới";
                        if (!task.isSuccessful()){
                            msg = "Tắt thông báo thất bại";
                        }
                        Toast.makeText(Setting_app.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void BatthongbaoPost() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      String msg = "Bạn sẽ nhận thông báo khi có bài viết mới";
                      if (!task.isSuccessful()){
                          msg = "Bật thông báo thất bại";
                      }
                        Toast.makeText(Setting_app.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}