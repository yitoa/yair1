package com.ya.yair.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.adapter.DeviceAdapter;
import com.ya.yair.domain.DeviceDomain;
import com.ya.yair.util.UdpHelper;
import com.ya.yair.util.WebServiceUtil;

/****
 * 获取设备的列表的
 * 
 * @author sbp
 */


public class DeviceListActivity extends BaseActivity {
	public static DeviceListActivity devicelistinstance = null;
	public static String g_userId = null;	
	private List<DeviceDomain> lists = new ArrayList<DeviceDomain>();
	private SharedPreferences storeSP;
	private LinearLayout add_device_layout;
	private String phoneMac;
	private String sb;// 改用户下的所有设备列表
	private String userId;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		EairApplaction.getInstance().addActivity(this);
		setContentView(R.layout.device_list_layout);
		devicelistinstance = this;
		storeSP = this.getSharedPreferences(Constant.STOREDB, 0);
		Bundle bundle = this.getIntent().getBundleExtra("sbbundle");
		userId = bundle.getString("userId");
		g_userId = userId;
		sb = bundle.getString("sb");
		if (sb == null) {
			WebServiceUtil wsu = new WebServiceUtil();
			sb = wsu.querySbByUserId(userId);
			if(sb==null){
			    try {
			        Thread.sleep(Constant.SLEEPTIME);
                    sb = wsu.querySbByUserId(userId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
			}
		}
		
		phoneMac = storeSP.getString(Constant.PHONEMAC, "");
		if (!"0".equals(sb)&&sb!=null) {
			String[] sbs = sb.split("&");
			for (String str : sbs) {
				DeviceDomain domian = new DeviceDomain();
				String newStr[] = str.split(":");
				domian.setMac(newStr[0]);
				domian.setPoweron(newStr[1]);
				domian.setAir(newStr[2]);
				domian.setBm(newStr[3]);
				lists.add(domian);
			}
		}
		ListView listView = (ListView) this.findViewById(R.id.deviceList);
		add_device_layout = (LinearLayout) this
				.findViewById(R.id.add_device_layout);
		listView.setAdapter(new DeviceAdapter(lists, this,userId));
		// 点击单个里面的事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String mac = lists.get(pos).getMac();
				// 这里可以加一个提示东东
				Thread tt = new Thread(new StartThread(mac));
				tt.start();
			}
		});
		add_device_layout.setOnClickListener(new AddDeviceListener(userId));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private class AddDeviceListener implements OnClickListener {

		private String userId;

		public AddDeviceListener(String userId) {
			this.userId = userId;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(DeviceListActivity.this,
					AddDeviceActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("userId", userId);
			intent.putExtra("uBundle", bundle);
			// to support AddDeviceUI back to DeviceList UI
			// DeviceListActivity.this.finish();
			DeviceListActivity.this.startActivity(intent);
		}

	}

	/***
	 * 后期处理啊
	 */
	private void hQhandle(String mac) {
		// 发送命令请求请求服务端获取设备是不是在开机
		try {
			Thread powerThread = new Thread(new PwonerThread(mac));
			powerThread.start();
			for (;;) {
				if (UdpHelper.result != null) {
					interval = 0;
					break;
				}
				Thread.sleep(500);
				interval += 1;
				if (interval > Constant.WAITTIME) {
					UdpHelper.result = "timeout";
					interval = 0;// 初始化，恢复0。
					break;
				}
			}
			// 线程销毁
			powerThread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.obj = mac;
		if ("timeout".equals(UdpHelper.result) || msg == null) {
			msg.what = -1;
			UdpHelper.result = null;
			handle.sendMessage(msg);
		} else if ("NotfindSbAddress".equals(UdpHelper.result)) {
			msg.what = 2;
			UdpHelper.result = null;
			handle.sendMessage(msg);
		} else {// 返回的信息
			String repMessage = UdpHelper.result;
			UdpHelper.result = null;
			System.out.println("==repMessage=" + repMessage);
			if (repMessage != null) {
				String mess[] = repMessage.split("&");
				if (mess[1].contains("=")) {
					String on = mess[1].split("=")[1];
					msg.what = Integer.parseInt(on);
					handle.sendMessage(msg);
				}
			}
		}

	}

	/****
	 * 启动对线程进行判断
	 */
	Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String mac = msg.obj.toString();
			if (msg.what == 0) {// 设备开启了
				// 获取设备运行的状态
				String str = "wlsn=" + mac + "&mac=" + phoneMac
						+ "&get&init&userid=" + userId + "&cmd";
				String repMessage = getRepMessage(str);
				if ("timeout".equals(repMessage) || repMessage == null) {
					Message msg2 = new Message();
					msg2.what = -1;
					handle.sendMessage(msg2);
				} else {// 返回的信息,对运行状态进行保存
					storeData(repMessage);
				}
				Intent intent = new Intent(DeviceListActivity.this,
						YADeviceHomePageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("wlsn", mac);
				bundle.putString("sb", sb);
				bundle.putString("userId", userId);
				intent.putExtras(bundle);
				DeviceListActivity.this.finish();
				DeviceListActivity.this.startActivity(intent);
			} else if (msg.what == 1) {// 设备没有开启了
				Intent intent = new Intent(DeviceListActivity.this,
						OpenActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("wlsn", mac);
				bundle.putString("sb", sb);
				bundle.putString("userId", userId);
				intent.putExtras(bundle);
				DeviceListActivity.this.finish();
				DeviceListActivity.this.startActivity(intent);
			} else if (msg.what == -1) {
				loadTipss();
			} else if (msg.what == 2) {
				Toast.makeText(
						DeviceListActivity.this,
						DeviceListActivity.this.getResources().getString(
								R.string.notsb1), Toast.LENGTH_LONG).show();
			}
			super.handleMessage(msg);
		}

	};

	private class PwonerThread implements Runnable {
		private String mac;

		public PwonerThread(String mac) {
			this.mac = mac;
		}

		@Override
		public void run() {
			String str = "wlsn=" + mac + "&mac=" + phoneMac
					+ "&get&ispoweron&userid=" + userId + "&cmd";
			try {
				udpH.sendSb(Constant.IPADDRESS, Constant.WPORT, str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 处理返回的init设备的信息
	 * 
	 * @param message
	 */
	private void storeData(String message) {
	    // rep&timer=0&speed=3&pmx=15&air=A&mode=1&uv=1&anion=1&hepa=1&end
	    //rep&timer=0&speed=2&lock=1&air=x&mode=1&anion=1&swind=1&motor=1&t_filter1=800&t_filter2=4000&end
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

	private class StartThread implements Runnable {

		private String mac;

		public StartThread(String mac) {
			this.mac = mac;
		}

		@Override
		public void run() {
			hQhandle(mac);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 点击返回就退出应用
			EairApplaction.getInstance().exit();
		}
		return super.onKeyDown(keyCode, event);
	}

}
