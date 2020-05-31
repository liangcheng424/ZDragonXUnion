package com.zhulong.eduvideo.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhulong.eduvideo.ApplicationEx;
import com.zhulong.eduvideo.R;
import com.zhulong.eduvideo.entity.PersonHeader;
import com.zhulong.eduvideo.entity.UserInfo;
import com.zhulong.eduvideo.mvp.fragment.BaseFragment;
import com.zhulong.eduvideo.mvp.presenter.BasePresenter;
import com.zhulong.eduvideo.net.ZhulongHttp;
import com.zhulong.eduvideo.net.parameter.HttpCallBack;
import com.zhulong.eduvideo.net.parameter.ParametersGet;
import com.zhulong.eduvideo.net.parameter.ParametersPost;
import com.zhulong.eduvideo.utils.SystemUtils;
import com.zhulong.eduvideo.utils.http.ZLImageLoader;
import com.zhulong.eduvideo.view.RoundImageView;

import org.json.JSONObject;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 授权页面
 */
public class AgreeAuthFragment extends BaseFragment {

    @BindView(R.id.tv_access_login)
    TextView tvAccessLogin;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.riv_user_avatar)
    RoundImageView rivUserAvatar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String mUsername;
    private String mZlid;
    private String mAvatar;
    private String mUid;
    private String mAppId;
    private String mIsVip;

    @Override
    protected void initData() {
        mZlid =   getArguments().getString("zlid");

        mUid = getArguments().getString("uid");
        mUsername = getArguments().getString("username");
        mAvatar = getArguments().getString("avatar");
        mAppId = getArguments().getString("appid");
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        tvTitle.setText("筑龙学社登录");
        if (null != mUsername) {
            tvUsername.setText(mUsername);
        }
        if (!TextUtils.isEmpty(mAvatar)) {
            ZLImageLoader.getInstance().displayImage(getContext(), mAvatar, rivUserAvatar);
        } else {
            rivUserAvatar.setImageResource(R.drawable.avatar);
        }

    }

    @Override
    protected void resumeInit(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_agree_auth;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @OnClick({R.id.tv_access_login, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_access_login:
                //recordAuthor();
                UserInfo userInfo = new UserInfo();
                userInfo.setUid(mUid);
                getHeader(userInfo,true);
                break;
            case R.id.back:
                ApplicationEx.getInstance().exitApp();
                break;
        }
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
                            mUsername = json_header.optString("username");
                            mIsVip = json_header.optString("is_vip");
                            //是否绑定手机号
                            int is_real_mobile = json_header.optInt("is_real_mobile");
                            if (isBack || is_real_mobile == 1) {//已绑定  不需要做其他处理，因为已登录和ZlSession
                                // 未过期 才会进入自动登录 所以请求用户头信息成功之后直接 返回授权结果即可，不需要在判断手机是否验证
                                recordAuthor();
                            }/* else {//未绑定
                                startActivityForResult(
                                        MobileNumCheckFragment.newInstance(AgreeAuthFragment.this, 160),
                                        Constants.LOGIN_FOR_RESULT);
                            }*/
                        }
                    } else {
                        PersonHeader ph = new PersonHeader();
                        String uid = userInfo.getUid();
                        String avatar = SystemUtils.getAvatarByUid(uid);
                        ph.setAvatar(avatar);
                        userInfo.setPersonHeader(ph);
                        if (isBack)
                            recordAuthor();
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
    private void recordAuthor() {
        ParametersGet parametersGet = new ParametersGet(getString(R.string.bbs_openapi) + "user/insertMobilelog");
        parametersGet.addParameter("authAppid", mAppId);
        ZhulongHttp.Get(getContext(), parametersGet, new HttpCallBack<String>() {
            @Override
            public void onFinished() {
                super.onFinished();
                //授权完成之后返回授权信息，记录授权状态给服务端
                Intent intent = new Intent();
                intent.putExtra("uid", mUid);
                intent.putExtra("avatar", mAvatar);
                intent.putExtra("username", mUsername);
                intent.putExtra("isVip",mIsVip);
//                setResult(RESULT_OK, intent);
                Navigation.findNavController(AgreeAuthFragment.this.view).navigateUp();
            }
        });
    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        ApplicationEx.getInstance().exitApp();
//    }

}


