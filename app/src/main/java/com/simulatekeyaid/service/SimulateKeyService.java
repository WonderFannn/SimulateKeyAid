package com.simulatekeyaid.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.simulatekeyaid.application.BaseApplication;
import com.simulatekeyaid.broad.BroadcastManager;
import com.simulatekeyaid.util.RootShellCmd;
import com.simulatekeyaid.util.ToastUtil;

public class SimulateKeyService extends Service {

    RootShellCmd mRSC;

    @Override
    public void onCreate() {
        Log.d("wangfan", "SimulateKeyService created");
        super.onCreate();
        ToastUtil.showShort(BaseApplication.getContext(),"按键模拟服务启动了");
        mRSC = new RootShellCmd();
        //注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_HOME);
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_BACK);
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_CENTER);
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_UP);
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_DOWN);
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_LEFT);
        mFilter.addAction(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_RIGHT);
        registerReceiver(wakeBroadcast, mFilter);
    }

    @Override
    public void onDestroy() {
        ToastUtil.showShort(BaseApplication.getContext(),"按键模拟服务已关闭");
        unregisterReceiver(wakeBroadcast);
        super.onDestroy();
    }

    public void setKeyPress(final int keycode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRSC.simulateKey(keycode);
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 唤醒广播
     */
    private BroadcastReceiver wakeBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastManager.ACTION_SIMULATE_KEY_HOME.equals(action)) {
                setKeyPress(KeyEvent.KEYCODE_HOME);
            }else if(BroadcastManager.ACTION_SIMULATE_KEY_BACK.equals(action)){
                setKeyPress(KeyEvent.KEYCODE_BACK);
            }else if(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_CENTER.equals(action)){
                setKeyPress(KeyEvent.KEYCODE_DPAD_CENTER);
            }else if(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_UP.equals(action)){
                setKeyPress(KeyEvent.KEYCODE_DPAD_UP);
            }else if(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_DOWN.equals(action)){
                setKeyPress(KeyEvent.KEYCODE_DPAD_DOWN);
            }else if(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_LEFT.equals(action)){
                setKeyPress(KeyEvent.KEYCODE_DPAD_LEFT);
            }else if(BroadcastManager.ACTION_SIMULATE_KEY_DPAD_RIGHT.equals(action)){
                setKeyPress(KeyEvent.KEYCODE_DPAD_RIGHT);
            }
        }
    };
}
