
package com.ya.yair.activity;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.util.WebServiceUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private CheckBox remeberBox;

    private SharedPreferences storeSP;

    private EditText userNameE;

    private EditText passwordE;

    private TextView registerT;

    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EairApplaction.getInstance().addActivity(this);
        setContentView(R.layout.login_layout);
        remeberBox = (CheckBox) this.findViewById(R.id.remeber_password);
        userNameE = (EditText) this.findViewById(R.id.user_name);
        passwordE = (EditText) this.findViewById(R.id.password);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        // 注册按钮
        registerT = (TextView) this.findViewById(R.id.register);
        storeSP = this.getSharedPreferences(Constant.STOREDB, 0);
        String isMemory = storeSP.getString("isMemory", "NO");
        // 进入界面时，这个if用来判断SharedPreferences里面name和password有没有数据，有的话则直接打在EditText上面
        if (isMemory.equals("YES")) {
            String name = storeSP.getString("name", "");
            String password = storeSP.getString("password", "");
            userNameE.setText(name);
            passwordE.setText(password);
            remeberBox.setChecked(true);
        }

        registerT.setOnClickListener(this);

        btn_login.setOnClickListener(this);

        remeberBox.setOnCheckedChangeListener(new RemeberBoxCheckedChangeListener());

    }

    private class RemeberBoxCheckedChangeListener implements CheckBox.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            if (remeberBox.isChecked()) {
                Editor edit = storeSP.edit();
                edit.putString("name", userNameE.getText().toString());
                edit.putString("password", passwordE.getText().toString());
                edit.putString("isMemory", "YES");
                edit.commit();
            } else if (!remeberBox.isChecked()) {
                Editor edit = storeSP.edit();
                edit.putString("isMemory", "NO");
                edit.commit();
            }

        }

    }

    @Override
    public void onClick(View v) {
        if (v == registerT) {
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
            this.finish();
        } else if (v == btn_login) {
            String username = userNameE.getText().toString();
            String password = passwordE.getText().toString();
            if (username == null || "".equals(username)) {
                Toast.makeText(this, this.getResources().getString(R.string.notusername),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (password == null || "".equals(password)) {
                Toast.makeText(this, this.getResources().getString(R.string.notpassword),
                        Toast.LENGTH_LONG).show();
                return;
            }
            WebServiceUtil wsu = new WebServiceUtil();
            String userId = wsu.login(username, password);
            if (userId == null) {// 尝试访问2次，确保登陆成功啊
                try {
                    Thread.sleep(Constant.SLEEPTIME);
                    userId = wsu.login(username, password);
                    if (userId == null) {
                        Thread.sleep(Constant.SLEEPTIME);
                        userId = wsu.login(username, password);
                    }
                } catch (InterruptedException e) {
                   
                    e.printStackTrace();
                }
            }
            if (userId == null) {
                Toast.makeText(this, this.getResources().getString(R.string.loginfail),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if ("0".equals(userId)) {// 登陆失败
                Toast.makeText(this, this.getResources().getString(R.string.notUser),
                        Toast.LENGTH_LONG).show();
                return;
            } else {// 登陆成功
                String ss = wsu.querySbByUserId(userId);
                if (ss == null || "0".equals(ss)) {// 没有设备到配置页面去，需要把用户的userId带过去以便用来保存
                    Intent intent = new Intent(this, ConfigActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId);
                    intent.putExtra("IDbundle", bundle);
                    this.finish();
                    this.startActivity(intent);
                } else {// 到列表页面
                    Intent intent = new Intent(this, DeviceListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("sb", ss);
                    bundle.putString("userId", userId);
                    intent.putExtra("sbbundle", bundle);
                    this.finish();
                    this.startActivity(intent);
                }
            }

        }

    }

}
