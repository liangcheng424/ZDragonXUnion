package com.lmc.zdragonxunion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

public class MainAdActivity extends AppCompatActivity {

    private ImageView mIvImg;
    private TextView mTvTitle;
    int count = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ad);
        initView();
    }

    private void initView() {
        mIvImg = (ImageView) findViewById(R.id.iv_img);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        String imgUrl = "http://newoss.zhulong.com/ad/202004/07/20/1646208v7voilc29z1ymi8.jpg";
        Glide.with(this).load(imgUrl).into(mIvImg);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count--;
                        mTvTitle.setText("跳过"+count);
                        if(count==1){
                            startActivity(new Intent(MainAdActivity.this,HomeActivity.class));
                        }
                    }
                });
            }
        }, 0,1000);


        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                startActivity(new Intent(MainAdActivity.this,HomeActivity.class));
            }
        });
    }
}