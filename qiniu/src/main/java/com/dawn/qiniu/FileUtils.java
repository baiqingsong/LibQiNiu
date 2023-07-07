package com.dawn.qiniu;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;

class FileUtils {

    public static byte[] readFile(String filePath) {
        if(filePath == null || !new File(filePath).exists()) return null;
        return readFile(new File(filePath));
    }
    /**
     * 读取文件，文件转换成字节数组
     */
    public static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    /**
     * 关闭流
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
