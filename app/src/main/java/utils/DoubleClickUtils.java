package utils;

/**
 * Created by zoushaohua on 2016/1/22.
 * qq:756350775
 */
public class DoubleClickUtils {
    private static long mLastClickTime;


    public static boolean isFastClick() {
        // 当前时间
        long currentTime = System.currentTimeMillis();
// 两次点击的时间差
        long time = currentTime - mLastClickTime;
        if (0 < time && time < 1000) {
            return false;
        }


        mLastClickTime = currentTime;
        return true;
    }

}
