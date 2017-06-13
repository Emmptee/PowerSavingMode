package com.example.shidongfang.myapplication.powersaving;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.example.shidongfang.myapplication.base.BaseApplication;
import com.example.shidongfang.myapplication.base.SPUtils;

/**
 * wifi状态
 *
 */

public class PowerSavingWifiController {

    private final WifiManager wifiManager;
    private static final String WIFICONTROLLER_STATE_KEY ="wificontroller_state_key";

    private PowerSavingWifiController(){
        wifiManager = (WifiManager) BaseApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private static PowerSavingWifiController instance;

    public static PowerSavingWifiController getInstance() {
        if (instance == null) {
            synchronized (PowerSavingWifiController.class) {
                if (instance == null) {
                    instance = new PowerSavingWifiController();
                }
            }
        }
        return instance;
    }

    public boolean getBeforeWifiState(){
        return SPUtils.getBoolean(WIFICONTROLLER_STATE_KEY);
    }

    public void setBeforeWifiState(boolean state){
        SPUtils.putBoolean(WIFICONTROLLER_STATE_KEY,state);
    }

    public void setWifiEnable(boolean state){
        if(state){
            if (getBeforeWifiState()) {
                wifiManager.setWifiEnabled(state);
            }
        }else{
            setBeforeWifiState(wifiManager.isWifiEnabled());
            wifiManager.setWifiEnabled(false);
        }
    }


}
