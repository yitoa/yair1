
package com.ya.yair.adapter;

import java.util.ArrayList;
import java.util.List;
import com.ya.yair.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimerAdapter extends BaseAdapter {

    private List<String> lists = new ArrayList<String>();

    private LayoutInflater mInflater;

    private int mSelection;

    private Context mContext;

    public TimerAdapter(Context context) {
        for (int i = 0; i <= 8; i++) {
            lists.add(String.valueOf(i));
        }
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {

        return  Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return  lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) { // 装载布局文件
            convertView = mInflater.inflate(R.layout.galley_item_layout, null);
        }
        TextView text = ((TextView) convertView.findViewById(R.id.text));
        if (this.mSelection != 8) {
            text.setTextSize(2, 30.0F);
            text.setTextColor(this.mContext.getResources().getColor(
                    R.color.color_white));
        } else {
            text.setTextSize(2, 20.0F);
            text.setTextColor(this.mContext.getResources().getColor(
                    R.color.color_white));
        }
        String time = lists.get(position%lists.size());
        text.setText(time);
        return convertView;
    }

    public void setOnselection(int paramInt)
    {
        this.mSelection = paramInt;
        notifyDataSetChanged();
    }

}
