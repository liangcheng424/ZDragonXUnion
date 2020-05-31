package com.zhulong.eduvideo.ccvideo.play;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.bokecc.sdk.mobile.exception.DreamwinException;
import com.bokecc.sdk.mobile.play.DWIjkMediaPlayer;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.bokecc.sdk.mobile.play.OnDreamWinErrorListener;
import com.bokecc.sdk.mobile.util.HttpUtil;
import com.flyco.roundview.RoundTextView;
import com.orhanobut.logger.Logger;
import com.zhulong.eduvideo.ApplicationEx;
import com.zhulong.eduvideo.R;
import com.zhulong.eduvideo.db.DataSet;
import com.zhulong.eduvideo.entity.LessonPartEntity;
import com.zhulong.eduvideo.entity.VideoParams;
import com.zhulong.eduvideo.mvp.activity.LocalCCUrlPlayActivity;
import com.zhulong.eduvideo.mvp.activity.LoginActivity;
import com.zhulong.eduvideo.mvp.activity.MainActivity;
import com.zhulong.eduvideo.mvp.fragment.HomepageFragment;
import com.zhulong.eduvideo.mvp.fragment.parent.MediaPlayFragment;
import com.zhulong.eduvideo.service.IMediaPlayActionListener;
import com.zhulong.eduvideo.utils.ParamsUtil;
import com.zhulong.eduvideo.utils.ScreenBrightnessManager;
import com.zhulong.eduvideo.utils.ScreenUtils;
import com.zhulong.eduvideo.utils.SharePreferencesUtils;
import com.zhulong.eduvideo.utils.TimerUtils;
import com.zhulong.eduvideo.utils.ToastUtil;
import com.zhulong.eduvideo.utils.statistics.BuriedPointStatistics;
import com.zhulong.eduvideo.view.CCVideoViewRightMenu;
import com.zhulong.eduvideo.view.PopMenu;
import com.zhulong.eduvideo.view.PopSpeed;
import com.zhulong.eduvideo.view.SeekNotice;
import com.zhulong.eduvideo.view.V4PlayerSeekBar;
import com.zhulong.eduvideo.view.expandrecyclerview.lessondirectory.WrappedLessonPart;
import com.zhulong.eduvideo.view.popupwindow.GestureBrightnessPopWindow;
import com.zhulong.eduvideo.view.popupwindow.GestureVolumePopWindow;
import com.zhulong.eduvideo.view.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by whb on 2017/6/28.
 * Email 18720982457@163.com
 */

public class CCVideoView extends RelativeLayout implements
        IMediaPlayer.OnBufferingUpdateListener,
        DWIjkMediaPlayer.OnInfoListener,
        DWIjkMediaPlayer.OnPreparedListener,
        DWIjkMediaPlayer.OnErrorListener,
        DWIjkMediaPlayer.OnVideoSizeChangedListener,
