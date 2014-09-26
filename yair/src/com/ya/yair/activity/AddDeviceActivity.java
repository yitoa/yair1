
package com.ya.yair.activity;

import java.util.List;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.activity.ConfigActivity.skipThread;
import com.ya.yair.util.UdpHelper;
import com.ya.yair.util.UserWebServiceUtil;
import com.ya.yair.util.Util;
import com.ya.yair.util.WebServiceUtil;
import com.ya.yair.util.WifiUtils;
import com.ya.yair.util.WifiUtils.WifiCipherType;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddDeviceActivity extends BaseActivity {

    private EditText qr_infoE;
    private Button btn_qrB;
    private WifiUtils mWifiManager;
    private EditText addPasswordE;
    private CheckBox pass_showC;
    private LinearLayout config_layout;
    private SharedPreferences storeSP;
    private String userId;
    private Button btn_addback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EairApplaction.getInstance().addActivity(this);
        setContentView(R.layout.upload_device_info_layout);
        btn_addback = (Button) this.findViewById(R.id.addback);
        btn_addback.setOnClickListener(new AddBackListener());
        Bundle bundle = this.getIntent().getBundleExtra("uBundle");
        userId = (String) bundle.get("userId");
        mWifiManager = new WifiUtils(this);
        qr_infoE = (EditText) this.findViewById(R.id.qr_info);
        addPasswordE = (EditText) this.findViewById(R.id.addPassword);
        pass_showC = (CheckBox) this.findViewById(R.id.pass_show);
        btn_qrB = (Button) this.findViewById(R.id.btn_upload);
        btn_qrB.setOnClickListener(new AddDeviceListener(userId));
        pass_showC.setOnCheckedChangeListener(new PassShowCheckedChangeListener());
        config_layout = (LinearLayout) this.findViewById(R.id.config_layout);
        storeSP = this.getSharedPreferences(Constant.STOREDB, 0);
    }

    private class PassShowCheckedChangeListener implements CheckBox.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (pass_showC.isChecked()) {
                // 文本正常显示
                addPasswordE.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                Editable etable = addPasswordE.getText();
                Selection.setSelection(etable, etable.length());
            } else {
                // 文本以密码形式显示
                addPasswordE.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                // 下面两行代码实现: 输入框光标一直在输入文本后面
                Editable etable = addPasswordE.getText();
                Selection.setSelection(etable, etable.length());
            }
        }

    }

    private class AddDeviceListener implements OnClickListener {

        private String userId;

        public AddDeviceListener(String userId) {
            this.userId = userId;
        }

        @Override
        public void onClick(View arg0) {
            boolean bool = Util.isWifi(AddDeviceActivity.this);
            if (!bool) {// 用的不是wifi连接
                Toast.makeText(AddDeviceActivity.this,
                        AddDeviceActivity.this.getResources().getString(R.string.addDevice),
                        Toast.LENGTH_LONG).show();
                return;
            }
            // 第一次将正常的可用的wifi的ssid保存下来
            WifiInfo wifiInfo = mWifiManager.getWifiInfor();
            String currentSsid = wifiInfo.getSSID().replace("\"", "");
            if (currentSsid != null && (!currentSsid.contains("YT@WL"))) {
                Editor edit = storeSP.edit();
                edit.putString(Constant.WIFIACCOUNT, currentSsid);
                edit.commit();
            }
            String wlsn = qr_infoE.getText().toString();
            String ssid = "YT@WL" + wlsn;// 获取用户添加的该设备的热点
            List<ScanResult> wifiList = mWifiManager.startScan();
            int pos = -1;
            for (int i = 0; i < wifiList.size(); i++) {
                ScanResult sr = wifiList.get(i);
                String wifiName = sr.SSID;// wifi的名字，根据此名字判读是不是设备的热点
                if (ssid.equals(wifiName.replace("\"", ""))) {
                    pos = i;
                }
            }
            if (pos == -1) {
                Toast.makeText(AddDeviceActivity.this,
                        AddDeviceActivity.this.getResources().getString(R.string.notrd),
                        Toast.LENGTH_LONG).show();
                return;
            }
            String password = addPasswordE.getText().toString();
            Thread configThread = new Thread(new ConfigThread(userId, ssid, password));
            config_layout.setVisibility(View.VISIBLE);
            configThread.start();

        }

    }

    private class ConfigThread implements Runnable {

        private String userid;

        private String ssid;

        private String password;

        public ConfigThread(String userid, String ssid, String password) {
            this.userid = userid;
            this.ssid = ssid;
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
            boolean isSuccess = false;
            String currentSsid = storeSP.getString(Constant.WIFIACCOUNT, "");
            if ("".endsWith(currentSsid)) {// 需要可以的wifi连接
                return;
            }
            List<WifiConfiguration> wifiConfis = mWifiManager.getmWifiConfigurations();
            int netid = getNetId(wifiConfis, currentSsid);
            String seriNum = ssid.split("@")[1];
            boolean isconnect = false;
            int sbNetId = getNetId(wifiConfis, ssid.replace("\"", ""));
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
                System.out.println("===iswificonnect===" + iswificonnect);
                if (iswificonnect) {// 连接上了，往设备发送信息，让设备去联网去
                    // 组装将要发送的信息
                    seriNum = seriNum.substring(2);
                    String repMessage = null;
                    // 组装将要发送的信息
                    for (int i = 0; i < 3; i++) {
                        String sendMessgae = "wlsn=" + seriNum + "&ssid" + getLength(currentSsid)
                                + getLength(password) + "=" + currentSsid + password
                                + "&seraddress=" + Constant.IPADDRESS + "&serport="
                                + Constant.WPORT + "&cmd";
                        repMessage = AddDeviceActivity.this.getMessage(sendMessgae);
                        if (repMessage.contains("success")) {
                            isSuccess = true;
                            break;
                        }
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (repMessage == null || "".equals(repMessage) || repMessage.contains("error")) {
                        Message msg = new Message();
                        msg.what = 2;
                        handle.sendMessage(msg);
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

                    // errorList.add(sr);
                }
            }
            // 删除已经连接好的netId;
            mWifiManager.removeNetwork(ssid.replace("\"", ""));
            // 连接完以后把刚开始的网络连接上
            mWifiManager.connectionWifi(netid);
            boolean yisconnect = false;
            for (int i = 0; i < 100; i++) {
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
            if (isSuccess) {
                UserWebServiceUtil uws = new UserWebServiceUtil();
                String rep = null;
                for (int i = 0; i < 10; i++) {
                    rep = uws.updUserId(userId, seriNum);
                    if (rep != null) {
                        break;
                    }
                    try {
                        Thread.sleep(Constant.LSLEEPTIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (rep == null) {
                    Message msg = new Message();
                    msg.what = 1;
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
                        Intent intent = new Intent(AddDeviceActivity.this, DeviceListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("sb", repResult);
                        bundle.putString("userId", userid);
                        intent.putExtra("sbbundle", bundle);
                        AddDeviceActivity.this.finish();
                        AddDeviceActivity.this.startActivity(intent);
                    } else {

                    }
                }
            } else {
                Message msg = new Message();
                msg.what = 2;
                handle.sendMessage(msg);
                return;
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

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(AddDeviceActivity.this,
                            AddDeviceActivity.this.getResources().getString(R.string.connSbFail),
                            Toast.LENGTH_LONG).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    Toast.makeText(AddDeviceActivity.this,
                            AddDeviceActivity.this.getResources().getString(R.string.connSbFail),
                            Toast.LENGTH_LONG).show();
                    config_layout.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    Object o = msg.obj;
                    String ssid = o.toString();
                    String str = AddDeviceActivity.this.getResources().getString(R.string.handlelj);
                    str = str.replace("%1$", ssid);
                    Toast.makeText(AddDeviceActivity.this, str, Toast.LENGTH_SHORT).show();
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

    private class AddBackListener implements OnClickListener{

		@Override
		public void onClick(View v) {
            Intent intent = new Intent(AddDeviceActivity.this, DeviceListActivity.class);
            //AddDeviceActivity.this.finish();
            AddDeviceActivity.this.startActivity(intent);
			
		}
    	
    }
    
}
