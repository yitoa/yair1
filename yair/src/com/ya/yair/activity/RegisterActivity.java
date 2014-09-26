
package com.ya.yair.activity;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.util.UserWebServiceUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private EditText userE;

    private EditText passwordE;

    private EditText confim_passwordE;

    private EditText phone_numE;
    
    private EditText emailE;

    private Button btn_RegisterB;

    private Button btn_regback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EairApplaction.getInstance().addActivity(this);
        setContentView(R.layout.register_layout);
        userE = (EditText) this.findViewById(R.id.user_name);
        passwordE = (EditText) this.findViewById(R.id.password);
        confim_passwordE = (EditText) this.findViewById(R.id.confim_password);
        phone_numE = (EditText) this.findViewById(R.id.phone_num);
        emailE=(EditText) this.findViewById(R.id.email);
        btn_RegisterB = (Button) this.findViewById(R.id.btn_Register);
        btn_RegisterB.setOnClickListener(this);
        btn_regback = (Button) this.findViewById(R.id.regback);
        btn_regback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    	
    	if (v == btn_regback) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            RegisterActivity.this.startActivity(intent);
            RegisterActivity.this.finish();
    	}
    	
        String reg = "^[0-9a-zA-Z_]{6,12}$";// 6-12字母和数据的
        if (v == btn_RegisterB) {
            String username = userE.getText().toString();
            if (username == null || "".equals(username)) {
                Toast.makeText(this, this.getResources().getString(R.string.notusername),
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                boolean bool = username.matches(reg);
                if (!bool) {
                    Toast.makeText(this, this.getResources().getString(R.string.inconusername),
                            Toast.LENGTH_LONG).show();
                    userE.setText("");
                    return;
                }
            }
            String password = passwordE.getText().toString();
            if (password == null || "".equals(password)) {
                Toast.makeText(this, this.getResources().getString(R.string.notpassword),
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                boolean bool = password.matches(reg);
                if (!bool) {
                    Toast.makeText(this, this.getResources().getString(R.string.inconpassword),
                            Toast.LENGTH_LONG).show();
                    passwordE.setText("");
                    return;
                }
            }
            String confim_password = confim_passwordE.getText().toString();
            if (confim_password == null || "".equals(confim_password)) {
                Toast.makeText(this, this.getResources().getString(R.string.notcofigpassword),
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                boolean bool = confim_password.matches(reg);
                if (!bool) {
                    Toast.makeText(this,
                            this.getResources().getString(R.string.inconconfigpassword),
                            Toast.LENGTH_LONG).show();
                    confim_passwordE.setText("");
                    return;
                }
            }
            if (!(password.equals(confim_password))) {
                Toast.makeText(this, this.getResources().getString(R.string.notyz),
                        Toast.LENGTH_LONG).show();
                confim_passwordE.setText("");
                return;
            }

            String phone_num = phone_numE.getText().toString();
            if (phone_num == null || "".equals(phone_num)) {
                Toast.makeText(this, this.getResources().getString(R.string.notphonenum),
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                String phoneExg = "^1\\d{10}$";
                boolean bool = phone_num.matches(phoneExg);
                if (!bool) {
                    Toast.makeText(this, this.getResources().getString(R.string.inconphonenum),
                            Toast.LENGTH_LONG).show();
                    phone_numE.setText("");
                    return;
                }
            }
            String email=emailE.getText().toString();
            UserWebServiceUtil wsu = new UserWebServiceUtil();
            String replyMess = wsu.addUsers(username, password, phone_num,email);
            if (replyMess == null) {
                try {
                    Thread.sleep(Constant.SLEEPTIME);
                    replyMess = wsu.addUsers(username, password, phone_num,email);
                    if (replyMess == null) {
                        Thread.sleep(Constant.SLEEPTIME);
                        replyMess = wsu.addUsers(username, password, phone_num,email);
                    }
                } catch (InterruptedException e) {
                    
                    e.printStackTrace();
                }

            }
            if ("1".equals(replyMess)) {// 注册成功跳到登陆界面
                this.finish();
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
            } else if ("0".equals(replyMess)) {// 注册没有成功
                Toast.makeText(this, this.getResources().getString(R.string.notReg),
                        Toast.LENGTH_LONG).show();
                return;
            } else if ("2".equals(replyMess)) {// 改用户名已经存在了，更换一个
                Toast.makeText(this, this.getResources().getString(R.string.existUser),
                        Toast.LENGTH_LONG).show();
                return;
            }

        }
    }
}
