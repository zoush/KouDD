package utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by zoushaohua on 2016/1/4.
 * qq:756350775
 */
public class FileManagerUtils {
    /**
     * 判断是否有存储卡
     */
    public static boolean isSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取存储卡路径
     */
    public static String getSdcardPath() {
        if (isSdcard()) {
            return Environment.getExternalStorageDirectory().getPath() + "/";
        } else {
            return Environment.getDownloadCacheDirectory() + "/";
        }
    }

    /**
     * 获取应用程序本地文件夹路径
     */
    public static String getAppPath(String filePath) {
        String path = getSdcardPath() + filePath;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return path;
    }
}