//        SurfaceHolder.Callback,
        TextureView.SurfaceTextureListener,
        SensorEventListener,
        DWIjkMediaPlayer.OnCompletionListener, View.OnTouchListener, OnDreamWinErrorListener {

    private static final String DEFAULT_DEFINITION = "default_definition";

    private boolean networkConnected = true;
    public DWIjkMediaPlayer player;
    // private Subtitle subtitle;
    public TextureView surfaceView;
    //    private SurfaceHolder surfaceHolder;
    private ProgressBar bufferProgressBar;
    public V4PlayerSeekBar skbProgress;
    private ImageView backPlayList;
    private TextView videoIdText, playDuration, videoDuration;
    private TextView tvDefinition;//清晰度
    private TextView tvSpeed;//倍速
    private PopMenu definitionMenu;
    private PopSpeed speedMenu;
    private RelativeLayout playerTopLayout;
    private RelativeLayout playerBottomLayout;
    private AudioManager audioManager;
    private TextView tv_tip;
    private TextView tv_download_play;
    private TextView tv_menu_play;
    private CCVideoViewRightMenu rl_menu;

    private int currentVolume;
    private int maxVolume;

    private ApplicationEx demoApplication;

    private boolean isDrag;

    /**
     * 滑动
     */
    //public GestureSeekToPopWindow mSeekToPopWindow;
    public SeekNotice mSeekToPopWindow;


    /**
     * 滑动的偏移加权
     */
    private int SLIDE_DISTANCE = 300;

    /**
     * 是否是本地播放
     */
    private boolean isLocalPlay = false;
    /**
     * 是否是单个视频播放
     */
    private boolean isSinglePlay = false;
    private boolean isPrepared;

    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;

    private int currentScreenSizeFlag = 0;
    private int currrentSubtitleSwitchFlag = 0;
    private int currentDefinitionIndex = 0;
    // 默认清晰度
    private int defaultDefinition;

    private boolean firstInitDefinition = true;
    private String path;

    public Boolean isPlaying;
    // 当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private boolean isFreeze = false;
    private boolean isSurfaceDestroy = false;

    int currentPosition;
    private Dialog dialog;

    private String[] definitionArray;
    private final String[] screenSizeArray = new String[]{"满屏", "100%", "75%", "50%"};
    private final String[] subtitleSwitchArray = new String[]{"开启", "关闭"};
    private final String subtitleExampleURL = "http://dev.bokecc.com/static/font/example.utf8.srt";
    private GestureDetector detector;
    private float scrollTotalDistance;
    private int lastPlayPosition, currentPlayPosition;
    private RelativeLayout rlBelow, rlPlay;
    private WindowManager wm;
    private ImageView ivFullscreen;
    private ConnectivityManager cm;
    private String videoId = "";
    private String cc_key = "";
    private String cc_uid = "";

    /**
     * 所有的章节
     */
    private List<LessonPartEntity> mPart;

    /**
     * 当前播放的part
     */
    private VideoParams currentPlayPart;

    /**
     * 声音
     */
    private GestureVolumePopWindow mVolumePopWindow;

    /**
     * 亮度
     */
    private GestureBrightnessPopWindow mBrightnessPopWindow;

    /**
     * CCVideo回调监听
     */
    private OnCCViewListener onCCViewlisten;

    /**
     * 是否用户在滑动调节进度条
     */
    private boolean isUserSeekBar = false;


    // 隐藏界面的线程
    private Runnable hidePlayRunnable = new Runnable() {
        @Override
        public void run() {
            setLayoutVisibility(View.GONE, false);
        }
    };
    private Activity activity;
    private Fragment fragment;

    /**
     * 是否是辅助线路播放
     */
    boolean isBackupPlay = false;


    public LinearLayout mLl_net_error;
    public RoundTextView mButton_replay;
    private SurfaceTexture surfaceTexture;
    private Surface surface;


    private Context context;
    private int surfaceViewHeight;
    private int surfaceViewWidth;
    public RelativeLayout mSurface_container;
    private MainActivity mainActivity;
    private boolean isStatus;
    private AudioManager mAudioMgr;
    public ImageView ivNext;
    private ImageView ivShare;
    public ImageView ivCollect;
    private LinearLayout llFullscreen;

    private IMediaPlayActionListener playActionListener;

    public void setMediaPlayActionListener(IMediaPlayActionListener listener) {
        this.playActionListener = listener;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }


    public CCVideoView(Context context) {
        this(context, null);
    }

    public CCVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init() {


//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();


        demoApplication = (ApplicationEx) activity.getApplication();

        if (demoApplication.getDRMServer() != null)
            demoApplication.getDRMServer().reset();
        // activity.requestWindowFeature(Window.FEATURE_NO_TITLE);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        detector = new GestureDetector(activity, new MyGesture());


        this.mVolumePopWindow = new GestureVolumePopWindow(getContext());
        this.mBrightnessPopWindow = new GestureBrightnessPopWindow(getContext());

        HttpUtil.LOG_LEVEL = HttpUtil.HttpLogLevel.DETAIL;

        defaultDefinition = SharePreferencesUtils.getInt(getContext(), DEFAULT_DEFINITION, 0);

        initView();

        initPlayHander();

        initPlayInfo();

//        setSurfaceViewLayout();

//        iniRightMenu();

        initSeek();
        initSlidingUpPanel();


    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

    }

    private OnAudioFocusChangeListener mAudioFocusChangeListener = null;

    private void initPlayInfo() {
        // 通过定时器和Handler来更新进度
        isPrepared = false;
        player = new DWIjkMediaPlayer();
        player.reset();
        player.setOnDreamWinErrorListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnVideoSizeChangedListener(this);
        player.setOnInfoListener(this);
        player.setDRMServerPort(demoApplication.getDrmServerPort());
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        if (activity != null) {
            if (isLocalPlay) {
                //本地视频播放
                if (isSinglePlay) {//单视频播放不允许旋转
                    sensorManager.unregisterListener(this);
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    if (!new File(path).exists()) {
                        return;
                    }
                }
            }
        }
    }


    /**
     * 设置头部
     *
     * @param title
     */
    public void setTitle(String title) {
        videoIdText.setText(title);
    }

    /**
     * 播放视频
     *
     * @param cc_key
     * @param cc_uid
     * @param cc_url
     * @param authCode    播放授权码
     * @param isCompleted
     */
    public void startPayVideo(String cc_key, String cc_uid, String cc_url, String authCode, boolean isCompleted) {
        Log.i("CCVideoView","part.getCc_key()" + cc_key + "------" + "part.getCc_uid()" + cc_uid + "----part.getCc_id()-----" + cc_url + "-------");
        this.videoId = cc_url;
        this.cc_key = cc_key;
        this.cc_uid = cc_uid;
        isPrepared = false;
        setLayoutVisibility(View.GONE, false);
        bufferProgressBar.setVisibility(View.VISIBLE);
        ivCenterPlay.setVisibility(View.GONE);
        if (timerTask != null) timerTask.cancel();
        currentPosition = 0;
        currentPlayPosition = 0;
        audioManager.abandonAudioFocus(null);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        player.pause();
        player.stop();
        player.reset();
        Log.i("CCVideoView","url-------" + cc_url + "------cc_uid--------" + cc_uid + "------cc_key--------" + cc_key);
        player.setVideoPlayInfo(cc_url, cc_uid, cc_key, authCode, activity);
    }

    private void updateCompleteDataPosition() {
        if (DataSet.getVideoPosition(videoId) > 0) {
            DataSet.updateVideoPosition(videoId, currentPlayPosition);
        } else {
            DataSet.insertVideoPosition(videoId, currentPlayPosition);
        }
    }


    @SuppressLint("HandlerLeak")
    private void initPlayHander() {

        playerHandler = new Handler() {
            @SuppressLint("WrongConstant")
            public void handleMessage(Message msg) {
                if (player == null) {
                    return;
                }
                // 更新播放进度
                currentPlayPosition = (int) player.getCurrentPosition();


                int duration = (int) player.getDuration();

                if (duration > 0) {
                    if (fragment != null) {//activity 调用 请先判断fragment是否存在
                        if (mainActivity.mSup_main.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            mainActivity.isRotation = true;
                        } else {
                            mainActivity.isRotation = false;
                        }
                        ((MainActivity) fragment.getActivity()).mSup_main.setTouchEnabled(true);
                    }
                    long pos = skbProgress.getMax() * currentPlayPosition / duration;

                    playDuration.setText(ParamsUtil.millisToStrMS((int) player.getCurrentPosition()));
                    if (!isUserSeekBar) {
                        skbProgress.setProgress((int) pos);
                        if (fragment != null) {
                            if (((MainActivity) activity).mSkbProgress != null) {
                                ((MainActivity) activity).mSkbProgress.setProgress((int) pos);
                            }
                        }
                    }
                    if (seekBarUpdateCallBack != null) {
                        seekBarUpdateCallBack.updateSeekBar((int) pos);
                    }
                }
            }
        };


    }

    static ImageView lockView;
    ImageView ivCenterPlay;
    // ImageView ivTopMenu;
    //TextView tvChangeVideo;
    ImageView ivPlay;//ivBackVideo, ivNextVideo,
    View view;

    private void initView() {
        if (activity instanceof MainActivity) {
            mainActivity = (MainActivity) this.activity;

        }
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_view, null);

        tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        mLl_net_error = view.findViewById(R.id.ll_net_error);
        mButton_replay = view.findViewById(R.id.button_replay);
        surfaceView = new TextureView(activity);
        mSurface_container = view.findViewById(R.id.surface_container);
        mSurface_container.addView(surfaceView);
        bufferProgressBar = (ProgressBar) view.findViewById(R.id.bufferProgressBar);

        ivCenterPlay = (ImageView) view.findViewById(R.id.iv_center_play);
        ivCenterPlay.setOnClickListener(onClickListener);

        backPlayList = (ImageView) view.findViewById(R.id.backPlayList);
        videoIdText = (TextView) view.findViewById(R.id.videoIdText);

        mSeekToPopWindow = (SeekNotice) view.findViewById(R.id.seek_notice);

        playDuration = (TextView) view.findViewById(R.id.playDuration);
        videoDuration = (TextView) view.findViewById(R.id.videoDuration);
        playDuration.setText(ParamsUtil.millisToStrMS(0));
        videoDuration.setText(ParamsUtil.millisToStrMS(0));

        ivPlay = (ImageView) view.findViewById(R.id.iv_play);

        ivPlay.setOnClickListener(onClickListener);
        ivNext = view.findViewById(R.id.iv_next);
        ivNext.setOnClickListener(onClickListener);

        tvDefinition = (TextView) view.findViewById(R.id.tv_definition);
        tvSpeed = (TextView) view.findViewById(R.id.tv_speed);

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mVolumePopWindow.setProgress(currentVolume * 100 / maxVolume);

        skbProgress = (V4PlayerSeekBar) view.findViewById(R.id.skbProgress);
        skbProgress.setMax(100);
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);
        playerTopLayout = (RelativeLayout) view.findViewById(R.id.playerTopLayout);
        playerBottomLayout = (RelativeLayout) view.findViewById(R.id.playerBottomLayout);

        ivFullscreen = (ImageView) view.findViewById(R.id.iv_fullscreen);
        llFullscreen = view.findViewById(R.id.ll_fullscreen);

        tv_download_play = (TextView) view.findViewById(R.id.tv_download_play);
        tv_menu_play = (TextView) view.findViewById(R.id.tv_menu_play);

        rl_menu = (CCVideoViewRightMenu) view.findViewById(R.id.rl_menu);


        ivShare = view.findViewById(R.id.share);
        ivCollect = view.findViewById(R.id.collect);
        ivShare.setOnClickListener(onClickListener);
        ivCollect.setOnClickListener(onClickListener);
        if (fragment instanceof MediaPlayFragment) {
            ivShare.setVisibility(View.VISIBLE);
            ivCollect.setVisibility(View.VISIBLE);
        } else {
            ivShare.setVisibility(View.GONE);
            ivCollect.setVisibility(View.GONE);
        }
        llFullscreen.setOnClickListener(onClickListener);
        backPlayList.setOnClickListener(onClickListener);
        tvDefinition.setOnClickListener(onClickListener);
        tvSpeed.setOnClickListener(onClickListener);
        tv_download_play.setOnClickListener(onClickListener);
        tv_menu_play.setOnClickListener(onClickListener);


