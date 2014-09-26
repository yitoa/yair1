
package com.ya.yair;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ya.yair.util.AQIUtil;
import com.ya.yair.util.UdpHelper;
import com.ya.yair.util.Util;
import com.ya.yair.util.WeatherUtil;
import com.ya.yair.util.WebServiceUtil;

public class EairApplaction extends Application
{
    public static long mTimeDiff = 0L;
    public List<Activity> mActivityList = new ArrayList();
    public String mNetIP;
    private static EairApplaction instance;

    public static Map<String, String> cityMap = new HashMap<String, String>();

    public UdpHelper udpH;

    public int interval;

    public static boolean status;

    public static String todayWeather;

    public static String aqi;

    public static String city;

    public EairApplaction() {

    }

    public synchronized static EairApplaction getInstance() {
        if (null == instance) {
            instance = new EairApplaction();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mActivityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        System.out.println("===EairApplaction==");
        cityMap.put("北京", "beijing");
        cityMap.put("上海", "shanghai");
        cityMap.put("天津", "tianjin");
        cityMap.put("重庆", "chongqing");
        cityMap.put("鞍山", "anshan");
        cityMap.put("安阳", "anyang");
        cityMap.put("安庆", "anqing");
        cityMap.put("阿里", "alidiqu");
        cityMap.put("保定", "baoding");
        cityMap.put("包头", "baotou");
        cityMap.put("本溪", "benxi");
        cityMap.put("北海", "beihai");
        cityMap.put("宝鸡", "baoji");
        cityMap.put("亳州", "bozhou");
        cityMap.put("蚌埠", "bengpu");
        cityMap.put("巴中", "bazhong");
        cityMap.put("滨州", "binzhou");
        cityMap.put("保山", "baoshan");
        cityMap.put("巴彦淖尔", "bayanchuoer");
        cityMap.put("长治", "changzhi");
        cityMap.put("赤峰", "chifeng");
        cityMap.put("长春", "changchun");
        cityMap.put("常州", "changzhou");
        cityMap.put("长沙", "changsha");
        cityMap.put("常德", "changde");
        cityMap.put("成都", "chengdu");
        cityMap.put("沧州", "cangzhou");
        cityMap.put("承德", "chengde");
        cityMap.put("朝阳", "chaoyang");
        cityMap.put("滁州", "chuzhou");
        cityMap.put("巢湖", "chaohushi");
        cityMap.put("池州", "chizhou");
        cityMap.put("楚雄", "chuxiong");
        cityMap.put("昌都", "changdu");
        cityMap.put("常熟", "changshu");
        cityMap.put("潮州", "chaozhou");
        cityMap.put("大同", "datong");
        cityMap.put("大连", "dalian");
        cityMap.put("大庆", "daqing");
        cityMap.put("德阳", "deyang");
        cityMap.put("东莞", "dongguan");
        cityMap.put("丹东", "dandong");
        cityMap.put("达州", "dazhou");
        cityMap.put("东营", "dongying");
        cityMap.put("德州", "dezhou");
        cityMap.put("大理", "dali");
        cityMap.put("鄂尔多斯", "eerduozi");
        cityMap.put("二连浩特", "erlianhaote");
        cityMap.put("抚顺", "fushun");
        cityMap.put("福州", "fuzhou");
        cityMap.put("佛山", "foshan");
        cityMap.put("阜新", "fuxin");
        cityMap.put("阜阳", "fuyang");
        cityMap.put("抚州", "fuzhoushi");
        cityMap.put("富阳", "fuyangshi");
        cityMap.put("广州", "guangzhou");
        cityMap.put("桂林", "guilin");
        cityMap.put("贵阳", "guiyang");
        cityMap.put("广安", "guangan");
        cityMap.put("广元", "guangyuan");
        cityMap.put("赣州", "ganzhou");
        cityMap.put("邯郸", "handan");
        cityMap.put("呼和浩特", "huhehaote");
        cityMap.put("哈尔滨", "haerbin");
        cityMap.put("杭州", "hangzhou");
        cityMap.put("湖州", "huzhou");
        cityMap.put("合肥", "hefei");
        cityMap.put("海口", "haikou");
        cityMap.put("淮安", "huaian");
        cityMap.put("惠州", "huizhou");
        cityMap.put("衡水", "hengshui");
        cityMap.put("淮北", "huaibei");
        cityMap.put("淮南", "huainan");
        cityMap.put("黄山", "huangshan");
        cityMap.put("黄山风景区", "huangshangjq");
        cityMap.put("菏泽", "heze");
        cityMap.put("鹤壁", "hebi");
        cityMap.put("黄冈", "huanggan");
        cityMap.put("呼伦贝尔", "hulunbeier");
        cityMap.put("河源", "heyuan");
        cityMap.put("葫芦岛", "huludao");
        cityMap.put("海门", "haimen");
        cityMap.put("锦州", "jinzhou");
        cityMap.put("吉林", "jilin");
        cityMap.put("嘉兴", "jiaxing");
        cityMap.put("九江", "jiujiang");
        cityMap.put("济南", "jinan");
        cityMap.put("济宁", "jining");
        cityMap.put("焦作", "jiaozuo");
        cityMap.put("荆州", "jingzhou");
        cityMap.put("金昌", "jinchang");
        cityMap.put("江门", "jiangmen");
        cityMap.put("金华", "jinhua");
        cityMap.put("晋城", "jincheng");
        cityMap.put("晋中", "jinzhong");
        cityMap.put("九华山风景区", "jiuhuanshanjq");
        cityMap.put("吉安", "jian");
        cityMap.put("景德镇", "jingdezhen");
        cityMap.put("江阴", "jiangyin");
        cityMap.put("金坛", "jintan");
        cityMap.put("胶州", "jiaozhou");
        cityMap.put("即墨", "jimo");
        cityMap.put("胶南", "jiaonan");
        cityMap.put("句容", "jurong");
        cityMap.put("揭阳", "jieyang");
        cityMap.put("嘉峪关", "jiayuguan");
        cityMap.put("开封", "kaifeng");
        cityMap.put("昆明", "kunming");
        cityMap.put("克拉玛依", "kelamayi");
        cityMap.put("库尔勒", "kuerle");
        cityMap.put("昆山", "kunshan");
        cityMap.put("临汾", "linfen");
        cityMap.put("连云港", "lianyungang");
        cityMap.put("洛阳", "luoyang");
        cityMap.put("柳州", "liuzhou");
        cityMap.put("泸州", "luzhou");
        cityMap.put("拉萨", "lasa");
        cityMap.put("兰州", "lanzhou");
        cityMap.put("丽水", "lishui");
        cityMap.put("廊坊", "langfang");
        cityMap.put("吕梁", "luliang");
        cityMap.put("辽阳", "liaoyang");
        cityMap.put("六安", "liuan");
        cityMap.put("乐山", "leshan");
        cityMap.put("莱芜", "laiwu");
        cityMap.put("临沂", "linyi");
        cityMap.put("聊城", "liaocheng");
        cityMap.put("漯河", "luohe");
        cityMap.put("丽江", "lijiang");
        cityMap.put("临沧", "lincang");
        cityMap.put("林芝", "linzhi");
        cityMap.put("龙岩", "longyan");
        cityMap.put("溧阳", "liyang");
        cityMap.put("临安", "linan");
        cityMap.put("莱西", "laixi");
        cityMap.put("莱州", "laizhou");
        cityMap.put("牡丹江", "mudanjiang");
        cityMap.put("马鞍山", "maanshan");
        cityMap.put("绵阳", "mianyang");
        cityMap.put("茂名", "maoming");
        cityMap.put("梅州", "meizhou");
        cityMap.put("南京", "nanjing");
        cityMap.put("南通", "nantong");
        cityMap.put("宁波", "ningbo");
        cityMap.put("南昌", "nanchang");
        cityMap.put("南宁", "nanning");
        cityMap.put("南充", "nanchong");
        cityMap.put("南阳", "nanyang");
        cityMap.put("那曲", "naqu");
        cityMap.put("南平", "nanping");
        cityMap.put("平顶山", "pingdingshan");
        cityMap.put("攀枝花", "panzhihua");
        cityMap.put("盘锦", "panjin");
        cityMap.put("萍乡", "pingxiang");
        cityMap.put("濮阳", "puyang");
        cityMap.put("莆田", "putian");
        cityMap.put("平度", "pingdu");
        cityMap.put("蓬莱", "penglai");
        cityMap.put("秦皇岛", "qinhuangdao");
        cityMap.put("齐齐哈尔", "qiqihaer");
        cityMap.put("泉州", "quanzhou");
        cityMap.put("青岛", "qingdao");
        cityMap.put("曲靖", "qujing");
        cityMap.put("衢州", "quzhou");
        cityMap.put("清远", "qingyuan");
        cityMap.put("日照", "rizhao");
        cityMap.put("日喀则", "rikaze");
        cityMap.put("荣成", "rongcheng");
        cityMap.put("乳山", "rushan");
        cityMap.put("石家庄", "shijiazhuang");
        cityMap.put("沈阳", "shenyang");
        cityMap.put("汕头", "shantou");
        cityMap.put("苏州", "suzhou");
        cityMap.put("绍兴", "shaoxing");
        cityMap.put("三门峡", "sanmenxia");
        cityMap.put("韶关", "shaoguan");
        cityMap.put("深圳", "shenzhen");
        cityMap.put("三亚", "sanya");
        cityMap.put("石嘴山", "shizuishan");
        cityMap.put("宿迁", "suqian");
        cityMap.put("朔州", "shuozhou");
        cityMap.put("宿州", "suzhoushi");
        cityMap.put("遂宁", "suiing");
        cityMap.put("上饶", "shangrao");
        cityMap.put("商丘", "shangqiu");
        cityMap.put("随州", "suizhou");
        cityMap.put("昭通", "shaotong");
        cityMap.put("山南", "shannan");
        cityMap.put("三明", "sanming");
        cityMap.put("寿光", "shouguang");
        cityMap.put("汕尾", "shanwei");
        cityMap.put("唐山", "tangshan");
        cityMap.put("太原", "taiyuan");
        cityMap.put("台州", "taizhou");
        cityMap.put("泰安", "taian");
        cityMap.put("铜川", "tongchuan");
        cityMap.put("泰州", "taizhoushi");
        cityMap.put("铁岭", "tieling");
        cityMap.put("铜陵", "tongling");
        cityMap.put("通辽", "tongliao");
        cityMap.put("太仓", "taicang");
        cityMap.put("无锡", "wuxi");
        cityMap.put("温州", "wenzhou");
        cityMap.put("芜湖", "wuhu");
        cityMap.put("潍坊", "weifang");
        cityMap.put("威海", "weihai");
        cityMap.put("武汉", "wuhan");
        cityMap.put("渭南", "weinan");
        cityMap.put("乌鲁木齐", "wulumuqi");
        cityMap.put("文山", "wenshan");
        cityMap.put("乌海", "wuhai");
        cityMap.put("瓦房店", "wafangdian");
        cityMap.put("吴江", "wujiang");
        cityMap.put("文登", "wendeng");
        cityMap.put("徐州", "xuzhou");
        cityMap.put("厦门", "xiamen");
        cityMap.put("湘潭", "xiangtan");
        cityMap.put("西安", "xian");
        cityMap.put("咸阳", "xianyang");
        cityMap.put("西宁", "xining");
        cityMap.put("邢台", "xingtai");
        cityMap.put("宣城", "xuancheng");
        cityMap.put("忻州", "qizhou");
        cityMap.put("新余", "xinyu");
        cityMap.put("新乡", "xinxiang");
        cityMap.put("许昌", "xuchang");
        cityMap.put("信阳", "xinyang");
        cityMap.put("阳泉", "yangquan");
        cityMap.put("扬州", "yangzhou");
        cityMap.put("烟台", "yantai");
        cityMap.put("宜昌", "yichang");
        cityMap.put("岳阳", "yueyang");
        cityMap.put("宜宾", "yibin");
        cityMap.put("玉溪", "yuxi");
        cityMap.put("延安", "yanan");
        cityMap.put("银川", "yinchuan");
        cityMap.put("盐城", "yancheng");
        cityMap.put("运城", "yuncheng");
        cityMap.put("营口", "yingkou");
        cityMap.put("雅安", "yaan");
        cityMap.put("宜春", "yichun");
        cityMap.put("鹰潭", "yingtan");
        cityMap.put("宜兴", "yixing");
        cityMap.put("义乌", "yiwu");
        cityMap.put("阳江", "yangjiang");
        cityMap.put("云浮", "yunfu");
        cityMap.put("镇江", "zhenjiang");
        cityMap.put("淄博", "zibo");
        cityMap.put("枣庄", "zaozhuang");
        cityMap.put("郑州", "zhengzhou");
        cityMap.put("株洲", "zhuzhou");
        cityMap.put("张家界", "zhangjiajie");
        cityMap.put("珠海", "zhuhai");
        cityMap.put("湛江", "zhanjiang");
        cityMap.put("中山", "zhongshan");
        cityMap.put("自贡", "zigong");
        cityMap.put("遵义", "zunyi");
        cityMap.put("肇庆", "zhaoqing");
        cityMap.put("张家口", "zhangjiakou");
        cityMap.put("舟山", "zhoushan");
        cityMap.put("资阳", "ziyang");
        cityMap.put("周口", "zhoukou");
        cityMap.put("驻马店", "zhumadian");
        cityMap.put("张家港", "zhangjiagang");
        cityMap.put("诸暨", "zhuji");
        cityMap.put("章丘", "zhangqiu");
        cityMap.put("招远", "zhaoyuan");
        cityMap.put("香港", "hongkong");
        cityMap.put("澳门", "macau");
        cityMap.put("新北", "xinbei");
        cityMap.put("台北", "taibei");
        cityMap.put("桃园", "taoyuan");
        cityMap.put("新竹", "xinzhushi");
        cityMap.put("苗栗", "miaoli");
        cityMap.put("台中", "taizhong");
        cityMap.put("彰化", "zhanghua");
        cityMap.put("南投", "nantou");
        cityMap.put("云林", "yunlin");
        cityMap.put("嘉義", "jiayi");
        cityMap.put("台南", "tainan");
        cityMap.put("高雄", "gaoxiong");
        cityMap.put("屏东", "pingdong");
        cityMap.put("台东", "taidong");
        cityMap.put("花莲", "hualian");
        cityMap.put("宜兰", "yilan");
        cityMap.put("金门", "jinmen");
        cityMap.put("澎湖", "penghu");
        cityMap.put("基隆", "jilong");
        Thread  messageThread=new Thread(new GetAllMessage(this));
        messageThread.start();
    }
    
    
    public class GetAllMessage implements Runnable{
        
        private Context  context;
        
        public  GetAllMessage(Context context){
            this.context=context;
        }

        @Override
        public void run() {
            WebServiceUtil wsU = new WebServiceUtil();
            String message = wsU.getWIP();
            if (message == null) {//尝试访问2次
                try {
                    Thread.sleep(Constant.SLEEPTIME);
                    message = wsU.getWIP();
                    if (message == null) {
                        Thread.sleep(Constant.SLEEPTIME);
                        message = wsU.getWIP();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (message == null) {
                status = false;
            } else {
                status = true;
                String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?ip="
                        + message;
                // 获取所在的地址
                // String city = Util.getCIty(url);
                city = Util.getCIty(url);
                // map chaohu to hefei
                if ("巢湖".equals(city))
                {
                    city = "合肥";
                }
                AQIUtil au = new AQIUtil();
                String pycity = EairApplaction.cityMap.get(city);
                String aqiUrl = "http://www.cnpm25.cn/city/" + pycity + ".html";
                aqi = au.getAQI(aqiUrl);
                WeatherUtil wu = new WeatherUtil(context);
                todayWeather = wu.getWeather(city);
                if (todayWeather != null) {
                    todayWeather = todayWeather.replace("/", "~");
                }
               
            }
        }
        
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
            udpH.sendSb(Constant.IPADDRESS, Constant.WPORT, str);
            for (;;) {
                if (UdpHelper.result != null) {
                    message = UdpHelper.result;
                    UdpHelper.result = null;
                    break;
                }
                Thread.sleep(500);
                interval += 1;
                if (interval > 6) {
                    message = "timeout";
                    interval = 0;// 初始化，恢复0。
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
