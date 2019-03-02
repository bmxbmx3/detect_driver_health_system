package com.example.stage_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Calendar;

public class IdentifyActivity extends AppCompatActivity implements View.OnClickListener{
    private Button menu_btn;
    private TextView title_txt;
    private Button back_btn;
    private Button reg_and_log_btn;//注册/登录按钮
    private CustomDialog_UserInfo user_Info_dg;
    private String sendData;//向服务器发送的数据
    private String getData;//从服务器接受的数据
    private EditText user_txt;
    private EditText key_txt;
    private String user;//用户名
    private String key;//密码
    private long mExitTime;//获取点按返回键时的当前系统时间
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        SysApplication.getInstance().addActivity(this);//收集活动
        InitParam();
        SetTitle("身份验证");
        HideMenuBtn();
        HideBackBtn();
        SetListener();
    }

    /*
     *获取控件
     */
    protected void InitParam(){
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pref.edit();
        back_btn=(Button)findViewById(R.id.back_btn);
        title_txt=(TextView)findViewById(R.id.title_txt);
        menu_btn=(Button)findViewById(R.id.menu_btn);
        reg_and_log_btn=(Button)findViewById(R.id.reg_and_log_btn);
        user_txt=(EditText) findViewById(R.id.user_txt);
        key_txt=(EditText) findViewById(R.id.key_txt);
    }

    /*
     *添加监听事件
     */
    protected void SetListener(){
        reg_and_log_btn.setOnClickListener(this);
    }

    /*
     *集中处理监听事件
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reg_and_log_btn:
                if(!CheckNetwork.isNetworkAvailable(this)){
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }else {
                    user=user_txt.getText().toString();
                    key=key_txt.getText().toString();
                    if(user.equals("")||key.equals("")) {
                        Toast.makeText(IdentifyActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                    } else {
                        String get_data=GetFromTCP("身份验证"+"+"+user+"+"+key);
                        switch (get_data){
                            case "用户存在且密码正确":
                                Toast.makeText(IdentifyActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                SaveUser();
                                break;
                            case "用户存在且密码错误":
                                Toast.makeText(IdentifyActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                user_txt.setText("");
                                key_txt.setText("");
                                break;
                            case "用户不存在":
                                ShowUserInfoDialog();
                                break;
                        }
                        editor.putString("hbt_data","0+0+0+0+0+0+0");
                        editor.putString("tmp_data","0+0+0+0+0+0+0");
                        editor.putString("diagnose","暂无信息。");
                        editor.putString("tel","120");
                        editor.apply();
                    }
                }
                break;
        }
    }

    /*
     *显示完善用户信息的对话框
     */
    private void ShowUserInfoDialog() {
        user_Info_dg = new CustomDialog_UserInfo(this);
        user_Info_dg.setYesOnclickListener("确定", new CustomDialog_UserInfo.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                if(user_Info_dg.GetBirthYearText().equals("")){
                    Toast.makeText(IdentifyActivity.this, "请输入出生年份", Toast.LENGTH_SHORT).show();
                }else {
                    Calendar calendar=Calendar.getInstance();
                    int this_year=calendar.get(Calendar.YEAR);//获取今年
                    int birth_year=Integer.parseInt(user_Info_dg.GetBirthYearText());
                    if(birth_year>=(this_year-18)||birth_year<=(this_year-65)){
                        Toast.makeText(IdentifyActivity.this, "请输入正确的出生年份", Toast.LENGTH_SHORT).show();
                    }else {
                        String get_data=GetFromTCP("注册"+"+"+user+"+"+key+"+"+user_Info_dg.GetUserInfo());
                        if(get_data.equals("注册成功")){
                            Toast.makeText(IdentifyActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            SaveUser();
                        }
                    }
                }
                user_Info_dg.dismiss();
            }
        });
        user_Info_dg.setNoOnclickListener("取消", new CustomDialog_UserInfo.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                user_Info_dg.dismiss();
            }
        });
        user_Info_dg.show();
    }

    /*
     *设置标题
     */
    protected void SetTitle(String title){
        title_txt.setText(title);
    }

    /*
     *隐藏设置按钮
     */
    protected void HideMenuBtn(){
        menu_btn.setVisibility(View.INVISIBLE);
    }

    /*
     *隐藏返回按钮
     */
    protected void HideBackBtn(){
        back_btn.setVisibility(View.INVISIBLE);
    }

    /*
     *与服务器连接
     */
    private void TCP() throws InterruptedException {
        final String host = "192.168.2.245";
        final int port = 1000;
        final String senddata=sendData;
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    // 1、创建连接
                    socket = new Socket(host,port );
                    if (socket.isConnected()) {
                        Log.d("fuck", "connect to Server success ");
                    }

                    // 2、设置读流的超时时间
                    socket.setSoTimeout(3000);

                    // 3、获取输出流与输入流
                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    // 4、发送信息
                    byte[] sendData = senddata.getBytes(Charset.forName("UTF-8"));
                    outputStream.write(sendData, 0, sendData.length);
                    outputStream.flush();

                    // 5、接收信息
                    byte[] buf = new byte[1024];
                    int len = inputStream.read(buf);
                    String receData = new String(buf, 0, len, Charset.forName("UTF-8"));
                    getData=receData;
                    Log.d("fuck", receData);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("fuck", e.toString());
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                            socket = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
        thread.join();
    }

    /*
     *发送与接受数据
     * @param send_data:发送的数据
     * @return:返回的数据
     */
    protected String GetFromTCP(String send_data){
        try {
            sendData=send_data;
            TCP();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getData;
    }

    /*
     *监听用户是否按下返回键，防止误操作退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 3000) {
                //大于设定时限则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于设定时限则认为是用户确实希望退出程序
                SysApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     *存储用户名
     */
    protected void SaveUser(){
        editor.putString("user",user);
        editor.apply();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }




}
