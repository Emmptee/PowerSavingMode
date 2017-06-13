package com.example.shidongfang.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shidongfang.myapplication.powersaving.PowerSavingManager;
import com.sdf.mydemo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PowerSavingManager.getInstance().setPowerSavingMode(this);
    }

}
