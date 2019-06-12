package com.bnv.liudongxun.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.bnv.liudongxun.bluetoothdemo.adapter.RCAdapter;
import com.bnv.liudongxun.bluetoothdemo.bean.BToothDeviceInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口的通用UUID,UUID是什么东西
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private RecyclerView recyclerView;//显示蓝牙设备
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mainactivity_recyclerview);
        recyclerView.setAdapter(new RCAdapter(deviceList));
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//.ACTION_STATE_CHANGED监听蓝牙状态，
        registerReceiver(new MyBluetoothReceiver(), intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(BluetoothDevice.ACTION_FOUND);//BluetoothDevice.ACTION_FOUND  注意这个action所属的类
        registerReceiver(new MyBluetoothDiscoveryReceiver(), intentFilter1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.enable()) {//当前设备支持蓝牙接下来判断蓝牙是否可用
                Log.d(TAG, "onCreate: 当前蓝牙已经打开");
//                Set<BluetoothDevice> set =bluetoothAdapter.getBondedDevices();//获取可连接的蓝牙设备信息,这个是获取到已配对的蓝牙信息
//                if (set.size() > 0) {
//                    for (BluetoothDevice device : set) {
//                        String deviceName = device.getName();
//                        String deviceHardwareAddress = device.getAddress(); // MAC address
//                        Log.d(TAG, "onCreate: 成功获取到可连接蓝牙信息 deviceName="+deviceName+" deviceHardwareAddress="+deviceHardwareAddress);
//                    }
//                }

                //==========查询已连接的蓝牙信息========
                boolean ifStartDiscovery = bluetoothAdapter.startDiscovery();//开始寻找蓝牙设备,异步执行,返回的boolean值表示是否已经开始寻找蓝牙设备
                //调用startDiscovery寻找设备比较占用蓝牙资源 不需要的时候及时调用cacelDiscovery()停止搜索蓝牙设备
                if (ifStartDiscovery) {
                    Log.d(TAG, "onCreate: 开始寻找周围的蓝牙设备");
                }else {
                    Log.d(TAG, "onCreate: 开始寻找蓝牙设备失败");
                }

            } else {//蓝牙不可用,调用系统dialog提醒用户打开蓝牙
                Log.d(TAG, "onCreate: 当前蓝牙未打开 提醒用户打开蓝牙");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.d(TAG, "onCreate: 设备不支持蓝牙");
            //bluetoothAdapter为null表示设备不支持蓝牙
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {//请求开启蓝牙的回调结果
            if (resultCode == Activity.RESULT_OK) {//用户允许打开蓝牙
                Log.d(TAG, "onActivityResult:用户允许打开蓝牙 ");
            } else if (resultCode == Activity.RESULT_CANCELED) {//打开蓝牙失败或者用户拒绝打开蓝牙
                Log.d(TAG, "onActivityResult: 2222222222");
            }
        }
    }


    /**
     * 监听搜索附近蓝牙设备的结果
     */
    private class MyBluetoothDiscoveryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//通过广播监听蓝牙设备搜索的结果
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();//名字返回null,表示发生了问题  这种设备一般过滤掉
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if (deviceName != null && deviceName.equals("魅蓝")) {
                    Log.d(TAG, "onReceive: 11111111111111");
//                    try {
//                        device.createBond();//建立连接?????????
//                        BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//                        bluetoothAdapter.cancelDiscovery();//发现是个耗时需要及时取消
//                        //     bluetoothSocket.connect();//打开socketl连接,这个是耗时方法,不能在UI线程调用
//                        Log.d(TAG, "onReceive: 开始和魅蓝设备建立通讯,接收魅蓝的数据");
//                        new MyReadDataThread(bluetoothSocket);
//                    } catch (IOException e) {
//                        Log.d(TAG, "onReceive: IOException=" + e.getMessage());
//                        e.printStackTrace();
//                    }
                }
                Log.d(TAG, "onReceive: 获取到附近蓝牙设备的结果 deviceName=" + deviceName + " deviceHardwareAddress=" + deviceHardwareAddress);
            }
        }
    }


    private class MyReadDataThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        private InputStream inStream;

        MyReadDataThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
        }

        @Override
        public void run() {
            super.run();
            try {
                bluetoothSocket.connect();
                inStream = bluetoothSocket.getInputStream();
            } catch (IOException e) {

            }
            while (true) {
                if (inStream == null) {
                    return;
                }
                byte[] buff = new byte[1024];
                try {
                    inStream.read(buff); //读取数据存储在buff数组中
                    Log.d(TAG, "run: ");
                    //       processBuffer(buff,1024);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 广播接收蓝牙的状态
     */
    private class MyBluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int oldBlutoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
            int currentBlutoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if (currentBlutoothState == BluetoothAdapter.STATE_ON) {
                Log.d(TAG, "onReceive:STATE_ON ");
            } else if (currentBlutoothState == BluetoothAdapter.STATE_OFF) {
                Log.d(TAG, "onReceive: STATE_OFF");
            } else if (currentBlutoothState == BluetoothAdapter.STATE_TURNING_OFF) {
                Log.d(TAG, "onReceive: STATE_TURNING_OFF");
            } else if (currentBlutoothState == BluetoothAdapter.STATE_TURNING_ON) {
                Log.d(TAG, "onReceive:STATE_TURNING_ON ");
            }
        }
    }
}
