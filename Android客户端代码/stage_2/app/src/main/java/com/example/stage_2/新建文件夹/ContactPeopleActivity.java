package com.example.stage_2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContactPeopleActivity extends AppCompatActivity implements View.OnClickListener{
    private Button menu_btn;
    private TextView title_txt;
    private Button change_tel_btn;
    private CustomDialog_EnterTel enter_tel_dg;
    private TextView tel_txt;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_people);
        SysApplication.getInstance().addActivity(this);//收集活动
        GetView();
        HideMenuBtn();
        SetTitle("应急联系人");
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
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pref.edit();
        menu_btn=(Button)findViewById(R.id.menu_btn);
        title_txt=(TextView)findViewById(R.id.title_txt);
        change_tel_btn=(Button)findViewById(R.id.change_tel_btn);
        tel_txt=(TextView)findViewById(R.id.tel_txt);
        //获取应急联系电话
        String tel=pref.getString("tel","120");
        //显示应急联系电话
        tel_txt.setText(tel);

    }

    /*
     *添加监听事件
     */
    protected void SetListener(){
        change_tel_btn.setOnClickListener(this);
    }

    /*
     *集中处理监听事件
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.change_tel_btn:
                ShowEnterTelDialog();
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
     *显示“请输入联系电话”对话框
     */
    private void ShowEnterTelDialog(){
        enter_tel_dg = new CustomDialog_EnterTel(this);
        enter_tel_dg.setYesOnclickListener("确定", new CustomDialog_EnterTel.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                String tel=enter_tel_dg.GetText();
                if(!tel.equals("")){
                    tel_txt.setText(tel);
                    //存储应急联系电话
                    editor.putString("tel",tel);
                    editor.apply();
                }
                enter_tel_dg.dismiss();
            }
        });
        enter_tel_dg.setNoOnclickListener("取消", new CustomDialog_EnterTel.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                enter_tel_dg.dismiss();
            }
        });
        enter_tel_dg.show();
    }
}
