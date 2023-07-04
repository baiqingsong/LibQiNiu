package com.dawn.qiniu;

import android.graphics.Bitmap;

import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;

public class QiNiuFactory {
    //单例模式
    private static QiNiuFactory instance;
    private QiNiuFactory(String access, String secret, String bucket, String host){
        this.access = access;
        this.secret = secret;
        this.bucket = bucket;
        this.host = host;
    }
    public static QiNiuFactory getInstance(String access, String secret, String bucket, String host){
        if(instance == null){
            synchronized (QiNiuFactory.class){
                if(instance == null){
                    instance = new QiNiuFactory(access, secret, bucket, host);
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
        uploadFile(FileUtils.readFile(fileName), key, listener);
    }

    /**
     * 图片上传
     * @param bitmap 图片
     * @param key 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadImage(Bitmap bitmap, String key, QiNiuUploadListener listener){
        if(bitmap == null){
            if(listener != null)
                listener.uploadFail();
            return;
        }
        uploadFile(StringUtils.Bitmap2Bytes(bitmap), key, listener);
    }

    /**
     * 文件上传
     * @param data 文件数据
     * @param upKey 七牛云上传路径，包括文件夹和文件名称
     * @param listener 上传监听
     */
    public void uploadFile(byte[] data, String upKey, QiNiuUploadListener listener) {
        //定义数据上传结束后的处理动作
        final UpCompletionHandler upCompletionHandler = (key, info, response) -> {
            if (info.isOK()) {
                if(listener != null)
                    listener.uploadSuccess(host + key);
            }else{
                if(listener != null)
                    listener.uploadFail();
            }
        };
        final UploadOptions uploadOptions = new UploadOptions(null, null, false, (key, percent) -> {
            if(listener != null)
                listener.uploadPercent((float) percent);
        }, () -> false);
        try {
            String token = checkUpToken();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    QiNiuManager.getSingleton().put(data, upKey, token, upCompletionHandler, uploadOptions);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface QiNiuUploadListener{
        void uploadPercent(float percent);
        void uploadSuccess(String url);
        void uploadFail();
    }

}