//        surfaceHolder = surfaceView.getHolder();
        surfaceView.setSurfaceTextureListener(this);


//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //2.3及以下使用，不然出现只有声音没有图像的问题
//        surfaceHolder.addCallback(this);


        lockView = (ImageView) view.findViewById(R.id.iv_lock);
        lockView.setSelected(false);
        lockView.setOnClickListener(onClickListener);

        rlPlay = (RelativeLayout) view.findViewById(R.id.rl_play);

        mSeekToPopWindow.setParentView(rlPlay);

        rlPlay.setClickable(true);
        rlPlay.setLongClickable(true);
        rlPlay.setFocusable(true);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        addView(view);
        rlPlay.setOnTouchListener(this);
        changePlayStatus(false);
    }


    private void initSlidingUpPanel() {
        if (mainActivity != null) {
            mainActivity.mRlVideoStatus.setVisibility(View.VISIBLE);
            mainActivity.mRlVideoStatus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportChangePlayStatus(player.isPlaying());
                    if (player.isPlaying()) {
                        changePlayStatus(true);
                    } else {
                        changePlayStatus(false);
                    }

                }
            });
            mainActivity.mRlClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainActivity.mSup_main != null) {
                        if (mainActivity.mSup_main.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                            mainActivity.mSup_main.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            mainActivity.mIvPlay.setVisibility(View.INVISIBLE);
                            mainActivity.mIvPause.setVisibility(View.VISIBLE);
                            if (fragment != null) {
                                ((MediaPlayFragment) fragment).getUserlessontime();
                            }
                            player.stop();
                            mainActivity.closeBottom = true;
                            mainActivity.isFinish = true;
                            HomepageFragment.newInstance().llHomeBottom.setVisibility(View.GONE);
                            updateDataPosition();
                        } else {
                            mainActivity.mSup_main.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    }
                }
            });
        }
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int progress = 0;

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (networkConnected || isLocalPlay) {
                if (player != null) {
                    player.seekTo(progress);
                    playerHandler.postDelayed(hidePlayRunnable, 5000);
                }

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            playerHandler.removeCallbacks(hidePlayRunnable);

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (networkConnected || isLocalPlay) {
                this.progress = (int) (progress * player.getDuration() / seekBar.getMax());
            }

        }
    };


    private SeekBarUpdateCallBack seekBarUpdateCallBack;

    @Override
    public void onPlayError(final DreamwinException e) {
        Log.e("CCVideoView","onPlayError:" + e.getMessage());
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastInfo(e.getMessage());
                }
            });
        }
    }


    public interface SeekBarUpdateCallBack {
        void updateSeekBar(int pos);
    }

