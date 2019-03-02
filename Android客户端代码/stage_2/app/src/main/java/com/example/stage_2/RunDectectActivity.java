package com.example.stage_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class RunDectectActivity extends AppCompatActivity implements View.OnClickListener{
    private Button menu_btn;
    private TextView title_txt;
    private Button over_btn;
    private Chronometer chronometer;
    private Button back_btn;
    private Handler handler;
    private Runnable runnable;
    private SpeechSynthesizer mTts;
    private CustomDialog_Msg warning_dg;
    private SharedPreferences pref;
    private String tel;
    private String sendData;//向服务器发送的数据
    private String getData;//从服务器接受的数据
    private long dt;//获取计时器已经运行的时间
    //构造科大讯飞在线语音库的对象
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_dectect);
        SysApplication.getInstance().addActivity(this);//收集活动
        InitParam();
        HideMenuBtn();
        SetTitle("运行检测");
        SetListener();
        StartTimer();
        SetVoice();
        StartRunning();

    }

    /*
     *隐藏设置按钮
     */
    protected void HideMenuBtn(){
        menu_btn.setVisibility(View.INVISIBLE);
    }

    /*
     *获取控件
     */
    protected void InitParam(){
        menu_btn=(Button)findViewById(R.id.menu_btn);
        title_txt=(TextView)findViewById(R.id.title_txt);
        over_btn=(Button)findViewById(R.id.over_btn);
        chronometer=(Chronometer)findViewById(R.id.time_show_chm);
        back_btn=(Button)findViewById(R.id.back_btn);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        tel=pref.getString("tel","120");
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
            String get_data=GetFromTCP(pref.getString("measure_data","no_data"));
            Speak(get_data);
            if(get_data.contains("异常")){
                StopClock();
                ShowWarningDialog();
                return;
            }
            handler.postDelayed(this, 5000);
            }
        };
    }

    /*
     *启动定时器
     */
    protected void StartTimer(){
        int hour = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000 / 60);
        chronometer.setFormat("0"+String.valueOf(hour)+":%s");
        StartClock();
    }

    /*
     *设置标题
     */
    protected void SetTitle(String title){
        title_txt.setText(title);
    }

    /*
     *跳转到拨号界面，同时传递电话号码
     * @param tel:电话号码
     */
    protected void Call(String number){
        Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(dialIntent);
    }

    /*
     *添加监听事件
     */
    protected void SetListener(){
        over_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    /*
     *集中处理监听事件
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.over_btn:
                StopClock();
                StopRunning();
                this.finish();
                break;
            case R.id.back_btn:
                StopClock();
                StopRunning();
                this.finish();
                break;
        }
    }

    /*
     *语音设置初始化
     */
    protected void SetVoice() {
        //用在科大讯飞注册的appid启动语音对象
        SpeechUtility.createUtility(getApplicationContext(), "appid=5b044cbc");
        //创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(getApplicationContext(), null);
        //发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //调节语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //调节音量
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        //连接网络
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    /*
     *播报语音
     * @param voice:所要播报的文字
     */
    protected void Speak(String voice) {
        //合成语音
        mTts.startSpeaking(voice, mSynListener);
    }

    /*
     *启动进程
     *@param dalay:延迟时间
     */
    protected void StartRunning(){
        handler.postDelayed(runnable, 3 *1000);
    }

    /*
     *结束进程
     */
    protected void StopRunning(){
        handler.removeCallbacks(runnable);
    }

    /*
     *显示“警告”对话框
     */
    protected void ShowWarningDialog(){
        warning_dg = new CustomDialog_Msg(this);
        warning_dg.setYesOnclickListener("确定", new CustomDialog_Msg.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                Call(tel);
                //关闭对话框
                warning_dg.dismiss();
            }
        });
        warning_dg.setNoOnclickListener("取消", new CustomDialog_Msg.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                StartClock();
                handler.postDelayed(runnable, 0);
                warning_dg.dismiss();
            }
        });
        warning_dg.show();
    }

    /*
     *启动计时器
     */
    protected void StartClock(){
        //跳过已经记录了的时间
        dt=SystemClock.elapsedRealtime() - dt;
        chronometer.setBase(dt);
        chronometer.start();
    }

    /*
     *停止计时器
     */
    protected void StopClock(){
        chronometer.stop();
        //保存这次记录了的时间
        dt=SystemClock.elapsedRealtime()- chronometer.getBase();
    }

    /*
     *监听用户是否按下返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StopClock();
            StopRunning();
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
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
                    socket.setSoTimeout(30000);

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


}


