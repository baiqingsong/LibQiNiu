package com.dawn.qiniu;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import java.io.File;

public class QiNiuFactory {
    //单例模式
    private static QiNiuFactory instance;
    private UploadManager uploadManager;
    private QiNiuFactory(String access, String secret, String bucket, String host, CustomZone customZone){
        this.access = access;
        this.secret = secret;
        this.bucket = bucket;
        this.host = host;
        uploadManager = QiNiuManager.getSingleton(customZone);
    }
    public static QiNiuFactory getInstance(String access, String secret, String bucket, String host, CustomZone customZone){
        if(instance == null){
            synchronized (QiNiuFactory.class){
                if(instance == null){
                    instance = new QiNiuFactory(access, secret, bucket, host, customZone);
                }
            }
        }
        return instance;
    }
    public static QiNiuFactory getInstance(String access, String secret, String bucket, String host){
        if(instance == null){
            synchronized (QiNiuFactory.class){
                if(instance == null){
                    instance = new QiNiuFactory(access, secret, bucket, host, CustomZone.ZONE_HUA_DONG);
                }
            }
        }
        return instance;
    }

    private String access;//AccessKey
    private String secret;//SecretKey
    private String bucket;//区域
    private String host;//域名
    private long timePre;//上一次时间
    private String token;//七牛云token值

    /**
     * 获取上传凭证
     */
    private String getUpToken(){
        return QiNiuUtil.create(access, secret).uploadToken(bucket);
    }

    /**
     * 检查token
     */
    private String checkUpToken(){
        long timeCurrent = System.currentTimeMillis()/1000;
        if((timeCurrent - timePre) > 3000){//默认时间3600秒
            timePre = System.currentTimeMillis()/1000;
            token = getUpToken();
        }
        return token;
    }

    /**
     * 文件上传
     * @param fileName 文件名称
     * @param key 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadFile(String fileName, String key, QiNiuUploadListener listener){
        uploadFile(null, fileName, key, listener);
    }

    /**
     * 文件上传
     * @param token 七牛云token值
     * @param fileName 文件名称
     * @param key 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadFile(String token, String fileName, String key, QiNiuUploadListener listener){
        if(TextUtils.isEmpty(fileName) || !new File(fileName).exists())
            return;
        uploadFile(token, FileUtils.readFile(fileName), key, listener);
    }

    /**
     * 图片上传
     * @param bitmap 图片
     * @param key 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadImage(Bitmap bitmap, String key, QiNiuUploadListener listener){
        uploadImage(null, bitmap, key, listener);
    }

    /**
     * 图片上传
     * @param token 七牛云token值
     * @param bitmap 图片
     * @param key 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadImage(String token, Bitmap bitmap, String key, QiNiuUploadListener listener){
        if(bitmap == null){
            if(listener != null)
                listener.uploadFail(key);
            return;
        }
        uploadFile(token, StringUtils.Bitmap2Bytes(bitmap), key, listener);
    }

    /**
     * 文件上传
     * @param data 文件数据
     * @param upKey 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadFile(byte[] data, String upKey, QiNiuUploadListener listener) {
        uploadFile(null, data, upKey, listener);
    }

    /**
     * 文件上传
     * @param token 七牛云token值
     * @param data 文件数据
     * @param upKey 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadFile(String token, byte[] data, String upKey, QiNiuUploadListener listener){
        //定义数据上传结束后的处理动作
        final UpCompletionHandler upCompletionHandler = (key, info, response) -> {
            if (info.isOK()) {
                if(listener != null)
                    listener.uploadSuccess(host + key);
            }else{
                if(listener != null)
                    listener.uploadFail(host + key);
            }
        };
        final UploadOptions uploadOptions = new UploadOptions(null, null, false, (key, percent) -> {
            if(listener != null)
                listener.uploadPercent((float) percent);
        }, () -> false);
        try {
            if(TextUtils.isEmpty(token)){
                token = checkUpToken();
            }
            final String finalToken = token;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    uploadManager.put(data, upKey, finalToken, upCompletionHandler, uploadOptions);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface QiNiuUploadListener{
        void uploadPercent(float percent);
        void uploadSuccess(String url);
        void uploadFail(String url);
    }

}