//    /**
//     * 初始化右侧菜单
//     */
//    public void iniRightMenu() {
//        rl_menu.setParts(mPart);
//        rl_menu.setCurrentPlayPart(currentPlayPart);
//        rl_menu.setMenuCallback(new CCVideoViewRightMenu.MenuCallback() {
//            @Override
//            public void onSelectClick(WrappedLessonPart part) {
//                rl_menu.setVisibility(INVISIBLE);
//                onCCViewlisten.paySelectPart(part);
//            }
//        });
//        rl_menu.init();
//    }

    /**
     * 初始化手动滑动
     */
    public void initSeek() {
        mSeekToPopWindow.setCallBack(new SeekNotice.SeekNoticeCallBack() {
            @Override
            public void dismiss(int seekCurrentPosition) {
                player.seekTo(seekCurrentPosition);
                isUserSeekBar = false;
            }
        });
    }

    //用户快进/快退动作时的初始时间和最终时间
    private String startTime;
    private String endTime;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isUserSeekBar){
                    startTime = String.valueOf(player.getCurrentPosition());
                }
            case MotionEvent.ACTION_UP:

                if (mVolumePopWindow != null) {
                    mVolumePopWindow.dismiss();
                }
                if (mBrightnessPopWindow != null) {
                    mBrightnessPopWindow.dismiss();
                }
                if (mSeekToPopWindow != null && isUserSeekBar) {
                    mSeekToPopWindow.dismiss();

                    endTime = String.valueOf(player.getCurrentPosition());
                    if (playActionListener != null) {
                        playActionListener.startServiceReportSeek(startTime, endTime);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        if (!isPrepared) {
            return true;
        }

        resetHideDelayed();
        // 事件监听交给手势类来处理
        try {
            detector.onTouchEvent(event);
        } catch (NullPointerException e) {
            Logger.e("滑动崩溃");
        }
        return true;
    }


//
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface1, int width, int height) {
//
//        try {
//            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setOnBufferingUpdateListener(this);
//            player.setOnPreparedListener(this);
//            player.setSurface(surface);
//
//            player.setScreenOnWhilePlaying(true);
//
//            if (isLocalPlay) {
//                player.setDRMVideoPath(path, activity);
//            }
//
//            demoApplication.getDRMServer().reset();
//            player.prepareAsync();
//        } catch (Exception e) {
//            Log.e("videoPlayer", "error", e);
//        }
//        Log.i("videoPlayer", "surface created");
//
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface2, int width, int height) {
//
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        if (player == null) {
//            return false;
//        }
//        if (isPrepared) {
//            currentPosition = (int) player.getCurrentPosition();
//        }
//
//        isPrepared = false;
//        isSurfaceDestroy = true;
//
//        player.stop();
//        player.reset();
//        return false;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//    }


    /**
     * 创建实体类
     */
    public static class Builder {

        CCVideoView ccVideoView;
        Context context;
        Activity activity;
        OnCCViewListener onCCViewlisten;
        /**
         * 是否是本地播放
         **/
        boolean isLocalPlay = false;
        /**
         * 是否是单个视频播放
         **/
        boolean isSinglePlay = false;

        String path;
        //        List<LessonPartEntity> mPart;
        private VideoParams currentPlayPart;
        private Fragment fragment;


        public Builder() {
        }


        public Builder with(Context context) {
            this.context = context;
            return this;
        }

        public Builder setActivity(Context context) {
            if (context instanceof Activity) {
                this.activity = (Activity) context;
            } else {
                this.activity = null;
            }


            return this;
        }

        public Builder setFragment(Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public Builder setOnCCViewlistenr(OnCCViewListener onCCViewlisten) {
            this.onCCViewlisten = onCCViewlisten;
            return this;
        }

        public Builder setLocalPlay(boolean localPlay) {
            this.isLocalPlay = localPlay;
            return this;
        }

        public Builder setSinglePay(boolean isSinglePlay) {
            this.isSinglePlay = isSinglePlay;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

//        public Builder setmPart(List<LessonPartEntity> mPart) {
//            this.mPart = mPart;
//            return this;
//        }

        public Builder setCurrentPlayPart(VideoParams wrappedLessonPart) {
            this.currentPlayPart = wrappedLessonPart;
            return this;
        }

        public CCVideoView onCreate() {
            ccVideoView = new CCVideoView(context);
            if (fragment != null) {
                ccVideoView.setFragment(fragment);
            }
            ccVideoView.setActivity(activity);
            ccVideoView.setOnCCViewlisten(onCCViewlisten);
            ccVideoView.setPath(path);
            ccVideoView.setLocalPlay(isLocalPlay);
            ccVideoView.setSinglePlay(isSinglePlay);
//            ccVideoView.setmPart(mPart);
            ccVideoView.setCurrentPlayPart(currentPlayPart);
            ccVideoView.init();
            return ccVideoView;
        }


    }

    /**
     * 监听点击事件
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            resetHideDelayed();
            switch (v.getId()) {
                case R.id.backPlayList:
                    if (activity != null) {
                        if (isPortrait() || isSinglePlay) {
                            if (activity instanceof MainActivity) {
                                mainActivity.mSup_main.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            } else if (activity instanceof LocalCCUrlPlayActivity) {
                                LocalCCUrlPlayActivity localCCUrlPlayActivity = (LocalCCUrlPlayActivity) activity;
                                if (player != null) {
                                    long currentPosition = player.getCurrentPosition() / 1000;
                                    localCCUrlPlayActivity.startServiceUpdateUserLessonTime(currentPosition);
                                }
                                activity.finish();
                            } else {
                                Navigation.findNavController(fragment.getView()).navigateUp();
                            }
                        } else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }

                    break;
                case R.id.ll_fullscreen:
                    if (activity != null) {
                        if (isPortrait()) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            if (activity instanceof MainActivity) {
                                mainActivity.mSup_main.setDragView(mainActivity.mFloatwindow_bottom);
                            }
                        } else {
                            if (activity instanceof MainActivity) {
                                if (fragment != null && ((MediaPlayFragment) fragment).playerLayout != null)
                                    mainActivity.mSup_main.setDragView(((MediaPlayFragment) fragment).playerLayout);
                            }
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }

                    break;
                case R.id.tv_definition:
                    tvDefinition.setBackground(getResources().getDrawable(R.drawable.shape_transparent_red_radius_bg));
                    if (isPortrait()) {
                        definitionMenu.showAsDropDown(v, true);
                    } else {
                        if (!isLocalPlay) {
                            definitionMenu.showAsDropDown(v, false);
                        }
                    }

                    break;
                case R.id.tv_speed:
                    tvSpeed.setBackground(getResources().getDrawable(R.drawable.shape_transparent_red_radius_bg));
                    if (isPortrait()) {
                        speedMenu.showAsDropDown(v, true);
                    } else {
                        speedMenu.showAsDropDown(v, false);
                    }
                    break;
                case R.id.iv_lock:
                    if (lockView.isSelected()) {
                        lockView.setSelected(false);
                        setLayoutVisibility(View.VISIBLE, true);
                        toastInfo("已解开屏幕");
                    } else {
                        lockView.setSelected(true);
                        setLandScapeRequestOrientation();
                        setLayoutVisibility(View.GONE, true);
                        lockView.setVisibility(View.VISIBLE);
                        toastInfo("已锁定屏幕");
                    }
                    break;
                case R.id.iv_center_play:
                case R.id.iv_play:
                    if (player != null){
                        reportChangePlayStatus(player.isPlaying());
                        changePlayStatus(player.isPlaying());
                    }
                    break;
                case R.id.tv_download_play:
                    if (TextUtils.isEmpty(videoId)) return;
                    if (onCCViewlisten != null)
                        onCCViewlisten.downloadPay();
                    break;
                case R.id.tv_menu_play:
                    setLayoutVisibility(View.GONE, false);
                    rl_menu.setVisibility(VISIBLE);
                    break;
                case R.id.iv_next:
                    if (isPrepared) {
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentPlayPosition = 1;
                                    currentPosition = 1;
                                    updateCompleteDataPosition();
                                    if (null != onCCViewlisten) onCCViewlisten.onCompletion();//回调完成
                                }
                            });
                        }
                    }
                    break;
                case R.id.share:
                    if (fragment instanceof MediaPlayFragment) {
                        BuriedPointStatistics.get(activity,
                                BuriedPointStatistics.CourseShareHits, "课程分享点击量", 1)
                                .execute();
                        ((MediaPlayFragment) fragment).openBottom();
                    }
                    break;
                case R.id.collect:
                    if (fragment instanceof MediaPlayFragment) {
                        if (!ApplicationEx.getInstance().isLogin()) {
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            mainActivity.mSup_main.setDragView(mainActivity.mFloatwindow_bottom);
                            return;
                        }
                        if (((MediaPlayFragment) fragment).playDetail != null) { //TODO 收藏
                            if (((MediaPlayFragment) fragment).playDetail.getIs_collect().equals("1")) {
//                                已收藏的取消收藏
                                ((MediaPlayFragment) fragment).cancelCollect();
                            } else {
                                //未收藏的收藏
                                ((MediaPlayFragment) fragment).collectLesson();
                            }
                        }

                    }
                    break;

            }
        }
    };

    /**
     * 点击暂停或恢复播放
     */
    private void reportChangePlayStatus(boolean isPlaying) {
        if (player == null) return;
        String currentTime = String.valueOf(player.getCurrentPosition());

        if (playActionListener != null) {
            if (isPlaying) {
                playActionListener.startServiceReportPause(currentTime);
            } else {
                playActionListener.startServiceReportResume(currentTime);
            }
        }
    }

    public void changePlayStatus(boolean isPlaying) {
        Log.i("CCVideoView","-------------changePlayStatus---" + isPlaying);
        if (isPlaying) {
            isStatus = false;

            player.pause();
            if (activity instanceof MainActivity) {
                mainActivity.mIvPause.setVisibility(View.INVISIBLE);
                mainActivity.mIvPlay.setVisibility(View.VISIBLE);
            }
            ivCenterPlay.setVisibility(View.VISIBLE);
            ivPlay.setImageResource(R.drawable.smallbegin_ic);
            bufferProgressBar.setProgress(View.GONE);
        } else {
            isStatus = true;
            if (activity instanceof MainActivity) {
                mainActivity.mIvPause.setVisibility(View.VISIBLE);
                mainActivity.mIvPlay.setVisibility(View.INVISIBLE);
            }
            if (audioManager != null) {
                audioManager.abandonAudioFocus(null);
                audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
            if (player != null) {
                player.start();
            }
            //            hidetip();
            ivCenterPlay.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.smallstop_ic);
        }
    }

    /**
     * 隐藏提示
     */
    public void hidetip() {
        tv_tip.setText("");
        tv_tip.setVisibility(INVISIBLE);
    }


    // 设置横屏的固定方向，禁用掉重力感应方向
    private void setLandScapeRequestOrientation() {
        int rotation = wm.getDefaultDisplay().getRotation();
        // 旋转90°为横屏正向，旋转270°为横屏逆向
        if (activity != null) {
            if (rotation == Surface.ROTATION_90) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (rotation == Surface.ROTATION_270) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }

    }

    private void toastInfo(String info) {
        Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
    }

    // 获得当前屏幕的方向
    private boolean isPortrait() {
        int mOrientation = getContext().getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        } else {
            return true;
        }
    }

    // 控制播放器面板显示
    private boolean isDisplay = false;

    /**
     * 控制面板的显示和隐藏
     *
     * @param visibility
     * @param isDisplay
     */
    private void setLayoutVisibility(int visibility, boolean isDisplay) {
        if (player == null || player.getDuration() <= 0) {
            return;
        }
        playerHandler.removeCallbacks(hidePlayRunnable);

        this.isDisplay = isDisplay;

        if (definitionMenu != null && visibility == View.GONE) {
            definitionMenu.dismiss();
        }
        if (speedMenu != null && visibility == View.GONE) {
            speedMenu.dismiss();
            if (!isPortrait()) {
                ScreenUtils.showFullScreen(activity, true);
            }
        }

        if (isDisplay) {
            playerHandler.postDelayed(hidePlayRunnable, 5000);
        }

        if (isPortrait()) {
            llFullscreen.setVisibility(visibility);
            ivFullscreen.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.letv_skin_v4_btn_chgscreen_small));
            lockView.setVisibility(View.GONE);
//            tvDefinition.setVisibility(View.GONE);
//            tvSpeed.setVisibility(View.GONE);
        } else {
            ivFullscreen.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.letv_skin_v4_btn_chgscreen_large));
            lockView.setVisibility(visibility);
            if (lockView.isSelected()) {
                visibility = View.GONE;
            }
            tvDefinition.setVisibility(visibility);
            tvSpeed.setVisibility(visibility);
        }
        Log.i("CCVideoView","isLocalPlay-------" + isLocalPlay);
        if (isLocalPlay) {
            tvDefinition.setVisibility(View.GONE);
            tv_download_play.setVisibility(GONE);
        }
        if (isSinglePlay) {
            llFullscreen.setVisibility(View.INVISIBLE);
            tv_menu_play.setVisibility(INVISIBLE);
        }
        if (!isPortrait() || visibility == GONE) playerTopLayout.setVisibility(visibility);

        playerBottomLayout.setVisibility(visibility);
        statusCallBack(visibility);
    }


    // 重置隐藏界面组件的延迟时间
    private synchronized void resetHideDelayed() {
        if (playerHandler == null) return;
        try {
            playerHandler.removeCallbacks(hidePlayRunnable);
            playerHandler.postDelayed(hidePlayRunnable, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int mX, mY, mZ;
    private long lastTimeStamp = 0;
    private Calendar mCalendar;
    public SensorManager sensorManager;


    /**
     * 传感器监听
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (mainActivity != null) {
            if (!mainActivity.isRotation) {
                return;
            }
        }
        Log.i("CCVideoView","isSelected---" + lockView.toString());
        if (!lockView.isSelected() && (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {

            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis() / 1000l;

            int second = mCalendar.get(Calendar.SECOND);// 53

            int px = Math.abs(mX - x);
            int py = Math.abs(mY - y);
            int pz = Math.abs(mZ - z);

            int maxvalue = getMaxValue(px, py, pz);
            if (maxvalue > 2 && (stamp - lastTimeStamp) > 1) {
                lastTimeStamp = stamp;
                if (activity != null) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }

            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }


    /**
     * 获取一个最大值
     *
     * @param px
     * @param py
     * @param pz
     * @return
     */
    private int getMaxValue(int px, int py, int pz) {
        int max = 0;
        if (px > py && px > pz) {
            max = px;
        } else if (py > px && py > pz) {
            max = py;
        } else if (pz > px && pz > py) {
            max = pz;
        }
        return max;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mediaPlayer, int i) {
        //skbProgress.setProgress(i);
        //skbProgress.setSecondaryProgress(i);
    }

    /**
     * 播放完成
     *
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(IMediaPlayer mediaPlayer) {
        //判断当前视频是否播放完成。
        long current = player.getCurrentPosition();
        long duration = player.getDuration();
        //获取视频失败时会调用onCompletion，此时的duration = 0
        //距离结束时间是否小于5000毫秒 不处理
        if (duration == 0 || Math.abs(duration - current) > 5000)
            return;
        if (isSinglePlay) {
            toastInfo("播放完成！");
            if (fragment != null) {
                if (activity instanceof MainActivity) {
                    mainActivity.mSup_main.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                } else {
                    Navigation.findNavController(fragment.getView()).navigateUp();
                }

            }

            return;
        }

        if (isPrepared) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        currentPlayPosition = 1;
                        currentPosition = 1;
                        isPrepared = false;
                        updateCompleteDataPosition();
                        if (null != onCCViewlisten) onCCViewlisten.onCompletion();//回调完成
                    }
                });
            }

        }
    }

    @Override
    public boolean onError(IMediaPlayer mediaPlayer, int what, int i1) {
        Log.e("CCVideoView","onError "+what);
        if (DWMediaPlayer.MEDIA_ERROR_DRM_LOCAL_SERVER == what) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                    toastInfo("服务器异常");
                    }
                });
            }

        } else {
            if (!isBackupPlay && !isLocalPlay) {
                startBackupPlay();
            } else {
                if (!isLocalPlay) {
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.stop();
                                mLl_net_error.setVisibility(View.VISIBLE);
                                mButton_replay.setVisibility(View.VISIBLE);
                                tv_tip.setVisibility(VISIBLE);
                                tv_tip.setText("网络不稳定，请稍后重试。");
                                bufferProgressBar.setVisibility(GONE);
//                        bufferProgressBar.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                } else {
                    if (activity != null) {
                        ToastUtil.showToastByType(activity, ToastUtil.FAIL, "播放异常");
                        if (onCCViewlisten != null)
                            onCCViewlisten.playError();
                    }
                }

            }

        }
        return true;
    }

    //---------------------------启用备用路线--------------------------------------------
    private Runnable backupPlayRunnable = new Runnable() {
        @Override
        public void run() {
            startBackupPlay();
        }
    };

    private void startBackupPlay() {
        cancelTimerTask();
        player.setBackupPlay(true);
        isBackupPlay = true;
        player.reset();
        player.prepareAsync();
    }

    private void cancelTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }
    //-----------------------------------------------------------------------

    @Override
    public boolean onInfo(IMediaPlayer mediaPlayer, int what, int i1) {
        switch (what) {
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (player.isPlaying()) {
                    bufferProgressBar.setVisibility(View.VISIBLE);
                }
                if (!isBackupPlay) {
                    playerHandler.postDelayed(backupPlayRunnable, 10 * 1000);
                }
                break;
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_END:
                bufferProgressBar.setVisibility(View.GONE);
                if (onCCViewlisten != null)
                    onCCViewlisten.payStatus(1);
                playerHandler.removeCallbacks(backupPlayRunnable);
                break;
        }
        return false;
    }

    /**
     * 更新进度条
     */
    private void initTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!isPrepared) {
                    return;
                }

                playerHandler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void onPrepared(IMediaPlayer mediaPlayer) {
        initTimerTask();
        isPrepared = true;
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (!isFreeze) {
            if (isPlaying == null || isPlaying.booleanValue()) {
                audioManager.abandonAudioFocus(null);
                audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                player.start();
                hidetip();
                ivPlay.setImageResource(R.drawable.smallstop_ic);
            }
        }

//        if (!isLocalPlay) {
            if (currentPosition > 0) {
                player.seekTo(currentPosition);
            } else {
                lastPlayPosition = DataSet.getVideoPosition(videoId);
                if (lastPlayPosition > 0) {
                    player.seekTo(lastPlayPosition);
                }
            }
//        }

        if (!isLocalPlay) {
            initDefinitionPopMenu();
        }

        initSpeedMenu();

        bufferProgressBar.setVisibility(View.GONE);
//        setSurfaceViewLayout();
        videoDuration.setText(ParamsUtil.millisToStrMS((int) player.getDuration()));
    }

    // 设置surfaceview的布局大小
    private void setSurfaceViewLayout() {
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams params = getScreenSizeParams(currentScreenSizeFlag);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setPadding(0, 0, 0, 0);
        surfaceView.setLayoutParams(params);
//        surfaceHolder.setSizeFromLayout();
    }

    private LayoutParams getScreenSizeParams(int position) {
        currentScreenSizeFlag = position;
        int width = 600;
        int height = 400;
        if (isPortrait()) {
            width = wm.getDefaultDisplay().getWidth();
            height = width * 9 / 16; //根据当前布局更改
        } else {
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
        }

        /*String screenSizeStr = screenSizeArray[position];
        if (screenSizeStr.indexOf("%") > 0) {// 按比例缩放*/
        int vWidth = 0;
        int vHeight = 0;
        if (player != null) {
            vHeight = player.getVideoHeight();
            vWidth = player.getVideoWidth();
        }

        if (vWidth == 0) {
            vWidth = 600;
        }


        if (vHeight == 0) {
            vHeight = 400;
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

           /* int screenSize = ParamsUtil.getInt(screenSizeStr.substring(0, screenSizeStr.indexOf("%")));
            width = (width * screenSize) / 100;
            height = (height * screenSize) / 100;
        }*/

        LayoutParams params = new LayoutParams(width, height);
        return params;
    }


    private void initDefinitionPopMenu() {
        //获取清晰度
        final Map<String, Integer> definitionMap = player.getDefinitions();
        //画质从高到底显示
        definitionArray = sortDefinitions(definitionMap);
        showTvDefinition();
        definitionMenu = new PopMenu(activity, R.drawable.popdown, currentDefinitionIndex, activity.getResources().getDimensionPixelSize(R.dimen.popmenu_height), tvDefinition);
        definitionMenu.addItems(definitionArray);
        definitionMenu.setOnItemClickListener(new PopMenu.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    currentDefinitionIndex = position;
                    defaultDefinition = definitionMap.get(definitionArray[position]);

                    if (isPrepared) {
                        currentPosition = (int) player.getCurrentPosition();
                        if (player.isPlaying()) {
                            isPlaying = true;
                        } else {
                            isPlaying = false;
                        }
                    }
                    isPrepared = false;
                    setLayoutVisibility(View.GONE, false);
                    bufferProgressBar.setVisibility(View.VISIBLE);
                    demoApplication.getDRMServer().disconnectCurrentStream();
                    player.reset();
                    demoApplication.getDRMServer().reset();
                    player.setSurface(surface);
                    player.setDefinition(mainActivity.getApplicationContext(), defaultDefinition);
                    setPlayerSpeed();
                    SharePreferencesUtils.putInt(getContext(), DEFAULT_DEFINITION, defaultDefinition);
                } catch (IOException e) {
                    Log.e("player error", e.getMessage());
                }


            }
        });
    }

    private void showTvDefinition() {
        if (definitionArray.length > currentDefinitionIndex) {
            setTvDefinition(definitionArray[currentDefinitionIndex]);
        }
    }

    private String[] sortDefinitions(Map<String, Integer> stringIntegerMap) {
        //按清晰度从高到底排序
        final ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(stringIntegerMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        final int size = entries.size();
        //如果没有默认清晰度，或没有该清晰度，为最高
        if (defaultDefinition == 0 || !stringIntegerMap.containsValue(defaultDefinition)) {
            defaultDefinition = entries.get(0).getValue();
        }

        // 设置清晰度列表
        definitionArray = new String[size];
        for (int i = 0; i < size; i++) {
            final Map.Entry<String, Integer> item = entries.get(i);
            definitionArray[i] = item.getKey();

            //清晰选择弹窗下标
            if (defaultDefinition == item.getValue()) {
                currentDefinitionIndex = i;
            }
        }

        return definitionArray;
    }

    private void setTvDefinition(String key) {
        if (key.equals("清晰")) {
            key = "标清";
        }
        tvDefinition.setText(key);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int i, int j) {
        setSurfaceViewLayout();
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setOnBufferingUpdateListener(this);
//            player.setOnPreparedListener(this);
//            player.setDisplay(holder);
//            player.setScreenOnWhilePlaying(true);
//
//            if (isLocalPlay) {
//                player.setDRMVideoPath(path, activity);
//            }
//
//            demoApplication.getDRMServer().reset();
//            player.prepareAsync();
//        } catch (Exception e) {
//            Log.e("videoPlayer", "error", e);
//        }
//        Log.i("videoPlayer", "surface created");
//    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            if (surfaceTexture != null) {
                surfaceView.setSurfaceTexture(surfaceTexture);
            } else {
                surfaceTexture = surface;
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setOnBufferingUpdateListener(this);
                player.setOnPreparedListener(this);
//            player.setDisplay(surface);
                this.surface = new Surface(surface);
                player.setSurface(this.surface);

                player.setScreenOnWhilePlaying(true);

                if (isLocalPlay) {
                    player.setOfflineVideoPath(path, activity);
                    demoApplication.getDRMServer().resetLocalPlay();
                } else {
                    demoApplication.getDRMServer().reset();
                }


                player.prepareAsync();
            }


        } catch (Exception e) {
            Log.e("videoPlayer", "error", e);
        }
        Log.i("videoPlayer", "surface created");

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surfaceTexture = surface;

