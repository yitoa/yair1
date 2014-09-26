
package com.ya.yair.activity;

import com.ya.yair.Constant;
import com.ya.yair.EairApplaction;
import com.ya.yair.R;
import com.ya.yair.util.UserWebServiceUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RenameActivity extends BaseActivity implements OnClickListener {

    private EditText devicename;
    private EditText devicecode;
    private Button btn_OK;
    private Button btn_renameback;
    private String bm;
    private String mac;
    private String userid;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EairApplaction.getInstance().addActivity(this);
        setContentView(R.layout.rename_layout);
        Bundle bundle = this.getIntent().getBundleExtra("macbundle");
		if (null == bundle)  
		{
			mac = null;
			bm = null;		
			userid=null;
		}
		else
		{
			mac = bundle.getString("mac");
			bm = bundle.getString("bm");
			userid=bundle.getString("userid");
		}
		
        devicename = (EditText) this.findViewById(R.id.device_name);
        if(!("x".equals(bm)))
        {
        	devicename.setText(bm);        	
        }
        
        devicecode = (EditText) this.findViewById(R.id.device_code);      
        devicecode.setText(mac);
        
        btn_OK = (Button) this.findViewById(R.id.btn_OK);
        btn_OK.setOnClickListener(this);
        btn_renameback = (Button) this.findViewById(R.id.renameback);
        btn_renameback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    	
    	if (v == btn_renameback) {
            Intent intent = new Intent(RenameActivity.this, DeviceListActivity.class);
            RenameActivity.this.startActivity(intent);
            RenameActivity.this.finish();
    	}
    	
        if (v == btn_OK) {
        	String devicebm = devicename.getText().toString();
        	if (devicebm == null) 
        	{
                Toast.makeText(this, this.getResources().getString(R.string.notrename),
                		Toast.LENGTH_LONG).show();
                return;
            }
        	else 
        	{
        		if ("".equals(devicebm))
        		{
        			devicebm = "x";
        		}
        		UserWebServiceUtil wsu = new UserWebServiceUtil();
                String replyMess = wsu.updBM(mac, devicebm,userid);
                if (replyMess == null) {
                    try {
                        Thread.sleep(Constant.SLEEPTIME);
                        replyMess = wsu.updBM(mac, devicebm,userid);
                        if (replyMess == null) {
                            Thread.sleep(Constant.SLEEPTIME);
                            replyMess = wsu.updBM(mac, devicebm,userid);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                
                if ((null != replyMess)&&(!("0".equals(replyMess)))) 
                {
                	Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", DeviceListActivity.g_userId);
                    intent.putExtra("sbbundle", bundle);                   
                	DeviceListActivity.devicelistinstance.finish();
                	intent.setClass(RenameActivity.this, DeviceListActivity.class);
                    RenameActivity.this.startActivity(intent);
                    RenameActivity.this.finish();
                }
                else 
                {
                    Toast.makeText(this, this.getResources().getString(R.string.notrename),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                
                
            }
        	
        }
    }
}
