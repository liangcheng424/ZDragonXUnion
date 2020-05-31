package com.zhulong.eduvideo.auth;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.zhulong.eduvideo.R;
import com.zhulong.eduvideo.entity.UserInfo;
import com.zhulong.eduvideo.manager.UserLocalManager;
import com.zhulong.eduvideo.mvp.activity.BaseActivity;
import com.zhulong.eduvideo.mvp.presenter.BasePresenter;
import com.zhulong.eduvideo.net.ZhulongHttp;
import com.zhulong.eduvideo.net.parameter.HttpCallBack;
import com.zhulong.eduvideo.net.parameter.ParametersPost;
import com.zhulong.eduvideo.utils.CommonUtil;
import com.zhulong.eduvideo.utils.DesUtils;
import com.zhulong.eduvideo.utils.GsonUtil;
import com.zhulong.eduvideo.utils.SharePreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 授权时调用的过渡页面
 * 授权信息检查cookie信息检查页面，判断是否已经登录，并且zlid（cookie）是否过期
 */
public class AuthorActivity extends BaseActivity {
    private String zlid;
    private static final String AUTHOR_INFO = "author_info";//返回json的key
    private String appId;

    @Override
    protected void initData() {
        boolean agree = authorVerification();
        if (agree) {
            autoLogin(appId);
        } else {
            AuthorInfo authorInfo = new AuthorInfo();
            authorInfo.setErrNo(2);//三方授权返回给调用者的错误码，2表示appid不正确，或者包名不正确
            authorInfo.setMsg("appid不正确");
            Intent intent = new Intent();
            intent.putExtra(AUTHOR_INFO, authorInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * app登录授权校验
     */
    private boolean authorVerification() {
        Intent intent = getIntent();
        appId = intent.getStringExtra("appid");
        String thirdPackageName = intent.getStringExtra("packageName");
        if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(thirdPackageName)) {
            return CommonUtil.checkAppid(thirdPackageName, appId);
        } else {
            return false;
        }
    }

    @Override
    protected void initView() {
        Toast.makeText(this, "正在调起应用授权", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_author;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    /**
     * 去登录
     */
    private void goLogin(String thirdAppId) {
        Intent intent = new Intent(this, AuthLoginActivity.class);
        intent.putExtra("appid",thirdAppId);
        startActivityForResult(intent, 101);
    }

    /**
     * 去授权页面
     * zlid 就是cookie
     */
    private void goAgreeAuth(String zlid, String uid, String avarar, String username,String thirdAppId) {
        Intent intent = new Intent(this, AgreeAuthActivity.class);
        intent.putExtra("zlid", zlid);
        intent.putExtra("uid", uid);
        intent.putExtra("username", username);
        intent.putExtra("avatar", avarar);
        intent.putExtra("appid",thirdAppId);
        startActivityForResult(intent, 100);
    }

    /**
     * 自动登录
     */
    public void autoLogin(final String thirdAppId) {
        String temp = SharePreferencesUtils.getString(this, "encryptzlid", null);
        if (temp != null) {
            DesUtils des = null;
            try {
                des = new DesUtils();
                zlid = des.decrypt(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (zlid == null) {
            goLogin(thirdAppId);
            return;
        }
        ParametersPost parameters = new ParametersPost(this.getString(R.string.passport_openapi_user) + "getUserInfoZlid");
        parameters.addParameter("zlid", zlid);
        ZhulongHttp.Post(this, "getUserInfoZlid", this.getString(R.string.secrectKey_passport), parameters, new HttpCallBack<String>() {
            @Override
            public void onSuccess(String resultData) {
                super.onSuccess(resultData);
                try {
                    JSONObject object = new JSONObject(resultData);
                    int errorCode = object.optInt("errNo");
                    if (errorCode == 0) {
                        JSONObject result = object.optJSONObject("result");
                        String username = result.optString("username");
                        String uid = result.optString("uid");
                        String avatar = result.optString("avatar");
                        goAgreeAuth(zlid, uid, avatar, username,thirdAppId);
                    } else {
                        showToast("授权过期请重新登录");
                        goLogin(thirdAppId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                showToast(getString(R.string.net_error));
                finish();
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100://授权页面返回
                case 101://登录页面返回
                    String uid = data.getStringExtra("uid");
                    String avatar = data.getStringExtra("avatar");
                    String username = data.getStringExtra("username");
                    String isVip = data.getStringExtra("isVip");
                    String info = authorInfoToJson(uid, avatar, username,isVip);
                    Intent intent = new Intent();
                    intent.putExtra(AUTHOR_INFO, info);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }

    /**
     * 拼接json字符串
     *
     * @param uid    用户id
     * @param avatar 用户头像链接
     * @return
     */
    private String authorInfoToJson(String uid, String avatar, String username,String isVip) {
        AuthorInfo authorInfo = new AuthorInfo();
        if (uid != null) {
            authorInfo.setErrNo(0);//获取用户信息成功
            authorInfo.setMsg("获取用户信息成功");
        } else {
            authorInfo.setErrNo(1);//获取用户信息失败
            authorInfo.setMsg("获取用户信息失败");
        }
        AuthorInfo.ResultBean resultBean = new AuthorInfo.ResultBean();
        resultBean.setUid(uid);
        resultBean.setUsername(username);
        resultBean.setAvatar(avatar);
        if ((isVip != null && isVip.equals("1")) || isEVip()) {
            resultBean.setIs_vip("1");
        }else{
            resultBean.setIs_vip(isVip);
        }
        authorInfo.setResult(resultBean);
        String info = GsonUtil.GsonString(authorInfo);
        return info;
    }

    private boolean isEVip() {  //是E会员
        UserInfo user = UserLocalManager.getUser();
        if (user != null && user.getPersonHeader() != null && user.getPersonHeader().getVip_level().equals("4"))
            return true;
        return false;
    }
}
