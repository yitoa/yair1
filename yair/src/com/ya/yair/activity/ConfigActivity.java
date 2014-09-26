
package com.ya.yair.activity;

import java.util.ArrayList;
import java.util.List;
import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.activity.BaseActivity.TipThread;
import com.ya.yair.util.UdpHelper;
import com.ya.yair.util.UserWebServiceUtil;
import com.ya.yair.util.Util;
import com.ya.yair.util.WebServiceUtil;
import com.ya.yair.util.WifiUtils;
import com.ya.yair.util.WifiUtils.WifiCipherType;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ConfigActivity extends BaseActivity implements OnClickListener {

    private EditText ssidE;

    private EditText passwordE;

    private CheckBox passShowC;

    private Button btn_start;

    private WifiUtils mWifiManager;

    private LinearLayout config_layout;

    private String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EairApplaction.getInstance().addActivity(this);
        setContentView(R.layout.config_device_layout);
        ssidE = (EditText) this.findViewById(R.id.ssid);
        passwordE = (EditText) this.findViewById(R.id.password);
        passShowC = (CheckBox) this.findViewById(R.id.pass_show);
        config_layout = (LinearLayout) this.findViewById(R.id.config_layout);
        passShowC.setOnCheckedChangeListener(new PassShowCheckedChangeListener());
        btn_start = (Button) this.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        mWifiManager = new WifiUtils(this);
        ssidE.setText(mWifiManager.getCurrentSSID());
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("IDbundle");
        userId = bundle.getString("userId");

    }

    private class PassShowCheckedChangeListener implements CheckBox.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (passShowC.isChecked()) {
                // 文本正常显示
                passwordE.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                Editable etable = passwordE.getText();
                Selection.setSelection(etable, etable.length());
            } else {
                // 文本以密码形式显示
                passwordE.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                // 下面两行代码实现: 输入框光标一直在输入文本后面
                Editable etable = passwordE.getText();
                Selection.setSelection(etable, etable.length());
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 点击返回就退出应用
            EairApplaction.getInstance().exit();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_start) {
            boolean bool = Util.isWifi(this);
            if (!bool) {// 用的不是wifi连接
                Toast.makeText(this, this.getResources().getString(R.string.configDevice),
                        Toast.LENGTH_LONG).show();
                return;
            } else {// 开始设备的配置，找到的设备把序列号存储到数据库中
                String password = passwordE.getText().toString();
                if (password == null) {
                    password = "";
                }
                config_layout.setVisibility(View.VISIBLE);
                btn_start.setEnabled(false);
                Thread confingThread = new Thread(new ConfigThread(password));
                confingThread.start();
            }
        }
    }

    private String getLength(String inputssid) {
        int leng = inputssid.length();
        if (leng == 0) {
            return "00";
        } else if (leng < 10) {
            return "0" + leng;
        } else {
            return String.valueOf(leng);
        }
    }

    /****
     * 发送命令获取返回值
     * 
     * @param str
     * @return
     */
    public String getMessage(String str) {
        String message = null;
        try {
            Thread vThread = new Thread(new SendThread(str));
            vThread.start();
            for (;;) {
                if (UdpHelper.result != null) {
                    message = UdpHelper.result;
                    UdpHelper.result = null;
                    interval = 0;
                    break;
                }
                Thread.sleep(500);
                interval += 1;
                if (interval > Constant.WAITTIME) {
                    message = "timeout";
                    interval = 0;// 初始化，恢复0。
                    break;
                }
            }
            vThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public class SendThread implements Runnable {
        private String str;

        public SendThread(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            try {
                udpH.sendSb(Constant.SBADRRESS, Constant.SBPORT, str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class ConfigThread implements Runnable {

        private String password;

        public ConfigThread(String password) {
            this.password = password;
        }

        /***
         * 获取wifi的netId
         * 
         * @param wifiConfis
         * @param ssid
         * @return
         */
        public int getNetId(List<WifiConfiguration> wifiConfis, String ssid) {
            int netid = -1;
            for (int i = 0; i < wifiConfis.size(); i++) {
                WifiConfiguration wc = wifiConfis.get(i);
                if (ssid.equals(wc.SSID.replace("\"", ""))) {
                    netid = wc.networkId;
                    break;
                }
            }
            return netid;
        }

        @Override
        public void run() {
            String inputssid = ssidE.getText().toString();
            List<WifiConfiguration> wifiConfis = mWifiManager.getmWifiConfigurations();
            int netid = getNetId(wifiConfis, inputssid);
            StringBuffer sb = new StringBuffer();
            // 查找设备热点
            List<ScanResult> newWifiList = new ArrayList<ScanResult>();
            List<ScanResult> wifiList = mWifiManager.startScan();
            List<ScanResult> errorList = new ArrayList<ScanResult>();
            for (ScanResult sr : wifiList) {
                String wifiName = sr.SSID;// wifi的名字，根据此名字判读是不是设备的热点
                if (wifiName.contains("YT@WL")) {
                    newWifiList.add(sr);
                }
            }
            // 去连接设备
            if (newWifiList.size() != 0) {
                for (ScanResult sr : newWifiList) {
                    String ssid = sr.SSID;
                    int sbNetId = getNetId(wifiConfis, ssid.replace("\"", ""));
                    String seriNum = ssid.split("@")[1];
                    boolean isconnect = false;
                    if (sbNetId == -1) {
                        isconnect = mWifiManager.Connect(ssid, "",
                                WifiCipherType.WIFICIPHER_NOPASS);
                    } else {
                        isconnect = mWifiManager.connectionWifi(sbNetId);
                    }
                    if (isconnect) {
                        boolean iswificonnect = false;
                        for (int i = 0; i < 20; i++) {
                            iswificonnect = mWifiManager.isWifiConnect();
                            if (iswificonnect) {
                                break;
                            } else {
                                try {
                                    Thread.sleep(1000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // System.out.println("=sbstat===" + sbstat);
                        if (iswificonnect) {// 连接上了，往设备发送信息，让设备去联网去
                            String repMessage = null;
                            // 组装将要发送的信息
                            for (int i = 0; i < 3; i++) {
                                String newseriNum = seriNum.substring(2);
                                String sendMessgae = "wlsn=" + newseriNum + "&ssid"
                                        + getLength(inputssid)
                                        + getLength(password) + "=" + inputssid + password
                                        + "&seraddress=" + Constant.IPADDRESS + "&serport="
                                        + Constant.WPORT + "&cmd";
                                repMessage = ConfigActivity.this.getMessage(sendMessgae);
                                if (repMessage.contains("success")) {
                                    sb.append(newseriNum).append("&");
                                    break;
                                }
                                try {
                                    Thread.sleep(1000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (repMessage == null || "".equals(repMessage)
                                    || repMessage.contains("error")) {
                                errorList.add(sr);
                            } else if ("timeout".equals(repMessage)) {
                                Message msg = new Message();
                                msg.what = 5;
                                msg.obj = ssid;
                                handle.sendMessage(msg);
                                return;
                            }

                        } else {// 没有连上需要跳到wifi页面进行手动连接
                            Message msg = new Message();
                            msg.what = 5;
                            msg.obj = ssid;
                            handle.sendMessage(msg);
                            return;
                        }

                    } else {
                        // 没有配置成功的
                        errorList.add(sr);
                    }
                    // 删除已经连接好的netId;
                    mWifiManager.removeNetwork(ssid.replace("\"", ""));
                }

            } else {
                Message msg = new Message();
                msg.what = 1;
                handle.sendMessage(msg);
                return;
            }
            // l连接完以后把刚开始的网络连接上
            mWifiManager.connectionWifi(netid);
            boolean yisconnect = false;
            for (int i = 0; i < 50; i++) {
                yisconnect = mWifiManager.isWifiConnect();
                if (yisconnect) {
                    break;
                } else {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (newWifiList.size() == errorList.size()) {// 全部配置错误，需要重新配置
                Message msg = new Message();
                msg.what = 2;
                handle.sendMessage(msg);
                return;
            } else {
                String mess = sb.toString().substring(0, sb.length() - 1);
                UserWebServiceUtil uws = new UserWebServiceUtil();
                String rep = null;
                for (int i = 0; i < 10; i++) {
                    rep = uws.updUserId(userId, mess);
                    if (rep != null) {
                        break;
                    }
                    try {
                        Thread.sleep(Constant.LSLEEPTIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (rep == null) {// 没有找到设备，需要重新配置
                    Message msg = new Message();
                    msg.what = 2;
                    handle.sendMessage(msg);
                    return;
                } else {
                    WebServiceUtil wsu = new WebServiceUtil();
                    String repResult = null;
                    for (int i = 0; i < 10; i++) {
                        repResult = wsu.querySbByUserId(userId);
                        if (repResult != null) {
                            break;
                        }
                        try {
                            Thread.sleep(Constant.LSLEEPTIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (repResult != null) {
                        Intent intent = new Intent(ConfigActivity.this, DeviceListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("sb", repResult);
                        bundle.putString("userId", userId);
                        intent.putExtra("sbbundle", bundle);
                        ConfigActivity.this.finish();
                        ConfigActivity.this.startActivity(intent);
                    } else {// 需要重新配置啊
                        Message msg = new Message();
                        msg.what = 2;
                        handle.sendMessage(msg);
                        return;
                    }
                }
            }
        }

    }

    Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            btn_start.setEnabled(true);
            switch (msg.what) {
                case 1:
                    Toast.makeText(ConfigActivity.this,
                            ConfigActivity.this.getResources().getString(R.string.notsb),
                            Toast.LENGTH_LONG).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    Toast.makeText(ConfigActivity.this,
                            ConfigActivity.this.getResources().getString(R.string.configFail),
                            Toast.LENGTH_LONG).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    Toast.makeText(ConfigActivity.this,
                            ConfigActivity.this.getResources().getString(R.string.passwordfail),
                            Toast.LENGTH_LONG).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    Toast.makeText(ConfigActivity.this,
                            ConfigActivity.this.getResources().getString(R.string.passwordfail),
                            Toast.LENGTH_LONG).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    Object o = msg.obj;
                    String ssid = o.toString();
                    String str = ConfigActivity.this.getResources().getString(R.string.handlelj);
                    str = str.replace("%1$", ssid);
                    Toast.makeText(ConfigActivity.this, str, Toast.LENGTH_SHORT).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    new skipThread().start();
                    break;

            }
        }

    };

    public class skipThread extends Thread {

        @Override
        public void run() {
            // 暂停5s
            try {
                Thread.sleep(5000);
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
