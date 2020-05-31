package com.zhulong.eduvideo.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhulong.eduvideo.ApplicationEx;
import com.zhulong.eduvideo.R;
import com.zhulong.eduvideo.base.BaseHandler;
import com.zhulong.eduvideo.constant.Constants;
import com.zhulong.eduvideo.entity.PersonHeader;
import com.zhulong.eduvideo.entity.UserInfo;
import com.zhulong.eduvideo.mvp.activity.MainActivity;
import com.zhulong.eduvideo.mvp.activity.MobileNumCheckActivity;
import com.zhulong.eduvideo.mvp.fragment.BaseFragment;
import com.zhulong.eduvideo.mvp.presenter.BasePresenter;
import com.zhulong.eduvideo.net.HttpUrlUtil;
import com.zhulong.eduvideo.net.ZhulongHttp;
import com.zhulong.eduvideo.net.parameter.HttpCallBack;
import com.zhulong.eduvideo.net.parameter.ParametersGet;
import com.zhulong.eduvideo.net.parameter.ParametersPost;
import com.zhulong.eduvideo.utils.DesUtils;
import com.zhulong.eduvideo.utils.RsaUtil;
import com.zhulong.eduvideo.utils.SharePreferencesUtils;
import com.zhulong.eduvideo.utils.StreamUtil;
import com.zhulong.eduvideo.utils.SystemUtils;
import com.zhulong.eduvideo.utils.ToastUtil;

import org.json.JSONObject;

import java.io.InputStream;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * 三方授权登录页面
 */
public class AuthLoginFragment extends BaseFragment implements BaseHandler.BaseHandlerCallBack {

