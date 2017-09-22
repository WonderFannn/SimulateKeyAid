package com.simulatekeyaid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.simulatekeyaid.R;
import com.simulatekeyaid.application.BaseApplication;
import com.simulatekeyaid.broad.BroadcastManager;
import com.simulatekeyaid.service.SimulateKeyService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangfan on 2017/9/18.
 */

public class TestActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.button1)
    Button openBtn;
    @BindView(R.id.button2)
    Button closeBtn;
    @BindView(R.id.button3)
    Button BtnStart;
    @BindView(R.id.button4)
    Button BtnOver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        openBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        BtnStart.setOnClickListener(this);
        BtnOver.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (openBtn  == view){
            Intent intent = new Intent(BaseApplication.getContext(), SimulateKeyService.class);
            BaseApplication.getContext().startService(intent);
        }else if (closeBtn == view){
            Intent intent = new Intent(BaseApplication.getContext(), SimulateKeyService.class);
            BaseApplication.getContext().stopService(intent);
        }
    }
}
