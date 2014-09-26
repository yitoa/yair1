
package com.ya.yair.activity;

import java.util.List;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.db.DBManager;
import com.ya.yair.util.UdpHelper;
import com.ya.yair.util.UpdateManager;
import com.ya.yair.util.Util;
import com.ya.yair.util.WebServiceUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LoadActivity extends BaseActivity implements OnClickListener {

    private SharedPreferences storeSP;

    private String mac = "";

    private UpdateManager mUpdateManager;

    private List<String> deviceList;

    // static
    // {
    // System.out.println("load NatTypeJni library");
    // System.loadLibrary("NatTypeJni");
    // }

    @Override
    public void onClick(View arg0) {
       

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        EairApplaction.getInstance().addActivity(this);
        storeSP = this.getSharedPreferences(Constant.STOREDB, 0);
        mac = storeSP.getString(Constant.PHONEMAC, "");
        PackageInfo info;
        // 检查网络是否有问题
        boolean isExistNet = Util.isNetworkConnected(this);
        if (!isExistNet) {// 没有网络
            loadTips();
            return;
        }
        try {
            PackageManager manager = LoadActivity.this.getPackageManager();
            info = manager.getPackageInfo(LoadActivity.this.getPackageName(), 0);
            String version = info.versionName;
            WebServiceUtil wsu = new WebServiceUtil();
            String repMessage = wsu.getVersion(version);
            if (repMessage == null) {// 尝试访问2次
                try {
                    Thread.sleep(Constant.SLEEPTIME);
                    repMessage = wsu.getVersion(version);
                    if (repMessage == null) {
                        Thread.sleep(Constant.SLEEPTIME);
                        repMessage = wsu.getVersion(version);
                    }
                } catch (InterruptedException e) {
                    
                    e.printStackTrace();
                }
            }
            if (!("notupd".equals(repMessage)) && repMessage!=null) {// 需要升级的，程序就停在首页等待升级，升级过以后才能进行下一步。
                // 这里来检测版本是否需要更新
                String mess[] = repMessage.split("&");
                mUpdateManager = new UpdateManager(mess[1], this);
                mUpdateManager.checkUpdateInfo();
                return;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if ("".equals(mac)) {// 如果手机里面没有mac提示用户用wifi的方式联网，来获取mac地址
            boolean bool = Util.isWifi(this);
            if (bool) {// 是wifi联网
                mac = Util.getLocalMacAddressFromWifiInfo(this);
                Editor editor = storeSP.edit();
                editor.putString(Constant.PHONEMAC, mac);
                editor.commit();// 将手机的mac地址保存
                Thread tt = new Thread(new TimeThread());
                tt.start();
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.notwifi),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Thread tt = new Thread(new TimeThread());
            tt.start();
        }

    }

    /***
     * 处理下面是怎么跳转
     */
    private void pdhandle() {
        // 获取手机里面存储的设备
        DBManager db = new DBManager(LoadActivity.this);
        deviceList = db.query();
        if (deviceList == null || deviceList.size() == 0) {// 没有设备,需要配置设备
            Intent intent = new Intent(this, ConfigActivity.class);
            this.finish();
            this.startActivity(intent);
        } else {// 有设备
            Intent intent = new Intent(this, DeviceListActivity.class);
            this.finish();
            this.startActivity(intent);
        }
    }

    private class TimeThread implements Runnable {
        @Override
        public void run() {
            // hQhandle();
            // pdhandle();
            // 跳转到登陆界面
            Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
            LoadActivity.this.finish();
            LoadActivity.this.startActivity(intent);

        }
    }

    /***
     * If current return value equal to NET_TYPE_FULLCONE_NAT, NET_TYPE_REST_NAT
     * and NET_TYPE_PORTREST_NAT, it can support UDP Hole Punching
     * NET_TYPE_OPENED = 0, NET_TYPE_FULLCONE_NAT = 1, // Full Cone NAT
     * NET_TYPE_REST_NAT = 2, // Restricted Cone NAT (restrict IP)
     * NET_TYPE_PORTREST_NAT = 3, // Port Restricted Cone NAT (restrict IP &
     * Port) NET_TYPE_SYM_UDP_FIREWALL = 4, NET_TYPE_SYM_NAT_LOCAL = 5,
     * NET_TYPE_SYM_NAT = 6, NET_TYPE_UDP_BLOCKED = 7, NET_TYPE_ERROR = 8
     ***/
    private static native int getNatType();

    public static native void destroying();

}
