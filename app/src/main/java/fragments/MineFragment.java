package fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import activity.AboutUsActivity;
import activity.FavoriteActivity;
import activity.LoginActivity;
import activity.MessageActivity;
import activity.SetActivity;
import activity.ShelfActivity;
import de.greenrobot.event.EventBus;
import utils.FileManagerUtils;
import utils.FilesUtils;
import utils.MD5;
import utils.PreferenceUtil;
import widgh.ActionSheet;
import yd.koudd.BaseFragment;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/20.
 * qq:756350775
 */
public class MineFragment extends BaseFragment implements ActionSheet.ActionSheetListener {
    @ViewInject(R.id.tvName)
    private TextView tvName;
    @ViewInject(R.id.ivLogo)
    private SimpleDraweeView ivLogo;
    @ViewInject(R.id.tvLogin)
    private TextView tvLogin;
    PreferenceUtil _pref;
    private String filePath = "";
    public static final int IMAGES_ACTION_CAMERA = 1;// 拍照
    public static final int IMAGES_ACTION_SELECT_IMAGES = 2;// 选取相册
    private String imagepath = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_mine, null);
        ViewUtils.inject(this, _view);

        _pref = PreferenceUtil.getInstance(getActivity());
        if (_pref.getLoginStatus()) {
            tvLogin.setVisibility(View.GONE);
            ivLogo.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(_pref.getMobile());
            // ivLogo.setImageURI(Uri.parse(Constants.IMAGE_URL + _pref.getAvatar()));
            getUserInfo();
        }
        EventBus.getDefault().register(this);
        return _view;
    }

    @OnClick({R.id.tvLogin, R.id.llAbout, R.id.llSet, R.id.llMessage, R.id.llShelf, R.id.llFavor, R.id.ivLogo})
    private void clickEvent(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tvLogin:
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.llAbout:
                intent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.llSet:
                intent = new Intent(getActivity(), SetActivity.class);
                startActivity(intent);
                break;
            case R.id.llMessage:
                if (_pref.getLoginStatus()) {
                    intent = new Intent(getActivity(), MessageActivity.class);
                    startActivity(intent);
                } else {
                    jumptoLogin();
                }
                break;
            case R.id.llShelf:
                if (_pref.getLoginStatus()) {
                    intent = new Intent(getActivity(), ShelfActivity.class);
                    startActivity(intent);
                } else {
                    jumptoLogin();
                }
                break;
            case R.id.llFavor:
                if (_pref.getLoginStatus()) {
                    intent = new Intent(getActivity(), FavoriteActivity.class);
                    startActivity(intent);
                } else {
                    jumptoLogin();
                }
                break;
            case R.id.ivLogo:
                getActivity().setTheme(R.style.ActionSheetStyleiOS7);
                ActionSheet.createBuilder(getActivity(), getChildFragmentManager())
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles("拍照", "从相册选择")
                        .setCancelableOnTouchOutside(true).setListener(this).show();
                break;
        }
    }

    private void jumptoLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public void onEventMainThread(String msg) {
        if (msg.equals("regist")) {
            tvLogin.setVisibility(View.GONE);
            ivLogo.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(_pref.getMobile());
            getUserInfo();
        } else if (msg.equals("logout")) {
            tvLogin.setVisibility(View.VISIBLE);
            ivLogo.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
        } else if (msg.equals("taobao")) {
            tvLogin.setVisibility(View.GONE);
            ivLogo.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            ivLogo.setImageURI(Uri.parse(_pref.getUrl()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void getUserInfo() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.user.getUserInfo");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.user.getUserInfo&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if (_pref.getUrl().length() != 0) {
                            tvName.setText(_pref.getUserName());
                            ivLogo.setImageURI(Uri.parse(_pref.getUrl()));
                        } else {
                            _pref.setAvatar(jsonObject1.getString("headimgurl"));
                            ivLogo.setImageURI(Uri.parse(Constants.IMAGE_URL + _pref.getAvatar()));
                            tvName.setText(jsonObject1.getString("mobile"));
                        }
                        _pref.setTaoBaoId(jsonObject1.getString("taobao_user_id"));
                        if (jsonObject1.getString("nickname").length() != 0 && !_pref.getTaoBaoLogin())
                            tvName.setText(jsonObject1.getString("nickname"));

                    } else {

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("sa", s);
            }
        });
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index == 0) {
            letCamera();
        } else {
            selectImages();
        }
    }

    protected void letCamera() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String strImgPath = FileManagerUtils.getAppPath("pplx/guide") + "/";
        String fileName = System.currentTimeMillis() + ".jpg";// 照片命名
        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(strImgPath, fileName);
        strImgPath = strImgPath + fileName;// 该照片的绝对路径
        imagepath = strImgPath;
        Uri uri = Uri.fromFile(out);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(imageCaptureIntent, IMAGES_ACTION_CAMERA);
    }

    protected void selectImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGES_ACTION_SELECT_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGES_ACTION_CAMERA:
                try {
                    if (imagepath.length() != 0) {
                        //ivLogo.setImageURI(Uri.parse("file://" + imagepath));
                        uploadImage();
                    } else {
                        ivLogo.setImageURI(Uri.parse(Constants.IMAGE_URL + _pref.getAvatar()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case IMAGES_ACTION_SELECT_IMAGES:
                try {
                    imagepath = FilesUtils.getPath(getActivity(), data.getData());
                    //ivLogo.setImageURI(Uri.parse("file://" + imagepath));
                    uploadImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void uploadImage() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("upfile", new File(imagepath));
        //_params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        //_params.addBodyParameter("api_name", "koudai.user.getUserInfo");
        //_params.addBodyParameter("appid", Constants.APPID);
        // String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.user.getUserInfo&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        //_params.addBodyParameter("token", MD5.MD5(signStr));
        HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, "http://koudai.beyondin.com/api/uploadImage/appid/1/submit/submit", _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    _pref.setAvatar(jsonObject.getString("file_path"));
                    editUserInfo();
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("sa", s);
            }
        });
    }

    private void editUserInfo() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.user.editUserInfo");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("headimgurl", _pref.getAvatar());
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.user.editUserInfo&appid=" + Constants.APPID + "&headimgurl=" + _pref.getAvatar() + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        ivLogo.setImageURI(Uri.parse(Constants.IMAGE_URL + _pref.getAvatar()));
                    } else {

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("sa", s);
            }
        });
    }
}
