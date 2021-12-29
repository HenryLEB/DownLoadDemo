package com.henry.downloaddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.daimajia.numberprogressbar.NumberProgressBar;

public class MainActivity extends AppCompatActivity implements ProgressListener {

    private NumberProgressBar pb;//进度条
    private DownLoadManager downLoadManager = null;
    private DownLoadFileInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (NumberProgressBar) findViewById(R.id.pb);
        final Button start = (Button) findViewById(R.id.start);//开始下载
        final Button restart = (Button) findViewById(R.id.restart);//重新下载
        final DownLoadDbHelper helper = new DownLoadDbHelper(this, "downLoad.db", null, 1);
        downLoadManager = downLoadManager.getInstance(this,helper, this);
        info = new DownLoadFileInfo("魔镜啊魔镜.apk", "https://yxfile.gowan8.com/putin/adpackage/307/307_123_1.0.1.apk");
        downLoadManager.addTask(info);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downLoadManager.getCurrentState(info.getUrl())) {
                    downLoadManager.stop(info.getUrl());
                    start.setText("开始下载");
                } else {
                    downLoadManager.start(info.getUrl());
                    start.setText("暂停下载");
                }
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadManager.restart(info.getUrl());
                start.setText("暂停下载");
            }
        });

    }

    @Override
    public void updateProgress(int max, int progress) {
        Log.e("哈哈", "Max: " + max + "  " + "Progress: " + progress);
        pb.setMax(max);
        pb.setProgress(progress);
    }
}