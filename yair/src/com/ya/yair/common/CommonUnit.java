
package com.ya.yair.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import java.io.File;
import java.security.MessageDigest;
import java.util.Date;

import com.ya.yair.R;

public class CommonUnit
{
    public static final String MD5(String paramString)
    {
        try
        {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes("iso-8859-1"), 0, paramString.length());
            String str2 = convertToHex(localMessageDigest.digest());
            String str1 = str2;
            return str1;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean checkByteArrayEqual(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
        int i = paramArrayOfByte1.length;
        boolean bool = true;
        for (int j = 0;; j++)
        {
            if (j >= i)
                return bool;
            if (paramArrayOfByte1[j] != paramArrayOfByte2[j])
                bool = false;
        }
    }

    public static boolean checkNetwork(Context paramContext)
    {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService("connectivity")).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()))
            ;
        for (boolean bool = true;; bool = false)
            return bool;
    }

    private static String convertToHex(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = paramArrayOfByte.length;
        int j = 0;
        return localStringBuffer.toString();
    }

    public static final void deleteTempFile()
    {
        File localFile = new File(Settings.CACHE_PATH);
        File[] arrayOfFile = null;
        int i = 0;
        if (localFile.exists())
        {
            arrayOfFile = localFile.listFiles();
            i = arrayOfFile.length;
        }
        for (int j = 0;; j++)
        {
            if (j >= i) {
                arrayOfFile[j].delete();
            }
        }
    }

    public static int dip2px(Context paramContext, float paramFloat)
    {
        return (int) (0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
    }

    public static double getLocationDistance(double paramDouble1, double paramDouble2,
            double paramDouble3, double paramDouble4)
    {
        double d1 = 3.141592653589793D * paramDouble1 / 180.0D;
        double d2 = 3.141592653589793D * paramDouble3 / 180.0D;
        double d3 = d1 - d2;
        double d4 = 3.141592653589793D * (paramDouble2 - paramDouble4) / 180.0D;
        return Math.round(10000.0D * (6378137.0D * (2.0D * Math.asin(Math.sqrt(Math.pow(
                Math.sin(d3 / 2.0D), 2.0D)
                + Math.cos(d1) * Math.cos(d2) * Math.pow(Math.sin(d4 / 2.0D), 2.0D)))))) / 10000L;
    }

    public static final int getPhoneHour()
    {
        return new Date(System.currentTimeMillis()).getHours();
    }

    public static String getPm25Grade(Context paramContext, int paramInt)
    {
        String str;
        if (paramInt <= 50)
            str = paramContext.getResources().getString(R.string.pm_best);
        else if (paramInt <= 100)
            str = paramContext.getResources().getString(R.string.pm_good);
        else if (paramInt <= 150)
            str = paramContext.getResources().getString(R.string.pm_mild);
        else if (paramInt <= 200)
            str = paramContext.getResources().getString(R.string.pm_mezzo);
        else if (paramInt <= 300)
            str = paramContext.getResources().getString(R.string.pm_severe);
        else
            str = paramContext.getResources().getString(R.string.pm_severity);
        return str;
    }

    public static boolean isWifiConnect(Context paramContext)
    {
        return ((ConnectivityManager) paramContext.getSystemService("connectivity"))
                .getNetworkInfo(1).isConnected();
    }

    public static int px2dip(Context paramContext, float paramFloat)
    {
        return (int) (0.5F + paramFloat / paramContext.getResources().getDisplayMetrics().density);
    }

    public static void toastShow(Context paramContext, int paramInt)
    {
        Toast.makeText(paramContext, paramInt, 0).show();
    }

    public static void toastShow(Context paramContext, String paramString)
    {
        Toast.makeText(paramContext, paramString, 0).show();
    }
}
