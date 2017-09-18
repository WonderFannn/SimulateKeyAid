package com.simulatekeyaid.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.simulatekeyaid.application.BaseApplication;
import com.simulatekeyaid.broad.BroadcastManager;
import com.simulatekeyaid.util.JsonParser;
import com.simulatekeyaid.util.RootShellCmd;
import com.simulatekeyaid.util.ToastUtil;

public class BackgroungSpeechRecongnizerService extends Service {

    // 语音听写对象
    public static SpeechRecognizer hearer;
    //监听线程
    ListenThread mListenThread;

    RootShellCmd mRSC;

    @Override
    public void onCreate() {
        Log.d("wangfan", "BackgroungSpeechRecongnizerService created");
        super.onCreate();
        ToastUtil.showShort(BaseApplication.getContext(),"语音监听服务启动了");
        mRSC = new RootShellCmd();
        //注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastManager.ACTION_VOICE_EMULATE_KEY_OPEN);
        mFilter.addAction(BroadcastManager.ACTION_VOICE_EMULATE_KEY_CLOSE);
        registerReceiver(wakeBroadcast, mFilter);

        // 初始化
        init();
    }

    private void init() {
        hearer = SpeechRecognizer.createRecognizer(this, mInitListener);
        setParameter();
        // 非空判断，防止因空指针使程序崩溃
        if (hearer != null) {
//            BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_EMULATE_KEY_OPEN, null);
        } else {
//            ToastUtil.showShort(getApplicationContext(), "未初始化唤醒对象");
        }
    }


    /**
     * 设置听写对象
     */
    private void setParameter() {
        // domain:域名
        hearer.setParameter(SpeechConstant.DOMAIN, "iat");
        // zh_cn汉语
        hearer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // mandarin:普通话
        hearer.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        hearer.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        hearer.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        hearer.setParameter(SpeechConstant.ASR_PTT,  "0");
    }

    public class ListenThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while(true){
                    if (hearer != null && !hearer.isListening()) {
                        Thread.sleep(300);
                        hearer.startListening(mRecoListener);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Log.i("TAG", "初始化失败，错误码：" + i);
            }
        }
    };

    /**
     * 听写监听器
     */
    private RecognizerListener mRecoListener = new RecognizerListener(){
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            parsedResult(results, isLast);
        }

        @Override
        public void onBeginOfSpeech() {
            //开始录音
            Log.i("TAG", "开始录音!");
        }

        @Override
        public void onEndOfSpeech() {
            Log.i("TAG", "结束录音!");
        }

        @Override
        public void onError(SpeechError error) {
            if(error.getErrorCode()!=10118){
                ToastUtil.showShort(getApplicationContext(), "onError:"+error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

        @Override
        public void onVolumeChanged(int i, byte[] b) {

        }
    };

    /**
     * 解析json并显示听写内容（执行唤醒内容）
     * @param results
     * 			json数据
     * @param isLast
     * 			是否是最后一句话
     */
    private void parsedResult(RecognizerResult results, Boolean isLast) {

        String text = null;
        Log.i("TAG", "parsedJsonAndSetText：" + results.getResultString());
        try {
            text = JsonParser.parseIatResult(results.getResultString());
            if(TextUtils.isEmpty(text)){
                return;
            }
            Log.i("TAG", "WS->result:"+text);
            if(!TextUtils.isEmpty(text)){
                ToastUtil.showShort(BaseApplication.getContext(),text);
                if (text.equals("返回")){
                    setKeyPress(KeyEvent.KEYCODE_BACK);
                }else if (text.equals("向上")){
                    setKeyPress(KeyEvent.KEYCODE_DPAD_UP);
                }else if (text.equals("向下")){
                    setKeyPress(KeyEvent.KEYCODE_DPAD_DOWN);
                }else if (text.equals("向左")){
                    setKeyPress(KeyEvent.KEYCODE_DPAD_LEFT);
                }else if (text.equals("向右")){
                    setKeyPress(KeyEvent.KEYCODE_DPAD_RIGHT);
                }else if (text.equals("确定")){
                    setKeyPress(KeyEvent.KEYCODE_DPAD_CENTER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (BroadcastManager.ACTION_VOICE_EMULATE_KEY_OPEN.equals(action)) {
                if (hearer != null) {
                    hearer.startListening(mRecoListener);
                }else {
                    hearer = SpeechRecognizer.createRecognizer(context, mInitListener);
                    hearer.startListening(mRecoListener);
                }
                mListenThread = new ListenThread();
                mListenThread.start();
            }else if(BroadcastManager.ACTION_VOICE_EMULATE_KEY_CLOSE.equals(action)){
                if(mListenThread!=null){
                    mListenThread.interrupt();
                    mListenThread = null;
                }
                if (hearer != null) {
                    hearer.stopListening();
                }
            }

        }
    };
}