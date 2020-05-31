package com.zhulong.eduvideo.ccvideo.cclive;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveListener;
import com.bokecc.sdk.mobile.live.DWLivePlayer;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.pojo.Answer;
import com.bokecc.sdk.mobile.live.pojo.ChatMessage;
import com.bokecc.sdk.mobile.live.pojo.PrivateChatInfo;
import com.bokecc.sdk.mobile.live.pojo.QualityInfo;
import com.bokecc.sdk.mobile.live.pojo.Question;
import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;
import com.bokecc.sdk.mobile.live.widget.DocView;
import com.orhanobut.logger.Logger;
import com.zhulong.eduvideo.R;
import com.zhulong.eduvideo.mvp.activity.MainActivity;
import com.zhulong.eduvideo.mvp.fragment.HomepageFragment;
import com.zhulong.eduvideo.mvp.fragment.parent.MediaPlayFragment;
import com.zhulong.eduvideo.view.slidinguppanel.SlidingUpPanelLayout;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.navigation.Navigation;
import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * Created by whb on 2017/7/10.
 * Email 18720982457@163.com
 */

public class CCLiveVideoView extends RelativeLayout implements TextureView.SurfaceTextureListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnVideoSizeChangedListener {

    public TextureView surfaceView;
    public RelativeLayout mSurface_container;
    RelativeLayout rlLiveInfosLayout;
    public TextView tip;
    public LinearLayout mLl_auditions_over;

    private View mRoot;
    private DWLivePlayer player;
    private DWLive dwLive = DWLive.getInstance();

    private WindowManager wm;

    private Fragment fragment;

    private OnCCLiveViewListener onCCLiveViewListener;

    private View view;

    private DocView docView;
    private SurfaceTexture surfaceTexture;
    private MainActivity mainActivity;

    public CCLiveVideoView(Context context) {
        super(context);
    }

    public CCLiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CCLiveVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 创建实体类
     */
    public static class Builder {
        CCLiveVideoView ccLiveVideoView;
        Context context;

        OnCCLiveViewListener setOnCCLiveViewListener;
        private Fragment fragment;

        public Builder() {
        }

        public Builder with(Context context) {
            this.context = context;
            return this;
        }

        public Builder setFragment(Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public Builder setOnCCLiveViewListener(OnCCLiveViewListener onCCViewlisten) {
            this.setOnCCLiveViewListener = onCCViewlisten;
            return this;
        }

        public CCLiveVideoView onCreate() {
            ccLiveVideoView = new CCLiveVideoView(context);
            ccLiveVideoView.setFragment(fragment);
            ccLiveVideoView.setOnCCLiveViewListener(setOnCCLiveViewListener);
            ccLiveVideoView.init();
            return ccLiveVideoView;
        }
    }

    public interface OnCCLiveViewListener {
        void onOrientationLoadSuccess(int orientation);

        void hideLoading(boolean b);
    }

    public void init() {
        mainActivity = (MainActivity) fragment.getActivity();
        initData();
        initView();
        initPlayer();
        initEvent();
        initSlidingUpPanel();
    }