    @BindView(R.id.login_name)
    AutoCompleteTextView loginName;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.check_code)
    EditText checkCode;
    @BindView(R.id.check_view)
    ImageView checkView;
    @BindView(R.id.ll_check)
    LinearLayout llCheck;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private DesUtils mDes;
    private String zlid;

    private String mAvatar;
    private String mUid;
    private String mUserName;
    private String mAppId;
    private String mIsVip;

    boolean mLoginNameEnable = false;
    boolean mLoginPwdEnable = false;
    private MainActivity mainActivity;

    @Override
    protected void initData() {
        try {
            mDes = new DesUtils();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSessionID();

        mAppId = getArguments().getString("appid");

        initEvent();
    }

    private void initEvent() {
        changeLoginBtnStatus();
    }

    /**
     * 改变登录按钮的状态
     */
    private void changeLoginBtnStatus() {
        login.setEnabled(false);
        loginName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLoginNameEnable = s.length() > 0;
                login.setEnabled(mLoginNameEnable && mLoginPwdEnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLoginPwdEnable = s.length() >= 6;
                login.setEnabled(mLoginNameEnable && mLoginPwdEnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth_login;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        tvTitle.setText("筑龙用户登录");
    }

    @Override
    protected void resumeInit(View view, Bundle savedInstanceState) {

    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends BaseHandler<AuthLoginFragment> {

        public MyHandler(AuthLoginFragment authLoginFragment) {
            super(authLoginFragment);
        }
    }

    @Override
    public void handMessageCallBack(android.os.Message msg) {
        switch (msg.what) {
            case 0:
                Bitmap bitmap = (Bitmap) msg.obj;
                if (bitmap != null)
                    checkView.setImageBitmap(bitmap);
                break;
        }
    };

    @Override
    public  void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @OnClick({R.id.back, R.id.login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                ApplicationEx.getInstance().exitApp();
                break;
            case R.id.login:
                String username = loginName.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    showToast("用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    showToast("密码不能为空");
                    return;
                }
                doLogin(username, password);
                break;
        }
    }

    /**
     * 登录
     */
    private void doLogin(String username, final String password) {
        ParametersPost param = new ParametersPost(getString(R.string.passport_openapi) + "user/userLoginNewAuth");
        if (llCheck.isShown()) {
            String code = checkCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                showToast("请输入验证码");
                return;
            } else {
                String sessionId = ApplicationEx.getInstance().getAppConfig().getZlSessionID();
                param.addParameter("ZLSessionID", sessionId);
                param.addParameter("seccode", code);
            }
        } else {
            param.addParameter("ZLSessionID", "");
            param.addParameter("seccode", "");
        }
        param.addParameter("loginName", username);
        param.addParameter("passwd", RsaUtil.encryptByPublic(password));
        param.addParameter("cookieday", "");
        param.addParameter("fromUrl", "android");
        param.addParameter("ignoreMobile", "0");
        showLoadDialog();
        HttpCallBack<String> callBack = new HttpCallBack<String>() {

            @Override
            public void onSuccess(String resultData) {
                super.onSuccess(resultData);
                try {
                    JSONObject object = new JSONObject(resultData);
                    int errorCode = object.optInt("errNo");
                    if (errorCode == 0 || errorCode == 160) {
                        JSONObject result = object.optJSONObject("result");
                        String uid = result.optString("uid");
                        zlid = result.optString("zlid");
                        String is_crop = result.optString("is_crop");
                        String username = result.optString("username");
                        String encryptzlid = mDes.encrypt(zlid);//自动登录
                        SharePreferencesUtils.putString(getContext(), "encryptzlid", encryptzlid);
                        UserInfo loginInfo = new UserInfo();
                        loginInfo.setLogin_name(username);
                        loginInfo.setLogin_password(password);
                        loginInfo.setIs_crop(is_crop);
                        loginInfo.setUid(uid);
                        loginInfo.setZlid(zlid);
                        loginInfo.setUsername(username);
                        ApplicationEx.getInstance().setUserInfo(loginInfo);
                        getHeader(loginInfo, false);
                        hideLoadDialog();
                    } else if (errorCode == 159) {//存在已经绑定手机号码，需要手机号码验证
                        String mobile = object.optString("mobile");
                        String uid = object.optString("msg");
                        String email = object.optString("email");
                        startActivityForResult(
                                MobileNumCheckActivity.newInstance(getContext(), 159, mobile, email, uid),
                                Constants.LOGIN_FOR_RESULT);//TODO 有返回值
//                        Bundle bundle = MobileNumCheckFragment.newInstance(getContext(), 159, mobile, email, uid);
//                        bundle.putInt("result", Constants.LOGIN_FOR_RESULT);
//                        Navigation.findNavController(AuthLoginFragment.this.view).navigate(R.id.action_authLoginFragment_to_mobileNumCheckFragment,bundle);

                    } else if (errorCode == 113 || errorCode == 161) {
                        String msg = object.optString("msg");
                        if (msg != null && !msg.equals("")) {
                            showToast(msg);
                        }
                        hideLoadDialog();
                    } else {
                        hideLoadDialog();
                        if (errorCode == 119) {
                            String msg = object.optString("msg");
                            if (msg != null && !msg.equals("")) {
                                showToast(msg);
                            }
                        }
                        if (object.has("debug")) {
                            int allowNum = object.optJSONObject("debug")
                                    .optInt("allownum");
                            int num = object.optJSONObject("debug").optInt(
                                    "num");
                            if (num >= allowNum || errorCode == 134
                                    || errorCode == 135) {
                                llCheck.setVisibility(View.VISIBLE);
                                new Thread(authCodeRunnable).start();
                                checkCode.setText("");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoadDialog();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                showToast(getString(R.string.net_error));
                hideLoadDialog();
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        };
        ZhulongHttp.Post(getContext(), "userLoginNewAuth", getString(R.string.secrectKey_passport),
                param, callBack);
    }

    /**
     * 获取用户头信息
     *
     * @param userInfo 用户数据
     * @param isBack   是否是验证返回时调用的
     *                 用于手机号验证，邮箱验证，手机号登录验证。
     */
    private void getHeader(final UserInfo userInfo, final boolean isBack) {
        ParametersPost parameters = new ParametersPost(getString(R.string.passport_api) +
                "getUserHeaderForMobile");
        parameters.addParameter("zuid", userInfo.getUid());
        parameters.addParameter("uid", userInfo.getUid());
        final HttpCallBack<String> callBack = new HttpCallBack<String>() {


            @Override
            public void onSuccess(String resultData) {
                super.onSuccess(resultData);
                try {
                    JSONObject jsonObj = new JSONObject(
                            resultData);
                    String errNo = jsonObj.getString("errNo");
                    if ("0".equals(errNo)) {
                        JSONObject json_header = jsonObj
                                .getJSONObject("result");
                        if (null != json_header) {
                            mUid = json_header.optString("uid");
                            mAvatar = json_header.optString("avatar");
                            mUserName = json_header.optString("username");
                            mIsVip = json_header.optString("is_vip");
                            //是否绑定手机号
                            int is_real_mobile = json_header.optInt("is_real_mobile");
                            if (isBack || is_real_mobile == 1) {//已绑定
                                recordAuthor(mUid, mAvatar, mUserName,mIsVip);
                            } else {//未绑定
                                startActivityForResult(//TODO 有返回值
                                        MobileNumCheckActivity.newInstance(getContext(), 160),
                                        Constants.LOGIN_FOR_RESULT);
//                                Bundle bundle = MobileNumCheckFragment.newInstance(getContext(), 160);
//                                bundle.putInt("result",Constants.LOGIN_FOR_RESULT);
//                                Navigation.findNavController(AuthLoginFragment.this.view).navigate(R.id.action_authLoginFragment_to_mobileNumCheckFragment,bundle);
                            }
                        }
                    } else {
                        PersonHeader ph = new PersonHeader();
                        String uid = userInfo.getUid();
                        String avatar = SystemUtils.getAvatarByUid(uid);
                        ph.setAvatar(avatar);
                        userInfo.setPersonHeader(ph);
                        if (isBack)
                            recordAuthor(uid, avatar, mUserName,mIsVip);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                showToast(getString(R.string.net_error));
            }

            @Override
            public void onFinished() {
                super.onFinished();
                hideLoadDialog();
            }
        };
        ZhulongHttp.Post(getContext(), "getUserHeaderForMobile", getString(R.string.secrectKey_passport),
                parameters, callBack);
    }

    /**
     * 记录应用授权
     */
    private void recordAuthor(final String uid, final String avatar, final String usename,final String isVip) {
        ParametersGet parametersGet = new ParametersGet(getString(R.string.bbs_openapi) + "user/insertMobilelog");
        parametersGet.addParameter("authAppid", mAppId);
        ZhulongHttp.Get(getContext(), parametersGet, new HttpCallBack<String>() {
            @Override
            public void onFinished() {
                super.onFinished();
                //授权完成之后返回授权信息，记录授权状态给服务端
                Intent intent = new Intent();
                intent.putExtra("uid", uid);
                intent.putExtra("avatar", avatar);
                intent.putExtra("username", usename);
                intent.putExtra("isVip",isVip);
//                setResult(RESULT_OK, intent);
                Navigation.findNavController(AuthLoginFragment.this.view).navigateUp();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        ApplicationEx.getInstance().exitApp();
//    }


    @Override
    public void onResume() {
        super.onResume();
        if (!mainActivity.resultBundle.isEmpty()){
            int result = mainActivity.resultBundle.getInt("result2");
            if (result == Constants.LOGIN_FOR_RESULT){
                try {
                        String code = mainActivity.resultBundle.getString("code");
                        String zlid = ApplicationEx.getInstance().getUserInfo().getZlid();
                        //手机号绑定
                        if (TextUtils.equals(code, Constants.MOBILE_NOT_READY)) {
                            //手机绑定成功之后设置数据返回
                            saveZlid(zlid);
                            recordAuthor(mUid, mAvatar, mUserName,mIsVip);
                        } else {//验证手机号、邮箱，手机验证登陆方式回调。
                            onResultCall(mainActivity.resultBundle);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoadDialog();
                        showToast(ToastUtil.FAIL, "操作失败，请重试。");
                    }
            }else{//验证失败
            }
            mainActivity.resultBundle.clear();


        }

    }

    /**
     * 保存Zlid,防止从三方进入学社输入用户名和密码授权成功之后再次调用需要重新输入用户名和密码问题（三方授权需要按断是否有zlid和过期问题处理）
     */
    private void saveZlid(String zlid) {
        try {
            DesUtils desUtils = new DesUtils();
            String decrypt = desUtils.encrypt(zlid);
            SharePreferencesUtils.putString(getContext(), "encryptzlid", decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证手机号、邮箱，手机验证登陆方式回调。
     *
     * @param data 返回信息
     * @throws Exception 抛出异常
     */
    private void onResultCall(Bundle data) throws Exception {
        showLoadDialog();
        String message = data.getString("message");
        String resultData = data.getString("result");
        JSONObject object = new JSONObject(resultData);

        JSONObject result = object.optJSONObject("result");
        String uid = result.optString("uid");
        String zlid = result.optString("zlid");
        String is_crop = result.optString("is_crop");
        String username = result.optString("username");//用户名
        String encryptzlid = mDes.encrypt(zlid);
        SharePreferencesUtils.putString(getContext(), "encryptzlid", encryptzlid);
        SharePreferencesUtils.putBoolean(getContext(), "isFirstIn", false);

        UserInfo loginInfo = new UserInfo();
        loginInfo.setLogin_name(message);
        loginInfo.setIs_crop(is_crop);
        loginInfo.setUid(uid);
        loginInfo.setZlid(zlid);
        loginInfo.setUsername(username);
        ZhulongHttp.addCookie(zlid);
        ZhulongHttp.saveWebViewCookie();
        getHeader(loginInfo, true);
    }

    /**
     * 异步加载验证码图片
     */
    private Runnable authCodeRunnable = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            try {
                while (TextUtils.isEmpty(ApplicationEx.getInstance().getAppConfig().getZlSessionID())) {
                    ApplicationEx.getInstance().getAppConfig().getZlSessionID();
                    Thread.sleep(100);
                }
                String passport = getString(R.string.passport);
                InputStream is = StreamUtil
                        .URL(HttpUrlUtil.getInstance().adapt(passport) + "seccode/index?ZLSessionID=" + ApplicationEx.getInstance().getAppConfig().getZlSessionID());
                Bitmap bitmap = BitmapFactory.decodeStream(is);//请求接口获取到验证码图片
                Message message = new Message();
                message.obj = bitmap;
                message.what = 0;
                mHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    };

    /**
     * 初始化获取验证码图片SessionID
     */
    public void getSessionID() {
        ParametersGet param = new ParametersGet(getString(R.string.passport_openapi) +
                "user/getsessionid");
        param.addParameter("startsession", 1);
        HttpCallBack<String> callBack = new HttpCallBack<String>() {
            @Override
            public void onSuccess(String resultData) {
                super.onSuccess(resultData);
                try {
                    JSONObject object = new JSONObject(resultData);
                    int errorCode = object.optInt("errNo");
                    if (errorCode == 0) {
                        String sessionID = object.optString("result");
                        ApplicationEx.getInstance().getAppConfig().setZlSessionID(sessionID);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        };
        ZhulongHttp.Get(getContext(), param, callBack);
    }
}
