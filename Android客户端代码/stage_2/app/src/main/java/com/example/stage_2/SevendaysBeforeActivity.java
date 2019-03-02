package com.example.stage_2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class SevendaysBeforeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button keep_btn;
    private String TAG="SevendaysActivity";
    private LineChart chart;
    private ViewPager viewPager;
    private TextView diagnose_txt;
    private float hbt_data[]=new float[7];//心率数据
    private float tmp_data[]=new float[7];//体温数据
    private String[] date=new String[7];//最近七天的日期
    private int month;//月
    private int day_of_month;//日
    private Button menu_btn;
    private TextView title_txt;
    private String sendData;//向服务器发送的数据
    private String getData;//从服务器接受的数据
    private Calendar calendar;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int current_position;//当前指标的位置
    private ScrollView scroll_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sevendays_before);
        SysApplication.getInstance().addActivity(this);//收集活动
        RenderViewPager();
        InitParam();
        RenderChart(hbt_data,date,0);
        RendererChartFromIndexSelected();
        HideMenuBtn();
        SetTitle("七天记录");
        SetListener();


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
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pref.edit();
        diagnose_txt=(TextView)findViewById(R.id.diagnose_txt);
        diagnose_txt.setText(pref.getString("diagnose","暂无信息。"));
        current_position=0;
        scroll_view=(ScrollView)findViewById(R.id.scroll_view);
        menu_btn=(Button)findViewById(R.id.menu_btn);
        title_txt=(TextView)findViewById(R.id.title_txt);
        keep_btn=(Button)findViewById(R.id.keep_btn);
        calendar=Calendar.getInstance();
        InitChartData();
    }

    /*
     *添加监听事件
     */
    protected void SetListener(){
        keep_btn.setOnClickListener(this);
    }

    /*
     *集中处理监听事件
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.keep_btn:
                if(!CheckNetwork.isNetworkAvailable(this)){
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }else {
                    String get_data=GetFromTCP("同步"+"+"+pref.getString("user",""));
                    if(get_data.contains("同步成功")){
                        Toast.makeText(this, "同步成功", Toast.LENGTH_SHORT).show();
                        RenderChartFromString(get_data);
                    }else {
                        Toast.makeText(this, "同步失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /*
     *设置标题
     */
    protected void SetTitle(String title){
        title_txt.setText(title);
    }

    /*
     *渲染左右滑动的指标
     */
    protected void RenderViewPager(){
        viewPager = (ViewPager) findViewById(R.id.vpg);
        viewPager.setAdapter(new CustomPagerAdapter(this));
    }

    /*
     *通过监听当前所选的指标，渲染相应图表
     */
    protected void RendererChartFromIndexSelected(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        RenderChart(hbt_data,date,0);
                        break;
                    case 1:
                        RenderChart(tmp_data,date,1);
                        break;
                }
                current_position=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /*
     *渲染图表
     * @param data:输入图表的数据
     * @param date:图表横坐标的日期
     * @param index:所选指标（0表示心率，1表示体温）
     */
    protected void RenderChart(float[] data,String[] date,int index){
        chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();
        for (int i=1;i<=7;i++) {
            entries.add(new Entry(i, data[i-1]));
        }
        LineDataSet dataSet = new LineDataSet(entries,"");
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.WHITE);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setCircleRadius(6f);
        dataSet.setDrawValues(false);
        dataSet.setDrawHorizontalHighlightIndicator(false);//禁止选择数据点时表示焦点的十字线的横线出现
        dataSet.setDrawVerticalHighlightIndicator(false);//禁止选择数据点时表示焦点的十字线的纵线出现
        LineData lineData = new LineData(dataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0.5f);
        xAxis.setAxisMaximum(7.5f);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setSpaceTop(45f);//设置图表最高点的上边距
        leftAxis.setSpaceBottom(10f);//设置图表最低点的下边距
        chart.animateX(1000, Easing.EasingOption.Linear);//显示动画
        chart.setData(lineData);//放置数据
        chart.getAxisLeft().setEnabled(false);//隐藏左纵坐标轴
        chart.getAxisRight().setEnabled(false);//隐藏右纵坐标轴
        chart.getXAxis().setEnabled(false);//隐藏横坐标轴
        chart.getLegend().setEnabled(false);//关闭图例
        chart.setScaleEnabled(false);//关闭缩放操作
        chart.getDescription().setEnabled(false);//关闭图注
        IMarker marker = new MyMarkerView(this,R.layout.marker,date,index);//设置弹出的数据信息的格式
        chart.setMarker(marker);//选择数据点时弹出数据信息
        chart.notifyDataSetChanged();//提醒图表更新数据
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

    /*
     *获取最近七天的日期
     */
    protected void SetSevenDaysBefore(){
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -8);
        for(int i=0;i<=6;i=i+1){
            calendar.add(Calendar.DAY_OF_MONTH, +1);
            month =calendar.get(Calendar.MONTH)+1;
            day_of_month=calendar.get(Calendar.DAY_OF_MONTH);
            date[i]=month+"月"+day_of_month+"日";
        }
    }

    /*
     *初始化图表数据
     */
    protected void InitChartData(){
        String hbt_getdata=pref.getString("hbt_data","0+0+0+0+0+0+0");
        String tmp_getdata=pref.getString("tmp_data","0+0+0+0+0+0+0");
        if(tmp_getdata.equals("0+0+0+0+0+0+0")){
            SetSevenDaysBefore();
        }else {
            String date_getdata=pref.getString("date_data","1月1日+1月2日+1月3日+1月4日+1月5日+1月6日+1月7日");
            String[] date_array=date_getdata.split("\\+");
            System.arraycopy(date_array,0,date,0,date_array.length);//数组复制
        }
        String[] hbt_array=hbt_getdata.split("\\+");
        String[] tmp_array=tmp_getdata.split("\\+");
        for(int i=0;i<=6;i++){
            hbt_data[i]=Float.parseFloat(hbt_array[i]);
            tmp_data[i]=Float.parseFloat(tmp_array[i]);
        }
    }

    /*
     *从服务器接收的数据渲染图表
     */
    protected void RenderChartFromString(String data){
        String[] data_array=data.split("\\+");
        String hbt_array="";
        String tmp_array="";
        String date_array="";
        for(int i=1;i<=7;i++){
            date[i-1]=data_array[i];
            if(i<7){
                date_array=date_array+data_array[i]+"+";
            }else {
                date_array=date_array+data_array[i];
            }
        }
        for(int i=8;i<=14;i++){
            hbt_data[i-8]=Float.parseFloat(data_array[i]);
            if(i<14){
                hbt_array=hbt_array+data_array[i]+"+";
            }else {
                hbt_array=hbt_array+data_array[i];
            }
        }
        for(int i=15;i<=21;i++){
            tmp_data[i-15]=Float.parseFloat(data_array[i]);
            if(i<21){
                tmp_array=tmp_array+data_array[i]+"+";
            }else {
                tmp_array=tmp_array+data_array[i];
            }
        }
        diagnose_txt.setText(data_array[22]);//更新诊断结果
        editor.putString("date_data",date_array);
        editor.putString("hbt_data",hbt_array);
        editor.putString("tmp_data",tmp_array);
        editor.putString("diagnose",data_array[22]);
        editor.apply();
        switch (current_position){
            case 0:
                RenderChart(hbt_data,date,0);
                break;
            case 1:
                RenderChart(tmp_data,date,1);
                break;
        }
        //回到界面顶部
        scroll_view.post(new Runnable() {
            @Override
            public void run() {
                scroll_view.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }
}
