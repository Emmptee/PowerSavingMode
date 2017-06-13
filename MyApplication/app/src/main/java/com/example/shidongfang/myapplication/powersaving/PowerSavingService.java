package com.example.shidongfang.myapplication.powersaving;

import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.example.shidongfang.myapplication.base.BaseApplication;
import com.socks.library.KLog;


public class PowerSavingService extends Service {
    private static final String TAG = "powersavingservice";
    private BluetoothManager mBtManager;
    private WifiManager wifiManager;
    private PowerManager powerManager;
    private BatteryManager batteryManager;


    private BroadcastReceiver mSreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //设置延时关闭策略
                KLog.a(TAG, Intent.ACTION_SCREEN_OFF);
                //if (!isConnected(context)) {
                    PowerSavingManager.getInstance().timerTenMin(context);
                    KLog.a(TAG,"ScreenOff,Ten min delay timer start");
                //}
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //打开蓝牙和wifi并取消延时关闭策略
                KLog.a(TAG, Intent.ACTION_SCREEN_ON);
                PowerSavingManager.getInstance().cancelTimer(context);
                KLog.a(TAG,"ScreenOn,Ten min delay timer cancel");

                mBtManager.getAdapter().enable();
                PowerSavingWifiController.getInstance().setWifiEnable(true);
                KLog.a(TAG,"ScreenOn, WIFI and Bluetooth is useful");

            }
        }
    };
    private BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Intent serviceIntent = new Intent(context, PowerSavingService.class);
            if (Intent.ACTION_TIME_CHANGED.equals(action)){
                KLog.a(TAG,Intent.ACTION_TIME_CHANGED);
//                PowerSavingManager.getInstance().cancelsetPowerSavingMode(context);
//                PowerSavingManager.getInstance().cancelendPowerSavingMode(context);
                context.stopService(serviceIntent);
                KLog.a(TAG,"TimeChanged,PowerSavingService stopped");
                mBtManager = (BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
                mBtManager.getAdapter().enable();
                PowerSavingWifiController.getInstance().setWifiEnable(true);
                KLog.a(TAG,"手动修改时间，---- 取消延时策略，开启蓝牙和WIFI");
            }
        }
    };


/*    private BroadcastReceiver mPowerReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC || chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                KLog.a(TAG,"Ischarging");
                PowerSavingManager.getInstance().cancelTimer(context);
                mBtManager.getAdapter().enable();
                KLog.a(TAG,"Ischarging,  Bluetooth is useful");

                PowerSavingWifiController.getInstance().setWifiEnable(true);
                KLog.a(TAG,"Ischarging,  WIFI is useful");

            } else {
                KLog.a(TAG,"Not charged");
                if (!powerManager.isScreenOn()) {
                    PowerSavingManager.getInstance().timerTenMin(context);
                    KLog.a(TAG,"Not charged,ScreenOff，Ten min delay timer start");

                }
            }

        }
    };*/



//    private BroadcastReceiver mWIFIStateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            int wifiState = wifiManager.getWifiState();
//            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
//                switch (wifiState) {
//                    case WifiManager.WIFI_STATE_DISABLING:
//                        break;
//                    case WifiManager.WIFI_STATE_DISABLED:
//                        break;
//                    case WifiManager.WIFI_STATE_ENABLING:
//                        break;
//                    case WifiManager.WIFI_STATE_ENABLED:
//                        break;
//                    case WifiManager.WIFI_STATE_UNKNOWN:
//                        break;
//                }
//            }
//        }
//    };

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.a(TAG,"PowerSavingService is created");
        mBtManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);


        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mSreenReceiver, screenFilter);

        IntentFilter timechangeFilter = new IntentFilter();
        timechangeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timechangeFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mTimeChangeReceiver,timechangeFilter);


/*        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mPowerReceiver, batteryFilter);*/

//      IntentFilter wifiFilter = new IntentFilter();
//      wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//      wifiFilter.addCategory(Intent.CATEGORY_DEFAULT);
//      registerReceiver(mWIFIStateReceiver, wifiFilter);


//        PowerSavingWifiController.getInstance().setBeforeWifiState(wifiManager.isWifiEnabled());
        //进入23点，屏幕关闭，可能不会触发ACTION_SCREEN_OFF
        if (!powerManager.isScreenOn() /*&& !isConnected(this)*/) {
            PowerSavingManager.getInstance().timerTenMin(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSreenReceiver);
//        unregisterReceiver(mPowerReceiver);
        unregisterReceiver(mTimeChangeReceiver);
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

}
