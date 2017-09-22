package com.simulatekeyaid.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.simulatekeyaid.service.SimulateKeyService;

/**
 * Created by wangfan on 2017/9/15.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        Intent mBootIntent = new Intent(context, SimulateKeyService.class);
        context.startService(mBootIntent);
    }
}
