package com.bnv.liudongxun.bluetoothdemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bnv.liudongxun.bluetoothdemo.R;
import com.bnv.liudongxun.bluetoothdemo.bean.BToothDeviceInfo;

import java.util.List;
import java.util.Map;

/**
 * rcview适配器
 */
public class RCAdapter extends RecyclerView.Adapter<RCAdapter.VH> {
    private static final String TAG = "RCAdapter";
    private List<BluetoothDevice> mDatas;

    public RCAdapter(List<BluetoothDevice> data) {
        this.mDatas = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcview_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.title.setText(mDatas.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class VH extends RecyclerView.ViewHolder {
        public final TextView title;

        public VH(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.mainactivity_recyclerview_item_bluetooth_name);
        }
    }
}
