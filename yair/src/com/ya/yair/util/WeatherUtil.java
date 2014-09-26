
package com.ya.yair.util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.content.Context;
import android.widget.Toast;

public class WeatherUtil {

    private Context context;

    // 获取天气信息的变量
    private static final String NAMESPACE = "http://WebXml.com.cn/";
    // WebService地址
    private static String URL = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";

    private static final String METHOD_NAME = "getWeatherbyCityName";

    private static String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName";

    private SoapObject detail;

    public WeatherUtil(Context context) {
        this.context = context;
    }

    /****
     * 根据城市名称获取今天的天气
     * 
     * @param city
     * @return
     */
    public String getWeather(String city) {
        if (!Util.isNetworkConnected(context)) {
            Toast.makeText(context, "暂时没有可用的网络，请稍后再试！",
                    Toast.LENGTH_LONG).show();

            return null;
        }
        String todayWeather = null;
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
            rpc.addProperty("theCityName", city);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(URL);
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            detail = (SoapObject) envelope.getResponse();
            todayWeather = detail.getProperty(5).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

}
