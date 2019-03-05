package com.example.stage_2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CustomDialog_UserInfo extends Dialog {

    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView title_dg_txt;//消息标题文本
    private String titleStr;//从外界设置的title文本
    private EditText birth_txt;//出生年份
    private RadioGroup radioGroup;
    private RadioButton male_radio,female_radio;//性别选择
    private String sex="男";//性别

    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    //RadioGroup用OnCheckedChangeListener来运行
    private RadioGroup.OnCheckedChangeListener change_radio = new
            RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    if(checkedId==male_radio.getId())
                    {
                        sex="男";
                    }
                    else if(checkedId==female_radio.getId())
                    {
                        sex="女";
                    }
                }
            };

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public CustomDialog_UserInfo(Context context) {
        super(context,R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_info);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title
        if (titleStr != null) {
            title_dg_txt.setText(titleStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.yes_btn);
        no = (Button) findViewById(R.id.no_btn);
        title_dg_txt = (TextView) findViewById(R.id.title_dg_txt);
        birth_txt=(EditText)findViewById(R.id.birth_txt);
        radioGroup=(RadioGroup)findViewById(R.id.radio_group);
        male_radio=(RadioButton)findViewById(R.id.male_radio);
        female_radio=(RadioButton)findViewById(R.id.female_radio);
        radioGroup.setOnCheckedChangeListener(change_radio);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

    /*
     *返回输入出生年份的内容
     */
    public String GetBirthYearText(){
        return birth_txt.getText().toString();
    }

    /*
     *返回用户完善的信息
     */
    public String GetUserInfo(){
        return sex+"+"+birth_txt.getText().toString();
    }



}
