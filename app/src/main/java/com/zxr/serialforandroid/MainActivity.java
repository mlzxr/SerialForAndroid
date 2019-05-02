package com.zxr.serialforandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private TemperatureUsbControl mTemperatureUsbControl;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        initUsbControl();       //初始化USB控制器
    }

    /**
     * 初始化USB
     */
    private void initUsbControl() {
        mTemperatureUsbControl = new TemperatureUsbControl(mContext);
        mTemperatureUsbControl.initUsbControl();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbFilter);
        mTemperatureUsbControl.onDeviceStateChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTemperatureUsbControl.onPause();
        unregisterReceiver(mUsbReceiver);
    }

    /**
     * 用于检测usb插入状态的BroadcasReceiver
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                //设备插入
                mTemperatureUsbControl.initUsbControl();
                mTemperatureUsbControl.onDeviceStateChange();
                Log.e(TAG, "ACTION_USB_DEVICE_ATTACHED");
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //设备移除
                mTemperatureUsbControl.onPause();
                Log.e(TAG, "ACTION_USB_DEVICE_DETACHED");
            }
        }
    };
}
