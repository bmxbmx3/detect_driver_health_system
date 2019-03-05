package com.example.stage_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity{
    private Button menu_btn;
    private TextView title_txt;
    private String[] menu_name_list={"应急联系人"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SysApplication.getInstance().addActivity(this);//收集活动
        SetRecyclerView();
        GetView();
        HideMenuBtn();
        SetTitle("设置");

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
    }

    /*
     *设置标题
     */
    protected void SetTitle(String title){
        title_txt.setText(title);
    }
    
    /*
     *添加菜单
     */
    protected void SetRecyclerView(){
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MenuAdapter adapter=new MenuAdapter(menu_name_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));//添加分隔线
        recyclerView.setAdapter(adapter);
    }
}
