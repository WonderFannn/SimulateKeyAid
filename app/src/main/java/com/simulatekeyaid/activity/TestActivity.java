package com.simulatekeyaid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.simulatekeyaid.R;
import com.simulatekeyaid.application.BaseApplication;
import com.simulatekeyaid.service.BackgroungSpeechRecongnizerService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangfan on 2017/9/18.
 */

public class TestActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.button)
    Button openBtn;
    @BindView(R.id.button2)
    Button closeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        openBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (openBtn  == view){
            Intent mBootIntent = new Intent(BaseApplication.getContext(), BackgroungSpeechRecongnizerService.class);
            BaseApplication.getContext().startService(mBootIntent);
        }else{

        }
    }
}