    private void initSlidingUpPanel() {

        if (mainActivity != null) {
            mainActivity.mRlVideoStatus.setVisibility(View.GONE);
            mainActivity.mRlClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainActivity.mSup_main != null) {
                        if (mainActivity.mSup_main.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                            mainActivity.mSup_main.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            mainActivity.mIvPlay.setVisibility(View.INVISIBLE);
                            mainActivity.mIvPause.setVisibility(View.VISIBLE);
                            mainActivity.closeBottom = true;
                            mainActivity.isFinish = true;
                            HomepageFragment.newInstance().llHomeBottom.setVisibility(View.GONE);
                            player.pause();
                            player.stop();


                        } else {
                            mainActivity.mSup_main.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    }
                }
            });
        }
    }


    public void initData() {
        wm = (WindowManager) fragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
        mRoot = fragment.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_cc_live_video_view, null);
        surfaceView = new TextureView(getContext());
        mSurface_container = (RelativeLayout) view.findViewById(R.id.surface_container);
        mSurface_container.addView(surfaceView);
        mLl_auditions_over = view.findViewById(R.id.ll_auditions_over);
        tip = (TextView) view.findViewById(R.id.tv_tip);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(lp);
        view.setBackgroundColor(Color.YELLOW);
        this.addView(view);

    }

    public void initPlayer() {
        surfaceView.setSurfaceTextureListener(this);
        player = new DWLivePlayer(getContext());
        player.setOnPreparedListener(this);
        player.setOnInfoListener(this);
        player.setOnVideoSizeChangedListener(this);
        dwLive.setDWLivePlayParams(myDWLiveListener, getContext(), docView, player);
    }

    public void initEvent() {
    }


    public void onPause() {
        try {
            if (player != null && player.isPlaying()) {
                player.pause();
            }
            if (dwLive != null) {
                dwLive.stop();
            }
        }catch (Exception e){

        }

    }

    public void onResume() {
        isOnResumeStart = false;
        if (surface != null) {
            dwLive.start(surface);
            isOnResumeStart = true;
        }
    }

    public void onDestroy() {
        if (player != null) {
            player.pause();
            player.stop();
            player.release();
        }
    }

    Surface surface;
    boolean isOnResumeStart = false;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        System.out.println("----onSurfaceTextureAvailable");
        if (this.surfaceTexture != null) {
            surfaceView.setSurfaceTexture(this.surfaceTexture);
        } else {
            surface = new Surface(surfaceTexture);
            if (isOnResumeStart) {
                return;
            }
            dwLive.start(surface);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        System.out.println("----onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        System.out.println("----onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        System.out.println("----onSurfaceTextureUpdated");
        this.surfaceTexture = surface;
    }

    /**
     * 暂停播放
     */
    public void stopPlay() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    /**
     * 重新开始播放
     */
    public void reStartPlay() {
        if (surface != null) {
            dwLive.start(surface);
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int arg1, int i1) {
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        setScreenorientation();
        player.start();
    }

    /**
     * 设置屏幕方向
     */
    public void setScreenorientation() {
        int width = player.getVideoWidth();
        int height = player.getVideoHeight();
        int orientation = width > height ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (onCCLiveViewListener != null)
            onCCLiveViewListener.onOrientationLoadSuccess(orientation);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        if (width == 0 || height == 0) {
            return;
        }
        sizeChange();
    }

    private boolean isfull = false;

    /**
     * 全屏
     */
    public void sizeChange() {
        setScreenorientation();
        LayoutParams params = getVideoSizeParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(params);
        if (!isPortrait() && isfull)
            this.getLayoutParams().height = LayoutParams.MATCH_PARENT;
        else
            this.getLayoutParams().height = params.height;
    }

    /**
     * 获取直播分辨率的宽度
     */
    public int getPlayerWidth() {
        if (player != null) {
            return player.getVideoWidth();
        }
        return 0;
    }

    /**
     * 获取直播分辨率的高度
     */
    public int getPlayerHeight() {
        if (player != null) {
            return player.getVideoHeight();
        }
        return 0;
    }

    /**
     * 直播是否在播放中
     *
     * @return true 直播中；false 没有直播
     */
    public boolean isPlaying() {
        if (dwLive != null && dwLive.getPlayStatus() != null) {
            return (dwLive.getPlayStatus() == DWLive.PlayStatus.PLAYING);
        }
        return false;
    }

    /**
     * 设置全屏
     *
     * @param isfull
     */
    public void setIsfull(boolean isfull) {
        this.isfull = isfull;
        sizeChange();
    }

    /**
     * 这是这两个参数是为了防止，直播结束，切换屏幕大小的时候出现的大小不一致的问题
     */
    private int oldvWidth;
    private int oldvHeight;

    // 视频等比缩放
    private LayoutParams getVideoSizeParams() {
        int width = wm.getDefaultDisplay().getWidth();
        int height = 0;
        if (isPortrait() && !isfull) {
            height = width * 9 / 16;
        } else {
            height = wm.getDefaultDisplay().getHeight();
        }
        int vWidth = player.getVideoWidth();
        int vHeight = player.getVideoHeight();
        if (vWidth == 0) {
            if (oldvWidth != 0)
                vWidth = oldvWidth;
            else
                vWidth = width;
        } else {
            oldvWidth = vWidth;
        }
        if (vHeight == 0) {
            if (oldvHeight != 0)
                vWidth = oldvHeight;
            else
                vHeight = height;
        } else {
            oldvHeight = vHeight;
        }

        if (vWidth > width || vHeight > height) {
            float wRatio = (float) vWidth / (float) width;
            float hRatio = (float) vHeight / (float) height;
            float ratio = Math.max(wRatio, hRatio);

            width = (int) Math.ceil((float) vWidth / ratio);
            height = (int) Math.ceil((float) vHeight / ratio);
        } else {
            float wRatio = (float) width / (float) vWidth;
            float hRatio = (float) height / (float) vHeight;
            float ratio = Math.min(wRatio, hRatio);

            width = (int) Math.ceil((float) vWidth * ratio);
            height = (int) Math.ceil((float) vHeight * ratio);
        }

        LayoutParams params = new LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }

    /**
     * 是否是竖屏
     *
     * @return
     */
    private boolean isPortrait() {
        if (fragment.isVisible()){
            int mOrientation = fragment.getResources().getConfiguration().orientation;
            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false;
            } else {
                return true;
            }
        }else{
            return true;
        }

    }

    private String getTips() {
        String tips = "";
        try {
            int isLiving = ((MediaPlayFragment) fragment).isLiving();
            if (isLiving == 0) {//未开始
                tips = "直播还没开始，敬请期待";
            } else if (isLiving == 1) {//直播中
                tips = "直播还没开始，敬请期待";
            } else if (isLiving == -1) {//已结束
                tips = "直播已经结束";
            }
        } catch (Exception e) {
            e.printStackTrace();
            tips = "直播还没开始，敬请期待";
        }
        return tips;
    }

    private DWLiveListener myDWLiveListener = new DWLiveListener() {
        @Override
        public void onQuestion(final Question question) {
        }

        @Override
        public void onPublishQuestion(String s) {

        }


        @Override
        public void onAnswer(final Answer answer) {
        }

        @Override
        public void onLiveStatus(final DWLive.PlayStatus playStatus) {
            if (mainActivity != null) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (playStatus) {
                            case PLAYING://
                                onCCLiveViewListener.hideLoading(true);
                                tip.setVisibility(VISIBLE);
                                Logger.e("直播开始");
                                tip.setText("");
                                if (surfaceView.getVisibility() == INVISIBLE)
                                    surfaceView.setVisibility(VISIBLE);
                                break;
                            case PREPARING:
                                onCCLiveViewListener.hideLoading(true);
                                tip.setVisibility(VISIBLE);
                                Logger.e("直播尚未开始");
                                tip.setText(getTips());
                                break;
                            default:
                                onCCLiveViewListener.hideLoading(true);
                                tip.setVisibility(VISIBLE);
                                tip.setText("等待中");
                                break;
                        }
                    }
                });
            }
        }

        @Override
        public void onStreamEnd(final boolean b) {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (player.isPlaying()) player.pause();
                    if (b) tip.setText("直播结束");
                    surfaceView.setVisibility(INVISIBLE);
                }
            });
        }

        @Override
        public void onHistoryChatMessage(ArrayList<ChatMessage> arrayList) {

        }


        @Override
        public void onPublicChatMessage(final ChatMessage chatMessage) {
        }

        @Override
        public void onSilenceUserChatMessage(final ChatMessage chatMessage) {
        }

        @Override
        public void onPrivateQuestionChatMessage(ChatMessage chatMessage) {
        }

        @Override
        public void onPrivateAnswerChatMessage(ChatMessage chatMessage) {
        }

        @Override
        public void onPrivateChat(final PrivateChatInfo info) {
        }

        @Override
        public void onPrivateChatSelf(final PrivateChatInfo info) {
        }

        @Override
        public void onUserCountMessage(final int i) {
        }

        @Override
        public void onPageChange(String s, String s1, int i, int i1) {

        }

        @Override
        public void onNotification(String s) {
            Log.e("onNotification", s);
        }

        @Override
        public void onBroadcastMsg(String s) {

        }


        @Override
        public void onInformation(final String s) {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onException(DWLiveException e) {

        }

        @Override
        public void onInitFinished(final int i, final List<QualityInfo> list) {

        }

        @Override
        public void onKickOut() {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "您已被踢出直播间", Toast.LENGTH_SHORT).show();
//                    fragment.getActivity().finish();
                    Navigation.findNavController(fragment.getView()).navigateUp();
                }
            });
        }

        @Override
        public void onLivePlayedTime(int i) {
        }

        @Override
        public void onLivePlayedTimeException(Exception e) {
        }

        @Override
        public void isPlayedBack(boolean b) {
        }

        @Override
        public void onStatisticsParams(Map<String, String> map) {
        }

        @Override
        public void onCustomMessage(String s) {
        }

        @Override
        public void onBanStream(String reason) {
        }

        @Override
        public void onUnbanStream() {
        }

        @Override
        public void onAnnouncement(final boolean isRemove, final String announcement) {
        }

        @Override
        public void onRollCall(final int duration) {
        }

        boolean isLotteryWin = false;
        String lotteryId = "";

        @Override
        public void onStartLottery(String lotteryId) {
            isLotteryWin = false;
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

        }

        @Override
        public void onLotteryResult(final boolean isWin, final String lotteryCode, final String lotteryId, final String winnerName) {
            this.isLotteryWin = isWin;
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onStopLottery(String lotteryId) {
            if (!isLotteryWin) {
            }
        }

        boolean isVoteResultShow = false;

        @Override
        public void onVoteStart(final int voteCount, final int VoteType) {
            isVoteResultShow = false;
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onVoteStop() {
        }

        @Override
        public void onVoteResult(final JSONObject jsonObject) {
            isVoteResultShow = true;
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onQuestionnairePublish(QuestionnaireInfo questionnaireInfo) {

        }

        @Override
        public void onQuestionnaireStop(String s) {

        }

        @Override
        public void onExeternalQuestionnairePublish(String s, String s1) {

        }


    };

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setOnCCLiveViewListener(OnCCLiveViewListener onCCLiveViewListener) {
        this.onCCLiveViewListener = onCCLiveViewListener;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sizeChange();
    }
}