//        if (player == null) {
//            return false;
//        }
//        if (isPrepared) {
//            currentPosition = (int) player.getCurrentPosition();
//        }
//
//        isPrepared = false;
//        isSurfaceDestroy = true;
//        surfaceTexture = surface;

//        player.stop();
//        player.reset();

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 弹出倍速窗口
     */
    public void initSpeedMenu() {
        speedMenu = new PopSpeed(activity, tvSpeed);
        speedMenu.setSpeedsClickListener(new PopSpeed.setSpeedsClickListener() {
            @Override
            public void onItemClick(float speed) {
                //设置当前倍速
                player.setSpeed(speed);
                tvSpeed.setText(speed + "X");
                speedMenu.dismiss();

                if (playActionListener != null) {
                    playActionListener.startServiceReportSpeed(speed);
                }
            }
        });
        setPlayerSpeed();
    }

    private void setPlayerSpeed() {
        //设置默认倍速
        try {
            CharSequence text = tvSpeed.getText();
            CharSequence charSequence = text.subSequence(0, text.length() - 1);
            Float speed = Float.parseFloat(charSequence.toString());
            player.setSpeed(speed);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        holder.setFixedSize(width, height);
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        if (player == null) {
//            return;
//        }
//        if (isPrepared) {
//            currentPosition = (int) player.getCurrentPosition();
//        }
//
//        isPrepared = false;
//        isSurfaceDestroy = true;
//
//        player.stop();
//        player.reset();
//    }


    // 手势监听器类
    private class MyGesture extends GestureDetector.SimpleOnGestureListener {

        private Boolean isVideo;
        private float scrollCurrentPosition;
        private float scrollCurrentVolume;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {


            if (lockView.isSelected()) {
                return true;
            }
            if (isVideo == null) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    isVideo = true;
                } else {
                    isVideo = false;
                }
            }


            if (isVideo.booleanValue()) {
                if (isDrag){
                    parseVideoScroll(distanceX);
                }
            } else {
                if (activity != null) {
                    if (ScreenUtils.isInRight(activity, (int) e1.getX())) {
                        Log.i("CCVideoView","---------parseAudioScroll");
                        parseAudioScroll(distanceY);
                    } else if (ScreenUtils.isInLeft(activity, (int) e1.getX())) {
                        this.mYMove = e2.getY();
                        int addtion = (int) (this.mYDown - this.mYMove) * 100 / ScreenUtils.getHeight(getContext());
                        parseBrighScroll(addtion);
                    }
                }

            }


            return super.onScroll(e1, e2, distanceX, distanceY);
        }


        /**
         * 调节播放进度
         *
         * @param distanceX
         */
        private void parseVideoScroll(float distanceX) {

            isUserSeekBar = true;

            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }

            scrollTotalDistance += distanceX;

            float duration = (float) player.getDuration();

            float width = wm.getDefaultDisplay().getWidth() * 0.75f; // 设定总长度是多少，此处根据实际调整
            //右滑distanceX为负
            float seekCurrentPosition = scrollCurrentPosition - (float) duration * scrollTotalDistance / width;

            if (seekCurrentPosition < 0) {
                seekCurrentPosition = 0;
            } else if (seekCurrentPosition > duration) {
                seekCurrentPosition = duration;
            }

            seekTo((int) distanceX, (int) seekCurrentPosition);

            playDuration.setText(ParamsUtil.millisToStrMS((int) seekCurrentPosition));
            int pos = (int) (skbProgress.getMax() * seekCurrentPosition / duration);
            skbProgress.setProgress(pos);
        }

        /**
         * 使用的乐视亮度调节
         *
         * @param addtion
         */
        private int level = 0;// 记录popupwindow每次显示时候的初始值
        private float mYDown;
        private float mXDown;
        private float mYMove;
        private int mCurrentBrightness = -1;

        private void parseBrighScroll(int addtion) {
            if (isPortrait()) return;
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            if (activity != null) {
                if (!mBrightnessPopWindow.isShowing()) {

                    mBrightnessPopWindow.showPopWindow(CCVideoView.this);
                    this.level = (getScreenBrightness(activity) * 100) / 255;
                    mBrightnessPopWindow.setProgress(this.level);
                    setScreenBrightness(activity, this.level);


                }
                int brightness = this.level + addtion;
                brightness = (brightness > 100 ? 100 : (brightness < 0 ? 0 : brightness));
                setScreenBrightness(activity, brightness * 255 / 100);
                mBrightnessPopWindow.setProgress(brightness);
            }
        }

        /**
         * 设置亮度
         *
         * @param paramInt 取值0-255
         */
        public void setScreenBrightness(Activity activity, int paramInt) {
            this.mCurrentBrightness = paramInt;
            ScreenBrightnessManager.setScreenBrightness(activity, paramInt);

        }

        /**
         * 获取当前亮度(取值0-255)
         */
        public int getScreenBrightness(Activity activity) {
            if (this.mCurrentBrightness != -1) {
                return this.mCurrentBrightness;
            }
            return ScreenBrightnessManager.getScreenBrightness(activity);
        }

        /**
         * 声音调节
         *
         * @param distanceY
         */
        private void parseAudioScroll(float distanceY) {
            if (isPortrait()) return;
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            scrollTotalDistance += distanceY;

            float height = wm.getDefaultDisplay().getHeight() * 0.75f;
            // 上滑distanceY为正
            currentVolume = (int) (scrollCurrentVolume + maxVolume * scrollTotalDistance / height);

            if (currentVolume < 0) {
                currentVolume = 0;
            } else if (currentVolume > maxVolume) {
                currentVolume = maxVolume;
            }

            int flag = currentVolume * 100 / maxVolume;
            mVolumePopWindow.setProgress(flag);
            setVolume(flag);
            if (!mVolumePopWindow.isShowing() && !isPortrait()) {//没有显示且是横屏装太
                mVolumePopWindow.showPopWindow(CCVideoView.this);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            scrollTotalDistance = 0f;
            isVideo = null;

            scrollCurrentPosition = (float) player.getCurrentPosition();
            scrollCurrentVolume = currentVolume;

            this.mXDown = e.getX();
            this.mYDown = e.getY();
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (lockView.isSelected()) {
                return true;
            }
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            if (player != null){
                reportChangePlayStatus(player.isPlaying());
                changePlayStatus(player.isPlaying());
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (rl_menu.getVisibility() == VISIBLE) {
                rl_menu.setVisibility(INVISIBLE);
            } else if (isDisplay) {
                setLayoutVisibility(View.GONE, false);
            } else {
                setLayoutVisibility(View.VISIBLE, true);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    //	------------------------音量控制----------------------
    public void setVolume(int percentage) {
        if (null == this.audioManager) {
            return;
        }

        if (percentage < 0 || percentage > 100) {
            return;
        }

        int maxValue = this.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        this.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentage * maxValue / 100, 0);
    }

    /**
     * 设置进度
     */
    protected void seekTo(int distanceX, int seekCurrentPosition) {
        if (!mSeekToPopWindow.isShowing() && !isPortrait()) {
            mSeekToPopWindow.showPopWindow(CCVideoView.this);
        }

        if (distanceX <= 0) {
            mSeekToPopWindow.setImageForward();
        } else {
            mSeekToPopWindow.setImageRewind();
        }

        if (skbProgress != null) {
            if (mSeekToPopWindow != null) {
                String progress = TimerUtils.stringForTime((int) (skbProgress.getProgress() * player.getDuration() / skbProgress.getMax() / 1000));
                int times = (int) (player.getDuration() / 1000);
                String duration = TimerUtils.stringForTime(times);
                mSeekToPopWindow.setProgress(progress, duration, seekCurrentPosition);
            }
        }
    }


    public void onDestroy() {
        Log.i("CCVideoView","------------------onDestroy");
        demoApplication.getDRMServer().disconnectCurrentStream();
        if (timerTask != null) {
            timerTask.cancel();
        }

        if (playerHandler != null) {
            playerHandler.removeCallbacksAndMessages(null);
            playerHandler = null;
        }

        updateDataPosition();

        if (player != null) {
            player.stop();
            player.reset();
//            player.release();

            player = null;
        }
        if (surfaceView != null) {

            surfaceTexture = null;
        }


        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void onResume() {


        if (isFreeze) {
            isFreeze = false;
            if (isPrepared) {
                changePlayStatus(true);
            } else {
                changePlayStatus(false);
            }
        } else {

            if (isPlaying != null && isPlaying.booleanValue() && isPrepared) {
                if (!isPlaying()) {
                    changePlayStatus(true);
                }
            } else {
                if (isPlaying()) {
                    changePlayStatus(false);
                }
            }
        }
        if (!isLocalPlay) {
            //监听横竖屏选装
            //sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        }

    }

    public void onPause() {

        if (player.isPlaying()) {
            isPlaying = false;
            changePlayStatus(true);
        } else {
            isPlaying = false;
        }
        if (isPrepared) {
            // 如果播放器prepare完成，则对播放器进行暂停操作，并记录状态
        } else {
            // 如果播放器没有prepare完成，则设置isFreeze为true
            isFreeze = true;
        }


    }

    protected void onStop() {
        if (!isLocalPlay) {
            sensorManager.unregisterListener(this);
            setLandScapeRequestOrientation();
        }
    }

    /**
     * 保存更新播放位置
     */
    private void updateDataPosition() {
//        if (isLocalPlay) {
//            return;
//        }

        if (currentPlayPosition > 0) {
            if (DataSet.getVideoPosition(videoId) > 0) {
                DataSet.updateVideoPosition(videoId, currentPlayPosition);
            } else {
                DataSet.insertVideoPosition(videoId, currentPlayPosition);
            }

//            lastPlayPosition = DataSet.getVideoPosition(videoId);
//            Log.e("CCVideoView", lastPlayPosition + "");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPrepared) {
            // 刷新界面
            rl_menu.setVisibility(INVISIBLE);
            setLayoutVisibility(View.GONE, false);
            setLayoutVisibility(View.VISIBLE, true);
        }

        lockView.setSelected(false);
        if (activity != null) {
            if (!isPortrait()) {//全屏隐藏title
                ScreenUtils.showFullScreen(activity, true);
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).mSup_main.setDragView(mainActivity.mFloatwindow_bottom);
                }
            } else {
                ScreenUtils.showFullScreen(activity, false);
                if (activity instanceof MainActivity) {
                    if (fragment != null && ((MediaPlayFragment) fragment).playerLayout != null)
                        ((MainActivity) activity).mSup_main.setDragView(((MediaPlayFragment) fragment).playerLayout);
                }
            }
        }
        setSurfaceViewLayout();
    }


    /**
     * 设置屏幕方向
     */
    public void setScreenorientation() {
        int width = player.getVideoWidth();
        int height = player.getVideoHeight();
        int orientation = width > height ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    }

    /**
     * CCView监听回调
     */
    public interface OnCCViewListener {
        void onCompletion();

        void downloadPay();

        void paySelectPart(WrappedLessonPart part);

        //1 播放， 0 没有播放 可以增加信息状态
        void payStatus(int state);

        //播放异常
        void playError();
    }

    /**
     * 底部状态栏显示状态回调
     *
     * @param visible
     */
    public void statusCallBack(int visible) {
        Log.e("tag", "statusCallBack: " + visible);
    }

    public void setOnCCViewlisten(OnCCViewListener onCCViewlisten) {
        this.onCCViewlisten = onCCViewlisten;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLocalPlay(boolean localPlay) {
        isLocalPlay = localPlay;
    }

    public void setSinglePlay(boolean isSinglePlay) {
        this.isSinglePlay = isSinglePlay;
    }

//    public void setmPart(List<LessonPartEntity> mPart) {
//        this.mPart = mPart;
//    }

    public void setCurrentPlayPart(VideoParams wrappedLessonPart) {
        this.currentPlayPart = wrappedLessonPart;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public boolean isDrag() {
        return isDrag;
    }

    public void setDrag(boolean drag) {
        isDrag = drag;
    }
}
