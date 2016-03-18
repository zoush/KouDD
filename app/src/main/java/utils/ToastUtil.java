package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by d on 2015/6/8.
 */
public class ToastUtil {
    private static Toast mToast;

    public static void showToast(final Context context, final String msg) {
        if (context != null && msg != null && !"".equals(msg)) {
            showMyToast(context, msg);
        }
    }


    public static void showToast(final Context context, final int resId) {
        if (context != null && resId > 0) {
            showMyToast(context, context.getResources().getString(resId));
        }
    }


    public static void showDebugToast(final Context context, final String msg) {
        if (true) {
            showToast(context, msg);
        }
    }


    public static void showDebugToast(final Context context, final int resId) {
        if (true && context != null && resId > 0) {
            showToast(context, resId);
        }
    }

    public static void showMyToast(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
