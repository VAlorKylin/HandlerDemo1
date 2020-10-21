package com.varlor.handlerdemo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadActivity extends AppCompatActivity {
    private Handler handler;
    public static final int  DOWNLOAD_MESSAGE_CODE=10000;
    public static final int  DOWNLOAD_MESSAGE_FAIL_CODE=20000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        /**
         * 主线程--》
         * 点击按钮
         * 发起下载
         * 开启子线程下载
         * 下载过程中通知主线程=》》主线程更新进度条
         */
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download("https://down.qq.com/qqweb/QQlite/Android_apk/qqlite_4.0.1.1060_537064364.apk");
                    }
                }).start();
                }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case DOWNLOAD_MESSAGE_CODE:
                        progressBar.setProgress((Integer) msg.obj);
                        break;
                    case DOWNLOAD_MESSAGE_FAIL_CODE:

                }
            }
        };
    }

    private void download(String appUrl) {
        try {
            URL url = new URL(appUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            //获取文件的总长度
            int contentLength = urlConnection.getContentLength();
            File downloadFolder = getFilesDir();

//            File file = new File(downloadFolderName);
//            if (!file.exists()){
//                file.mkdir();
//            }
            String fileName = downloadFolder+"QQMobile.apk";
            File apkFile = new File(downloadFolder,fileName);
            if (apkFile.exists()){
                apkFile.delete();
            }
            //下载的长度
            int downloadSize = 0;
            byte[] bytes = new byte[1024];
            int length = 0;
            OutputStream outputStream = new FileOutputStream(fileName);
            while ((length = inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,length);
                downloadSize += length;
                /**
                 * 更新UI
                 */
                Message message = Message.obtain();
                message.obj = downloadSize*100/contentLength;
                message.what=DOWNLOAD_MESSAGE_CODE;
                handler.sendMessage(message);
            }
            inputStream.close();
            outputStream.close();
        } catch (MalformedURLException e) {
            Message message = Message.obtain();
            message.what=DOWNLOAD_MESSAGE_FAIL_CODE;
            handler.sendMessage(message);
            e.printStackTrace();
        } catch (IOException e) {
            Message message = Message.obtain();
            message.what=DOWNLOAD_MESSAGE_FAIL_CODE;
            handler.sendMessage(message);
            e.printStackTrace();
        }
    }
}