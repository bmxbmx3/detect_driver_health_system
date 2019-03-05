package com.example.stage_2;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SevendaysBeforeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button keep_btn;
    private String TAG="SevendaysActivity";
    private LineChart chart;
    private ViewPager viewPager;
    private float data_1[]={60f,70f,80f,90f,100f,90f,80f};//数据1
    private float data_2[]={36.3f,36.5f,36.7f,36.9f,36.5f,36.7f,36.8f};//数据2
    private String[] date=new String[7];//最近七天的日期
    private int month;//月
    private int day_of_month;//日
    private Button menu_btn;
    private TextView title_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sevendays_before);
        SysApplication.getInstance().addActivity(this);//收集活动
        SetSevenDatesBefore();
        RenderViewPager();
        RenderChart(data_1,date,0);
        RendererChartFromIndexSelected();
        GetView();
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
    protected void GetView(){
        menu_btn=(Button)findViewById(R.id.menu_btn);
        title_txt=(TextView)findViewById(R.id.title_txt);
        keep_btn=(Button)findViewById(R.id.keep_btn);
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
                Toast.makeText(this, "正在同步", Toast.LENGTH_SHORT).show();
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
                        RenderChart(data_1,date,0);
                        break;
                    case 1:
                        RenderChart(data_2,date,1);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
     *初始化最近七天的日期
     */
    protected void SetSevenDatesBefore(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -8);//从当前时间倒退8天
        for(int i=1;i<=7;i=i+1){
            calendar.add(Calendar.DAY_OF_MONTH, +1);
            month =calendar.get(Calendar.MONTH)+1;
            day_of_month=calendar.get(Calendar.DAY_OF_MONTH);
            date[i-1]=month+"月"+day_of_month+"日";
        }
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
}
