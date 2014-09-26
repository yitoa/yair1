
package com.ya.yair.adapter;

import java.util.List;
import com.ya.yair.R;
import com.ya.yair.activity.DeviceListActivity;
import com.ya.yair.activity.RenameActivity;
import com.ya.yair.domain.DeviceDomain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceAdapter extends BaseAdapter {

    private List<DeviceDomain> lists;

    private LayoutInflater mInflater;

    private Context context;
    
    private String userid;
    
    public DeviceAdapter(List<DeviceDomain> list, Context context,String userid) {
        this.lists = list;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.userid=userid;
    }

    @Override
    public int getCount() {

        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) { // 装载布局文件
            convertView = mInflater.inflate(R.layout.device_list_item_layout, null);
        }
        final DeviceDomain domain = lists.get(position);
        
        TextView deviceBm = (TextView) convertView.findViewById(R.id.device_bm);
        if(!("x".equals(domain.getBm())))
        {
        	deviceBm.setText(domain.getBm());        	
        }
        
        TextView deviceName = (TextView) convertView.findViewById(R.id.device_name);
        deviceName.setText(domain.getMac());
        
        TextView deviceState = (TextView) convertView.findViewById(R.id.eair);
        String stDeviceState = null;
        if("0".equals(domain.getPoweron()))
        {
        	if("A".equals(domain.getAir()))
        	{
        		stDeviceState = "空气  优";
        	}
        	else if("B".equals(domain.getAir()))
        	{
        		stDeviceState = "空气  良";
        	}
        	else if("C".equals(domain.getAir()))
        	{
        		stDeviceState = "空气  一般";
        	}
        	else if("D".equals(domain.getAir()))
        	{
        		stDeviceState = "空气  污染";
        	}
        	else if("E".equals(domain.getAir()))
        	{
        		stDeviceState = "空气  严重污染";
        	}
        	else if("x".equals(domain.getAir()))
        	{
        		stDeviceState = "暂未获取";
        	}
        	else
        	{
        		stDeviceState = "暂未获取";        		
        	}
        }
        else
        {
        	stDeviceState = "未开启";        
        }
        
        deviceState.setText(stDeviceState);
        
        TextView deviceIcon = (TextView) convertView.findViewById(R.id.device_icon);
        deviceIcon.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		System.out.println("Press Picture");
                Intent intent = new Intent();
                intent.setClass(context, RenameActivity.class); 
                Bundle bundle = new Bundle();
                bundle.putString("bm", domain.getBm());
                bundle.putString("mac", domain.getMac());
                bundle.putString("userid",userid);
                intent.putExtra("macbundle", bundle);
                context.startActivity(intent);
            }
        
        });
       	       	       
        return convertView;
    }

}
