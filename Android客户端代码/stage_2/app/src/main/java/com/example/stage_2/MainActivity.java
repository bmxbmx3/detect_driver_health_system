package com.example.stage_2;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button back_btn;
    private Button start_btn;
    private Button sevendays_before_btn;
    private Button bluetooth_btn;
    private long mExitTime;//获取点按返回键时的当前系统时间
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Calendar calendar;
    //↓蓝牙
    private String target_device_name=null;
    private static final String TAG = "health";
    private static final boolean DEBUG = false;
    public static final int REC_DATA = 2;
    public static final int CONNECTED_DEVICE_NAME = 4;
    public static final int BT_TOAST = 5;
    public static final int MAIN_TOAST = 6;
    // 标志字符串常量
    public static final String DEVICE_NAME = "device name";
    public static final String TOAST = "toast";
    // 意图请求码
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private Button bluetoothBtn;
    // 已连接设备的名字
    private String mConnectedDeviceName = null;
    //蓝牙连接服务对象
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mConnectService = null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    //↑蓝牙

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(this);//收集活动
        InitParam();
        HideBackBtn();
        SetListener();

    }


    /*
     *隐藏返回按钮
     */
    protected void HideBackBtn(){
        back_btn.setVisibility(View.INVISIBLE);
    }

    /*
     *获取控件
     */
    protected void InitParam(){
        back_btn=(Button)findViewById(R.id.back_btn);
        start_btn=(Button)findViewById(R.id.start_btn);
        sevendays_before_btn=(Button)findViewById(R.id.sevendays_before_btn);
        bluetooth_btn=(Button)findViewById(R.id.bluetooth_btn);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        calendar= Calendar.getInstance();
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pref.edit();
        //清空蓝牙连接状态
        editor.putString("bluetooth_connection","no");
        editor.apply();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /*
     *添加监听事件
     */
    protected void SetListener(){
        start_btn.setOnClickListener(this);
        bluetooth_btn.setOnClickListener(this);
        sevendays_before_btn.setOnClickListener(this);
    }

    /*
     *集中处理监听事件
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.start_btn:
                if(!CheckNetwork.isNetworkAvailable(this)){
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }else {
                    if(pref.getString("bluetooth_connection","no").equals("no")){
                        Toast.makeText(this, "请连接蓝牙设备BT04-A", Toast.LENGTH_SHORT).show();
                    }else {
                        //↓蓝牙
                        sendMessage(start_btn,"close");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //↑蓝牙
                        Intent intent_1=new Intent(this,RunDectectActivity.class);
                        startActivity(intent_1);
                    }
                }
                break;
            case R.id.bluetooth_btn:
                //↓蓝牙
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                    return;
                }
                // 打开设备蓝牙设备列表活动
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                //↑蓝牙
                break;
            case R.id.sevendays_before_btn:
                Intent intent_2=new Intent(this,SevendaysBeforeActivity.class);
                startActivity(intent_2);
                break;
        }
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


    @Override
    public void onStart() {
        super.onStart();
        if(DEBUG) Log.i(TAG, "++ ON START ++");
        // 查看请求打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        } //否则创建蓝牙连接服务对象
        else if (mConnectService == null){
            mConnectService = new BluetoothService(mHandler);
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (mConnectService != null) {
            if (mConnectService.getState() == BluetoothService.IDLE) {
                //监听其他蓝牙主设备
                mConnectService.acceptWait();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //↓蓝牙
        if(DEBUG) Log.e(TAG, "onDestroy");
        //停止蓝牙连接
        if (mConnectService != null) mConnectService.cancelAllBtThread();
        android.os.Process.killProcess(android.os.Process.myPid());
        //↑蓝牙
    }

    /**
     * 通过蓝牙发送字符串
     * @param  Str2Send 欲发送的字符串.
     */
    private void sendMessage(Button callButton, String Str2Send) {
        //↓蓝牙
        if(callButton!=null){
            if (mConnectService==null||mConnectService.getState()!=BluetoothService.CONNECTED) {
                Toast.makeText(this,"未连接到任何蓝牙设备", Toast.LENGTH_SHORT).show();
                return;
            }
        }else if(Str2Send==null||mConnectService==null||Str2Send.equals(""))return;
        byte[] bs;
        bs = Str2Send.getBytes();
        mConnectService.write(bs);
        //↑蓝牙
    }

    // 用于从蓝牙获取信息的Handler对象
    private final Handler mHandler = new Handler(){
        StringBuffer sb=new StringBuffer();
        byte[] bs;
        int i;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REC_DATA:
                    bs=(byte[])msg.obj;
                    char[] c=new char[msg.arg1];
                    for(i=0;i<msg.arg1;i++){
                        c[i]=(char)(bs[i]&0xff);
                        sb.append(c[i]);
                        if(c[i]=='#'){
                            String measure_data="测量+"+pref.getString("user","")+"+"+
                                    sb.toString().replace("#","")+"+"+GetFormatTime();
                            //暂时存储测量的数据
                            editor.putString("measure_data",measure_data);
                            editor.apply();
                            //清空字符串缓存
                            sb.delete(0,sb.length());
                        }
                    }
                    break;
                case CONNECTED_DEVICE_NAME:
                    // 提示已连接设备名
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "已连接到"
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //只能与“BT04-A”蓝牙连接
                    if(mConnectedDeviceName.equals("BT04-A")){
                        editor.putString("bluetooth_connection","yes");
                    }else {
                        editor.putString("bluetooth_connection","no");
                    }
                    editor.apply();
                    break;
                case BT_TOAST:
                    if(mConnectedDeviceName!=null)
                        Toast.makeText(getApplicationContext(), "与"+mConnectedDeviceName+
                                msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getApplicationContext(), "与"+target_device_name+
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    mConnectedDeviceName=null;
                    break;
                case MAIN_TOAST:
                    Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //↓蓝牙
        Log.e(TAG, "onActivityResult");
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    // 提取蓝牙地址数据
                    String address = data.getExtras().getString(DeviceListActivity.DEVICE_ADDRESS);
                    // 获取设备
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    target_device_name=device.getName();
                    if(target_device_name.equals(mConnectedDeviceName)){
                        Toast.makeText(this, "已连接"+mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 提示正在连接设备
                    Toast.makeText(this, "正在连接"+target_device_name, Toast.LENGTH_SHORT).show();
                    // 连接设备
                    mConnectService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // 请求打开蓝牙被用户拒绝时提示
                if (resultCode == Activity.RESULT_OK) {
                    mConnectService = new BluetoothService(mHandler);
                } else {
                    Toast.makeText(this,"拒绝打开蓝牙", Toast.LENGTH_SHORT).show();
                }
        }
        //↑蓝牙
    }

    /*
     *获取测量时的时间
     * @return:一定格式的表示时间的字符串
     */
    protected String GetFormatTime(){
        calendar.setTime(new Date());
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int second=calendar.get(Calendar.SECOND);
        int minute=calendar.get(Calendar.MINUTE);
        return String.format("%02d",year)+"-"+String.format("%02d",month)+"-"+String.format("%02d",day)
                +" "+String.format("%02d",hour)+":"+String.format("%02d",minute)+":"+String.format("%02d",second);
    }


}
