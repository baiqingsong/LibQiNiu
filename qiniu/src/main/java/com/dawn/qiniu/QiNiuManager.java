package com.dawn.qiniu;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

/**
 * 初始化七牛管理器
 */
public class QiNiuManager {
    private static UploadManager instance;

    //私有构造
    private QiNiuManager() {
    }

    //获取UploadManager
    public static UploadManager getSingleton() {
        return getSingleton(CustomZone.ZONE_HUA_DONG);
    }

    public enum CustomZone {
        ZONE_HUA_DONG,//华东
        ZONE_HUA_BEI,//华北
        ZONE_HUA_NAN,//华南
    }

    /**
     * 获取UploadManager
     * @param customZone 上传区域
     */
    public static UploadManager getSingleton(CustomZone customZone) {
        Zone zone = FixedZone.zone0;
        switch (customZone) {
            case ZONE_HUA_DONG:
                zone = FixedZone.zone0;
                break;
            case ZONE_HUA_BEI:
                zone = FixedZone.zone1;
                break;
            case ZONE_HUA_NAN:
                zone = FixedZone.zone2;
                break;
        }
        if (instance == null) {
            synchronized (QiNiuManager.class) {
                if (instance == null) {
                    instance = new UploadManager(new Configuration.Builder()
                            .connectTimeout(10)              // 链接超时。默认90秒
                            .useHttps(false)                  // 是否使用https上传域名
                            .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
                            .concurrentTaskCount(3)          // 并发上传线程数量为3
                            .responseTimeout(10)             // 服务器响应超时。默认90秒
                            .zone(zone) // 设置区域为华东
                            .build());
                }
            }
        }
        return instance;
    }
}
