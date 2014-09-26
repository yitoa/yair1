
package com.ya.yair.util;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class WifiUtils {

    /** Called when the activity is first created. */

    private WifiManager mWifiManager;

    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;

    private ConnectivityManager connMan;

    private int netId;// 网络id

    public int getNetId() {
        return netId;
    }

    public void setNetId(int netId) {
        this.netId = netId;
    }

    /**
     * Wifi info instance
     */
    private WifiInfo mWifiInfo = null;

    public WifiInfo getmWifiInfo() {
        return mWifiInfo;
    }

    public void setmWifiInfo(WifiInfo mWifiInfo) {
        this.mWifiInfo = mWifiInfo;
    }

    /**
     * Called activity context
     */
    private Context mContext = null;

    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;

    public List<WifiConfiguration> getmWifiConfigurations() {
        return mWifiManager.getConfiguredNetworks();
    }

    private static final int BUILD_VERSION_JELLYBEAN = 17;

    /***
     * @author sbp 两种编码方式WEP、WPA
     */
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    @SuppressLint("ServiceCast")
    public WifiUtils(Context context) {
        this.mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        connMan = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    // 断开指定ID的网络
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    // 连接指定ID的网络
    public boolean connectionWifi(int netId) {
        return mWifiManager.enableNetwork(netId, true);
    }

    public List<ScanResult> startScan() {
        mWifiManager.startScan();
        mWifiManager.getConfiguredNetworks();
        // 得到扫描结果
        return mWifiManager.getScanResults();
    }

    /***
     * 获取当前连接的网络信息
     * 
     * @return
     */
    public WifiInfo getWifiInfor() {
        return mWifiManager.getConnectionInfo();
    }

    /***
     * 计算信号强度
     * 
     * @param rssi
     * @param numLevels
     * @return
     */
    public int calculateSignalLevel(int rssi, int numLevels) {
        return mWifiManager.calculateSignalLevel(rssi, numLevels);
    }

    /***
     * 获取加密方式
     * 
     * @param str
     * @return
     */
    public String securedType(String str) {
        if (str.contains("WPA") && str.contains("WPA2")) {
            return "WPA/WP2";
        } else if (str.contains("WPA")) {
            return "WPA";
        } else if (str.contains("WPA2")) {
            return "WPA2";
        } else if (str.contains("WEP")) {
            return "WPE";
        }
        return null;
    }

    public List<ScanResult> getmWifiList() {
        return mWifiList;
    }

    /***
     * 获取wifi是否被启??
     * 
     * @return
     */
    public boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }

    /***
     * 关闭wifi
     * 
     * @return
     */
    public boolean CloseWifi() {
        boolean bRet = true;
        if (mWifiManager.isWifiEnabled()) {// 如果是启用的
            bRet = mWifiManager.setWifiEnabled(false);
        }
        return bRet;
    }

    // 打开wifi功能
    public boolean OpenWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
        if (!this.OpenWifi()) {
            return false;
        }
        while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            try {
                Thread.currentThread();
                Thread.sleep(500l);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            return false;
        }

        //WifiConfiguration tempConfig = this.IsExsits(SSID);

        //if (tempConfig != null) {
        //     mWifiManager.removeNetwork(tempConfig.networkId);
       //  }

        int netID = mWifiManager.addNetwork(wifiConfig);
        // 将最后一个放的位置
        this.setNetId(mWifiManager.getConfiguredNetworks().size() - 1);
        boolean bRet = mWifiManager.enableNetwork(netID, true);
        return bRet;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.allowedAuthAlgorithms.clear();
        wc.allowedGroupCiphers.clear();
        wc.allowedKeyManagement.clear();
        wc.allowedPairwiseCiphers.clear();
        wc.allowedProtocols.clear();
        wc.SSID = "\"" + SSID + "\"";
        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            wc.wepKeys[0] = "\"" + "\""; 
            wc.hiddenSSID = true;
            //wc.wepKeys[0] = "";
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //wc.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            wc.wepKeys[0] = "\"" + Password + "\""; // 该热点的密码
            wc.hiddenSSID = true;
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wc.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            wc.preSharedKey = "\"" + Password + "\"";
            wc.hiddenSSID = true;
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);  
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);   
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
            wc.status = WifiConfiguration.Status.ENABLED;  
            
            //wc.status = WifiConfiguration.Status.ENABLED;
            //wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            //wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            //wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            //wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            //wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            //wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        } else {
            return null;
        }
        return wc;
    }

    /**
     * returns current ssid connected to
     * 
     * @return current ssid
     */
    public String getCurrentSSID()
    {
        return removeSSIDQuotes(mWifiInfo.getSSID());
    }

    public int getState(int netid) {
        return mWifiManager.getConfiguredNetworks().get(netid).status;
    }

    /**
     * Filters the double Quotations occuring in Jellybean and above devices.
     * This is only occuring in SDK 17 and above this is documented in SDK as
     * http
     * ://developer.android.com/reference/android/net/wifi/WifiConfiguration.
     * html#SSID
     * 
     * @param connectedSSID
     * @return
     */
    public static String removeSSIDQuotes(String connectedSSID)
    {
        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= BUILD_VERSION_JELLYBEAN)
        {
            if (connectedSSID.startsWith("\"") && connectedSSID.endsWith("\""))
            {
                connectedSSID = connectedSSID.substring(1, connectedSSID.length() - 1);
            }
        }
        return connectedSSID;
    }

    public boolean Connect(WifiConfiguration wc) {
        if (!this.OpenWifi()) {
            return false;
        }
        int netID = mWifiManager.addNetwork(wc);
        boolean bRet = mWifiManager.enableNetwork(netID, true);
        return bRet;
    }

    public void removeNetwork(String ssid) {
        WifiConfiguration tempConfig = this.IsExsits(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

    }

    public int getWifiConfiguration(String ssid) {
        List<WifiConfiguration> lists = this.getmWifiConfigurations();
        int pos = -1;
        for (int i = 0; i < lists.size(); i++) {
            WifiConfiguration wc = lists.get(i);
            if (ssid.replace("\"", "").equals(wc.SSID.replace("\"", ""))) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public State getWifiState() {
        State wifiState = connMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return wifiState;
    }

    /****
     * isWifiConnect判断wifi是否已经连接成功
     * 
     * @return
     */
    public boolean isWifiConnect() {
        NetworkInfo mWifi = connMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
}
