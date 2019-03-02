package com.example.stage_2;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class MyMarkerView extends MarkerView {
    private TextView tvContent;
    private String date[];
    private int index;//所选指标
    String data_format;//数据格式
    String unit;//单位
    public MyMarkerView(Context context, int layoutResource,String[] date_data,int index_data) {
        super(context, layoutResource);
        date=date_data;
        index=index_data;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }




    /*
     *设置点按数据点时显示的信息
     */
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        switch (index){
            case 0:
                data_format=(int)e.getY()+"";
                unit="次";
                break;
            case 1:
                data_format=(float)(Math.round(e.getY()*10))/10+"";
                unit="℃";
                break;
        }
        tvContent.setText(data_format+unit+"\n"+date[(int)e.getX()-1]);

        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight()-20);
        }

        return mOffset;
    }

}
