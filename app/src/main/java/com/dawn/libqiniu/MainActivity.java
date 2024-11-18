package com.dawn.libqiniu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dawn.qiniu.QiNiuFactory;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView tvUpload;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvUpload = findViewById(R.id.tv_upload);
        qiniuInit();
        // 将drawable中的图片转换成bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demo_1);
        tvUpload.setOnClickListener(view -> {
            qiniuUploadImage(bitmap);
        });
    }
    public static final String ACCESS = "";
    public static final String SECRET = "";
    public static final String BUCKET = "";//区域
    public static final String PHOTO_HOST = "";
    QiNiuFactory qiNiuFactory;
    private void qiniuInit(){
        qiNiuFactory = QiNiuFactory.getInstance(ACCESS, SECRET, BUCKET, PHOTO_HOST);
    }

    private void qiniuUploadImage(Bitmap bitmap){
        String key = "img/" + System.currentTimeMillis() + new Random().nextInt(1000) + ".jpg";
        Log.e("dawn", "key: " + key);
        qiNiuFactory.uploadImage(bitmap, key, new QiNiuFactory.QiNiuUploadListener() {
            @Override
            public void uploadPercent(float percent) {
                Log.e("dawn", "percent: " + percent);
            }

            @Override
            public void uploadSuccess(String url) {
                Log.e("dawn", "url: " + url);
            }

            @Override
            public void uploadFail() {
                Log.e("dawn", "uploadFail");
            }
        });
    }
}