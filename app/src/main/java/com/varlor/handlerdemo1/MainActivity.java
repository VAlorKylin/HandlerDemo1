package com.varlor.handlerdemo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //UI线程开启
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView =findViewById(R.id.textview);
        //创建Handler
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //处理消息
                Log.i(TAG,"handleMessage:"+msg.what);
                if (msg.what==1002){
                    textView.setText("XXXXX");
                    Log.d(TAG,"handlerMessage:"+msg.arg1);
                    Log.d(TAG,"handlerMessage:"+msg.arg2);
                    Log.d(TAG,"handlerMessage:"+msg.obj);
                }
            }
        };
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模拟大量耗时操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1001);

                        //Message message = new Message();不建议用
                        Message message = Message.obtain();
                        message.what=1002;
                        message.arg1=1003;
                        message.arg2=1004;
                        message.obj=MainActivity.this;

                        handler.sendMessage(message);
                        //定时发送（绝对时间）
                        handler.sendMessageAtTime(message, SystemClock.uptimeMillis()+3000);
                        //定时发送(相对时间)
                        handler.sendMessageDelayed(message,2000);
                        //
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                int a=1+2+3;
                            }
                        };
                        handler.post(runnable);
                        runnable.run();
                    }
                }).start();

            }
        });



    }
}