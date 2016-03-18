package yd.koudd;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.WindowManager;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.socialize.PlatformConfig;

import java.util.LinkedList;
import java.util.List;

import im.fir.sdk.FIR;

/**
 * Created by zoushaohua on 2016/1/20.
 * qq:756350775
 */
public class MyApplication extends Application implements AMapLocationListener {
    public static int screenWidth;
    public static List<Activity> activityList = new LinkedList<Activity>();
    public static List<Activity> registList = new LinkedList<Activity>();

    private static AMapLocation sAMapLocation;
    private LocationManagerProxy aMapLocManager = null;
    public String sArea = "";
    public static double mLat = 0.0, mLng = 0.0;
    private String sCity = "";

    public static int notice_num = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
        Fresco.initialize(this);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();

        aMapLocManager = LocationManagerProxy.getInstance(this);
        aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);

        AlibabaSDK.asyncInit(this, new InitResultCallback() {
            @Override
            public void onSuccess() {
               // Toast.makeText(getApplicationContext(), "初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String message) {
                //Toast.makeText(getApplicationContext(), "初始化异常"+message, Toast.LENGTH_SHORT).show();
            }
        });

        initUMentShowConfig();
    }

    private void initUMentShowConfig() {
        PlatformConfig.setWeixin("wx85b7000ca421482f", "34bdd3d18fe2d810d4a536295de38048");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("1105181812", "73XiXpKO0BnHyBxd");
        // QQ和Qzone appid appkey
    }
    // =======================地图定位============================

    private void stopLocation() {
        if (aMapLocManager != null) {
            aMapLocManager.removeUpdates(this);
            aMapLocManager.destory();
        }
        aMapLocManager = null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            sArea = location.getProvince();
            sCity = location.getCity();
            sAMapLocation = location;
            mLat = location.getLatitude();
            mLng = location.getLongitude();
            stopLocation();
        }
    }
}
