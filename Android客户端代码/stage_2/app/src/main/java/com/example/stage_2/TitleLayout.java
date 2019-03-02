package com.example.stage_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class TitleLayout extends ConstraintLayout implements View.OnClickListener{
    Button back_btn;
    Button menu_btn;
    public TitleLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.actionbar_custom,this);
        InitParam();
        SetListener();
    }

    /*
     *获取控件
     */
    protected void InitParam(){
        back_btn=(Button)findViewById(R.id.back_btn);
        menu_btn=(Button)findViewById(R.id.menu_btn);
    }

    /*
     *添加监听事件
     */
    protected void SetListener(){
        back_btn.setOnClickListener(this);
        menu_btn.setOnClickListener(this);
    }

    /*
     *集中处理监听事件
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back_btn:
                ((Activity)getContext()).finish();
                break;
            case R.id.menu_btn:
                Intent intent=new Intent(getContext(),MenuActivity.class);
                getContext().startActivity(intent);
                break;
        }
    }
}
