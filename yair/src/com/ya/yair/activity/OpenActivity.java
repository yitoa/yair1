
package com.ya.yair.activity;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class OpenActivity extends BaseActivity implements OnClickListener {

    public Button btn_open;
    private String appMac = "";
    private TextView location_pm;
    private TextView pm;
    private TextView pm_description;
    private SharedPreferences storeSP;
    private String wlsn;
    private String sb;// 改用户下的所有设备列表
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EairApplaction.getInstance().addActivity(this);
        setContentView(R.layout.local_weath_layout);
        storeSP = this.getSharedPreferences(Constant.STOREDB, 0);
        appMac = storeSP.getString(Constant.PHONEMAC, "");
        btn_open = (Button) this.findViewById(R.id.btn_open);
        Bundle bundle = this.getIntent().getExtras();
        wlsn = bundle.getString("wlsn");
        sb = bundle.getString("sb");
        userId = bundle.getString("userId");
        TextView location_text = (TextView) this.findViewById(R.id.location_text);
        location_pm = (TextView) this.findViewById(R.id.location_pm);
        pm = (TextView) this.findViewById(R.id.pm);
        btn_open.setOnClickListener(this);
        if (EairApplaction.status) {
            String city = EairApplaction.city;
            location_text.setText(city + "  " + EairApplaction.todayWeather);
            pm.setText(city + this.getResources().getString(R.string.aqibycity));
            location_pm.setText(EairApplaction.aqi);
            pmDescription(EairApplaction.aqi);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btn_open) {
            String str = "wlsn=" + wlsn + "&mac=" + appMac + "&set&poweron=0&userid=" + userId
                    + "&cmd";
            String result = getRepMessage(str);
            if ("timeout".equals(result) || result == null) {// 开机失败
                tips();
            } else {// 开机成功
                    // 获取设备的初始化状态
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                str = "wlsn=" + wlsn + "&mac=" + appMac + "&get&init&userid=" + userId + "&cmd";
                String mess = getRepMessage(str);
                if ("timeout".equals(mess) || mess == null) {// 没有获取到
                    tips();
                } else {
                    storeData(mess);
                    // 到运行页面去
                    Intent intent = new Intent(OpenActivity.this, YADeviceHomePageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("wlsn", wlsn);
                    bundle.putString("sb", sb);
                    bundle.putString("userId", userId);
                    intent.putExtras(bundle);
                    OpenActivity.this.finish();
                    OpenActivity.this.startActivity(intent);
                }
            }

        }

    }

    /***
     * 处理返回的init设备的信息
     * 
     * @param message
     */
    private void storeData(String message) {
        Editor editor = storeSP.edit();
        String[] mess = message.split("&");
        editor.putInt(Constant.TIME, Integer.parseInt(mess[1].split("=")[1]));
        editor.putInt(Constant.WINDDW, Integer.parseInt(mess[2].split("=")[1]));
        editor.putInt(Constant.LOCK, Integer.parseInt(mess[3].split("=")[1]));
        editor.putString(Constant.AIR, mess[4].split("=")[1]);
        editor.putInt(Constant.MODE, Integer.parseInt(mess[5].split("=")[1]));
        editor.putInt(Constant.ANION, Integer.parseInt(mess[6].split("=")[1]));
        editor.putInt(Constant.SWIND, Integer.parseInt(mess[7].split("=")[1]));
        editor.putInt(Constant.MOTOR, Integer.parseInt(mess[8].split("=")[1]));
        editor.commit();
    }

    private void pmDescription(String aqi) {
        pm_description = (TextView) this.findViewById(R.id.pm_value);
        if (aqi == null) {
            pm_description.setText("获取中");
        } else {
            int aqi_value = Integer.parseInt(aqi);
            if ((aqi_value > 0) && (aqi_value <= 50))
            {
                pm_description.setText("优");
            }
            else if ((aqi_value > 50) && (aqi_value <= 100))
            {
                pm_description.setText("良");
            }
            else if ((aqi_value > 100) && (aqi_value <= 150))
            {
                pm_description.setText("轻度污染");
            }
            else if ((aqi_value > 150) && (aqi_value <= 200))
            {
                pm_description.setText("中度污染");
            }
            else if ((aqi_value > 200) && (aqi_value <= 300))
            {
                pm_description.setText("重度污染");
            }
            else if (aqi_value > 300)
            {
                pm_description.setText("严重污染");
            }
            else
            {
                pm_description.setText("");
            }
        }
    }

}
