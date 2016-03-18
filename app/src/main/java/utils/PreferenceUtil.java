package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {
    private static PreferenceUtil instance;
    private static Context context;

    private PreferenceUtil(Context context) {
        this.context = context;
    }

    public synchronized static PreferenceUtil getInstance(Context context) {
        if (instance == null)
            instance = new PreferenceUtil(context);
        return instance;
    }

    private SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getUserid() {
        return getSharedPreferences().getString("userid", "");
    }

    public boolean setUserid(String userid) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("userid", userid);
        return editor.commit();
    }

    public String getUserName() {
        return getSharedPreferences().getString("username", "");
    }

    public boolean setUserName(String username) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("username", username);
        return editor.commit();
    }

    public String getPassword() {
        return getSharedPreferences().getString("password", "");
    }

    public boolean setPassword(String password) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("password", password);
        return editor.commit();
    }

    public String getMyCircleLocationAddress() {
        return getSharedPreferences().getString("myCircleLocationAddress", "");
    }

    public boolean setMyCircleLocationAddress(String myCircleLocationAddress) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("myCircleLocationAddress", myCircleLocationAddress);
        return editor.commit();
    }

    public String getMyCircleLocationLat() {
        return getSharedPreferences().getString("myCircleLocationLat", "");
    }

    public boolean setMyCircleLocationLat(String myCircleLocationLat) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("myCircleLocationLat", myCircleLocationLat);
        return editor.commit();
    }

    public String getMyCircleLocationLng() {
        return getSharedPreferences().getString("myCircleLocationLng", "");
    }

    public boolean setMyCircleLocationLng(String myCircleLocationLng) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("myCircleLocationLng", myCircleLocationLng);
        return editor.commit();
    }

    public String getCode() {
        return getSharedPreferences().getString("code", "");
    }

    public boolean setCode(String code) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("code", code);
        return editor.commit();
    }


    public String getAirport() {
        return getSharedPreferences().getString("airport", "");
    }

    public boolean setAirport(String airport) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("airport", airport);
        return editor.commit();
    }

    public String getMetar() {
        return getSharedPreferences().getString("metar", "");
    }

    public boolean setMetar(String metar) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("metar", metar);
        return editor.commit();
    }

    public String getTaf() {
        return getSharedPreferences().getString("taf", "");
    }

    public boolean setTaf(String taf) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("taf", taf);
        return editor.commit();
    }

    public String getArea() {
        return getSharedPreferences().getString("area", "");
    }

    public boolean setArea(String area) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("area", area);
        return editor.commit();
    }

    public String getUrl() {
        return getSharedPreferences().getString("url", "");
    }

    public boolean setUrl(String url) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("url", url);
        return editor.commit();
    }

    public boolean getLoginStatus() {
        return getSharedPreferences().getBoolean("login", false);
    }

    public boolean setLoginStatus(boolean loginStatus) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("login", loginStatus);
        return editor.commit();
    }

    public String getId() {
        return getSharedPreferences().getString("id", "");
    }

    public boolean setId(String id) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("id", id);
        return editor.commit();
    }

    public String getMobile() {
        return getSharedPreferences().getString("mobile", "");
    }

    public boolean setMobile(String mobile) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("mobile", mobile);
        return editor.commit();
    }

    public String getType() {
        return getSharedPreferences().getString("type", "");
    }

    public boolean setType(String type) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("type", type);
        return editor.commit();
    }

    public String getGender() {
        return getSharedPreferences().getString("gender", "");
    }

    public boolean setGender(String gender) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("gender", gender);
        return editor.commit();
    }

    public String getBirthday() {
        return getSharedPreferences().getString("birthday", "");
    }

    public boolean setBirthday(String birthday) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("birthday", birthday);
        return editor.commit();
    }

    public String getAvatar() {
        return getSharedPreferences().getString("avatar", "");
    }

    public boolean setAvatar(String avatar) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("avatar", avatar);
        return editor.commit();
    }


    public String getIp() {
        return getSharedPreferences().getString("ip", "");
    }

    public boolean setIp(String ip) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("ip", ip);
        return editor.commit();
    }

    public String getLoginTime() {
        return getSharedPreferences().getString("loginTime", "");
    }

    public boolean setLoginTime(String loginTime) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("loginTime", loginTime);
        return editor.commit();
    }

    public String getIntegration() {
        return getSharedPreferences().getString("integration", "");
    }

    public boolean setIntegration(String integration) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("integration", integration);
        return editor.commit();
    }

    public String getStatus() {
        return getSharedPreferences().getString("status", "");
    }

    public boolean setStatus(String status) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("status", status);
        return editor.commit();
    }

    public String getCreateAt() {
        return getSharedPreferences().getString("createAt", "");
    }

    public boolean setCreateAt(String createAt) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("createAt", createAt);
        return editor.commit();
    }

    public String getUpdateAt() {
        return getSharedPreferences().getString("updateAt", "");
    }

    public boolean setUpdateAt(String updateAt) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("updateAt", updateAt);
        return editor.commit();
    }


    public boolean getHasGallery() {
        return getSharedPreferences().getBoolean("gallery", false);
    }

    public boolean setHasGallery(boolean gallery) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("gallery", gallery);
        return editor.commit();
    }

    public String getCity() {
        return getSharedPreferences().getString("city", "");
    }

    public boolean setCity(String city) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("city", city);
        return editor.commit();
    }

    public String getCover() {
        return getSharedPreferences().getString("cover", "");
    }

    public boolean setCover(String cover) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("cover", cover);
        return editor.commit();
    }

    public String getGalleryName() {
        return getSharedPreferences().getString("galleryName", "");
    }

    public boolean setGalleryName(String galleryName) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("galleryName", galleryName);
        return editor.commit();
    }

    public String getGalleryId() {
        return getSharedPreferences().getString("galleryId", "");
    }

    public boolean setGalleryId(String galleryId) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("galleryId", galleryId);
        return editor.commit();
    }

    public String getFans() {
        return getSharedPreferences().getString("fans", "");
    }

    public boolean setFans(String fans) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("fans", fans);
        return editor.commit();
    }

    public String getFollow() {
        return getSharedPreferences().getString("follow", "");
    }

    public boolean setFollow(String follow) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("follow", follow);
        return editor.commit();
    }

    public boolean getSign() {
        return getSharedPreferences().getBoolean("sign", false);
    }

    public boolean setSign(boolean sign) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("sign", sign);
        return editor.commit();
    }

    public boolean getPush() {
        return getSharedPreferences().getBoolean("push", false);
    }

    public boolean setPush(boolean push) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("push", push);
        return editor.commit();
    }

    public String getLat() {
        return getSharedPreferences().getString("lat", "");
    }

    public boolean setLat(String lat) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("lat", lat);
        return editor.commit();
    }


    public String getSystemMoneyName() {
        return getSharedPreferences().getString("money", "");
    }

    public boolean setSystemMoneyName(String money) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("money", money);
        return editor.commit();
    }

    public String getSessionId() {
        return getSharedPreferences().getString("session", "");
    }

    public boolean setSessionId(String session) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("session", session);
        return editor.commit();
    }


    public boolean getTaoBaoLogin() {
        return getSharedPreferences().getBoolean("login", false);
    }

    public boolean setTaoBaoLogin(boolean login) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("login", login);
        return editor.commit();
    }

    public String getTaoBaoId() {
        return getSharedPreferences().getString("taobao", "");
    }

    public boolean setTaoBaoId(String taobao) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("taobao", taobao);
        return editor.commit();
    }


}
