
package com.ya.yair.util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/****
 * 用的注册、登陆等和web进行交换的将在这个类里面进行
 * 
 * @author sbp
 */
public class UserWebServiceUtil {

    // 命名空间
    private static final String NAMESPACE = "http://wl.yitoa.com/";
    // WebService地址
    private static String URL = "http://60.173.247.120:82/yitoawl/webservices/userService?wsdl";

    private static final String UPDUSERID_METHOD_NAME = "updUserId";

    private static String UPDUSERID__SOAP_ACTION = NAMESPACE + UPDUSERID_METHOD_NAME;

    private static final String REG_METHOD_NAME = "addUsers";
  
    private static String REG_SOAP_ACTION = NAMESPACE + REG_METHOD_NAME;
    
    private static String UPD_METHOD_NAME="updBm";
    
    private static String UPD_SOAP_ACTION= NAMESPACE + UPD_METHOD_NAME;

    public String updUserId(String userId, String mess) {
        String ss = null;
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, UPDUSERID_METHOD_NAME);
            rpc.addProperty("userId", userId);
            rpc.addProperty("mess", mess);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER10);
            envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(URL);
            ht.debug = true;
            ht.call(UPDUSERID__SOAP_ACTION, envelope);
            ss = envelope.getResponse().toString();
        } catch (Exception e) {
            return null;
        }
        return ss;
    }

    /****
     * 注册用户信息
     * 
     * @param city
     * @return
     */
    public String addUsers(String username, String password, String phonenumber,String email) {

        String ss = null;
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, REG_METHOD_NAME);
            rpc.addProperty("username", username);
            rpc.addProperty("password", password);
            rpc.addProperty("phonenumber", phonenumber);
            rpc.addProperty("email", email);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER10);
            envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(URL);
            ht.debug = true;
            ht.call(REG_SOAP_ACTION, envelope);
            ss = envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ss;
    }
    
    
    /****
     * 注册用户信息
     * 
     * @param city
     * @return
     */
    public String updBM(String  wlsn, String bm,String userid) {

        String ss = null;
        try {
            SoapObject rpc = new SoapObject(NAMESPACE, UPD_METHOD_NAME);
            rpc.addProperty("wlsn", wlsn);
            rpc.addProperty("bm", bm);
            rpc.addProperty("userid", userid);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER10);
            envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(URL);
            ht.debug = true;
            ht.call(UPD_SOAP_ACTION, envelope);
            ss = envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ss;
    }

}
