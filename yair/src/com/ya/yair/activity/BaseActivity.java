
package com.ya.yair.activity;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.util.UdpHelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends Activity {

    public UdpHelper udpH;

    public int interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        udpH = new UdpHelper();

    }

    /****
     * 发送命令获取返回值
     * 
     * @param str
     * @return
     */
    public String getRepMessage(String str) {
        String message = null;
        try {
            Thread vThread = new Thread(new VisitorThread(str));
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

    public class VisitorThread implements Runnable {
        private String str;

        public VisitorThread(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            try {
                udpH.sendSb(Constant.IPADDRESS, Constant.WPORT, str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void tips() {
        Toast.makeText(BaseActivity.this,
                BaseActivity.this.getResources().getString(R.string.netException),
                Toast.LENGTH_SHORT).show();
    }

    public void openTips() {
        Toast.makeText(BaseActivity.this,
                BaseActivity.this.getResources().getString(R.string.dooropen),
                Toast.LENGTH_SHORT).show();
    }

    public void loadTips() {
        Toast.makeText(BaseActivity.this,
                BaseActivity.this.getResources().getString(R.string.netError),
                Toast.LENGTH_SHORT).show();
        new TipThread().start();
    }

    public void loadTipss() {
        Toast.makeText(BaseActivity.this,
                BaseActivity.this.getResources().getString(R.string.netError),
                Toast.LENGTH_SHORT).show();
    }

    public class TipThread extends Thread {

        @Override
        public void run() {
            // 暂停3s
            try {
                Thread.sleep(3000);
                EairApplaction.getInstance().exit();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
