package com.example.stage_2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SysApplication.getInstance().addActivity(this);//收集活动
        InitParam();
        SetTransparentStatusBar();
        JumpPage();

    }

    /*
     *获取控件
     */
    protected void InitParam(){
        pref= PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * 设置状态栏透明
     */
    protected void SetTransparentStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /*
     *等待几秒后跳转页面
     */
    protected void JumpPage(){
        String user=pref.getString("user","");
        if(user.equals("")){
            StartJump(IdentifyActivity.class);
        }else {
            StartJump(MainActivity.class);
        }
    }

    /*
     *开始跳转页面
     * @param cls:跳转到的页面
     */
    protected void StartJump(Class<?> cls){
        Intent intent=new Intent(this,cls);//跳转页面
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        long trigger_time= SystemClock.elapsedRealtime()+3000;//延迟几秒
        if (manager != null) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigger_time,pendingIntent);
        }
    }
}
