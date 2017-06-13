package com.example.shidongfang.myapplication.powersaving;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;

import com.example.shidongfang.myapplication.base.BaseApplication;

import com.socks.library.KLog;

import java.util.Calendar;




public class PowerSavingManager {
    private static final String TAG = "powersavingmanager";
    private static final long INTERVAL = 6000 * 2;
    private static final int START_TIME = 23;
    private static final int END_TIME = 9;
    private static final String IS_POWER_SAVEMODE = "is_power_saving";
    private static final int POWER_SAVEMODE_ON_REQUESTCODE = 10001;
    private static final int POWER_SAVEMODE_OFF_REQUESTCODE = 10002;
    private static final int POWER_SAVEMODE_OUT_REQUESTCODE = 10003;


    private static PowerSavingManager instance;
    private static BluetoothManager mBtManager;
    private static WifiManager wifiManager;
    private static Boolean section;

    private PowerSavingManager() {
    }

    public static PowerSavingManager getInstance() {
        if (instance == null) {
            synchronized (PowerSavingManager.class) {
                if (instance == null) {
                    instance = new PowerSavingManager();
                }
            }
        }
        return instance;
    }

    public static class PowerDelayReceiver extends BroadcastReceiver {
        public PowerDelayReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            KLog.a(TAG, "PowerDelayReceiver is receive");

            mBtManager = (BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            wifiManager = (WifiManager) BaseApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mBtManager.getAdapter().isEnabled()) {
                mBtManager.getAdapter().disable();
                KLog.a(TAG, "Ten minute delay timer shut down bluetooth");
            }
            if (wifiManager.isWifiEnabled()) {
                PowerSavingWifiController.getInstance().setWifiEnable(false);
                KLog.a(TAG, "Ten minute delay timer shut down WIFI");

            }
        }

    }

    public static class PowerSavingReceiver extends BroadcastReceiver {

        public PowerSavingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPowerSaving = intent.getBooleanExtra(IS_POWER_SAVEMODE, false);
            Intent serviceIntent = new Intent(context, PowerSavingService.class);
            if (isPowerSaving) {
                KLog.a(TAG, "ISPOWERSAVING========================" + isPowerSaving);

                //23点进入启动service
                context.startService(serviceIntent);

                //LocationManager.getInstance().sendLocationBroadCast();

                KLog.a(TAG, "11，powersavingService start");
                //并设置9点退出定时器
                PowerSavingManager.getInstance().endPowerSavingMode(context);

            } else {
                KLog.a(TAG, "ISPOWERSAVING++++++++++++++++++" + isPowerSaving);
                //9点进入关闭service
                context.stopService(serviceIntent);
                KLog.a(TAG, "Nine Clock , powersavingService stop");
                PowerSavingManager.getInstance().setPowerSavingMode(context);

                PowerSavingWifiController.getInstance().setWifiEnable(true);
                mBtManager = (BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
                mBtManager.getAdapter().enable();
                KLog.a(TAG, "Nine Clock,  WIFI and Bluetooth are enable");

            }


        }
    }
//    public static class TimeChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Intent serviceIntent = new Intent(context, PowerSavingService.class);
//
//            if (Intent.ACTION_TIME_CHANGED.equals(action)){
//
//                KLog.a(TAG,Intent.ACTION_TIME_CHANGED);
//                PowerSavingManager.getInstance().cancelsetPowerSavingMode(context);
//                PowerSavingManager.getInstance().cancelendPowerSavingMode(context);
//                context.stopService(serviceIntent);
//                KLog.a(TAG,"TimeChanged,PowerSavingService stopped");
//                mBtManager = (BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
//                mBtManager.getAdapter().enable();
//                PowerSavingWifiController.getInstance().setWifiEnable(true);
//                KLog.a(TAG,"手动修改时间，---- 取消延时策略，开启蓝牙和WIFI");
//            }
//        }
//    }


    /**
     * 夜间11点开启计时器，可以在BaseApplication中调用此方法，为整个逻辑的开始
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void setPowerSavingMode(Context context) {

        if (isOneToNine().equals(true)){

        }
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, START_TIME);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        Intent startIntent = new Intent(context, PowerSavingReceiver.class);
        startIntent.putExtra(IS_POWER_SAVEMODE, true);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, POWER_SAVEMODE_ON_REQUESTCODE, startIntent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
        KLog.a(TAG, calendar.getTime().toString());

    }

    /**
     * 上午9点关闭计时器
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void endPowerSavingMode(Context context) {
        long l = Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 60 * 10;

        Intent endIntent = new Intent(context, PowerSavingReceiver.class);
        endIntent.putExtra(IS_POWER_SAVEMODE, false);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, POWER_SAVEMODE_OFF_REQUESTCODE, endIntent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, l, broadcast);
//        Klog.a(TAG, new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss SSS a").format(new Date(l)) +
//                "Shut down interval timer in morning");
    }
    /**
     * 23点计时器取消
     *
     * @param context
     */
    public void cancelsetPowerSavingMode(Context context) {

        KLog.a(TAG, "Eleven oclock timer cancel");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent cancelIntent = new Intent(context, PowerSavingReceiver.class);
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(context, POWER_SAVEMODE_ON_REQUESTCODE, cancelIntent, 0);
        alarmManager.cancel(broadcastIntent);
    }
    /**
     * 9点计时器取消
     *
     * @param context
     */
    public void cancelendPowerSavingMode(Context context) {

        KLog.a(TAG, "Nine oclock timer cancel");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent cancelIntent = new Intent(context, PowerSavingReceiver.class);
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(context, POWER_SAVEMODE_OFF_REQUESTCODE, cancelIntent, 0);
        alarmManager.cancel(broadcastIntent);
    }

    /**
     * 延时10分钟计时器
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void timerTenMin(Context context) {
        cancelTimer(context);
        KLog.a(TAG, "Ten minute delay timer start");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Intent endIntent = new Intent(context, PowerDelayReceiver.class);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, endIntent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + INTERVAL, broadcast);

    }

    /**
     * 10分钟延时计时器取消
     *
     * @param context
     */
    public void cancelTimer(Context context) {

        KLog.a(TAG, "Ten minute delay timer cancel");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent endIntent = new Intent(context, PowerDelayReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, endIntent, 0);
        alarmManager.cancel(broadcast);
    }
    public Boolean isOneToNine(){
        boolean section = false;
        Calendar calendar=Calendar.getInstance();
        Calendar calendar1=(Calendar) calendar.clone();
        Calendar calendar2=(Calendar) calendar.clone();
        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.HOUR, 9);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        if(calendar.after(calendar1)&&calendar.before(calendar2)){
            section = true;
        }
        return section;

    }

//    public void getLocation(Context context){
//        Intent locationintent = new Intent(context, LocationManager.LocationRecever.class);
//    }
}
