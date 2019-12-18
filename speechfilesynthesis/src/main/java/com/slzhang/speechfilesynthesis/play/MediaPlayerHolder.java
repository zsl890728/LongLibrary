package com.slzhang.speechfilesynthesis.play;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MediaPlayerHolder implements PlayerAdapterListener{
    public static int PLAYSTATUS0=0;//正在播放
    public static int PLAYSTATUS1=1;//暂停播放
    public static int PLAYSTATUS2=2;//重置
    public static int PLAYSTATUS3=3;//播放完成
    public static int PLAYSTATUS4=4;//媒体流装载完成
    public static int PLAYSTATUS5=5;//媒体流加载中
    public static int PLAYSTATUSD1=-1;//错误

    public int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;
    private String TAG = MediaPlayerHolder.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private ScheduledExecutorService mExecutor;//开启线程
    private PlaybackInfoListener mPlaybackInfoListener;
    private Runnable mSeekbarPositionUpdateTask;
    private String musiUrl;//音乐地址，可以是本地的音乐，可以是网络的音乐


    public void setmPlaybackInfoListener(PlaybackInfoListener mPlaybackInfoListener) {
        this.mPlaybackInfoListener = mPlaybackInfoListener;
    }


    /**
     * @date: 2019/6/21 0021
     * @author: gaoxiaoxiong
     * @description:初始化MediaPlayer
     **/
    private void initializeMediaPlayer(){
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //注册，播放完成后的监听
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopUpdatingCallbackWithPosition(true);
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onStateChanged(PLAYSTATUS3);
                        mPlaybackInfoListener.onPlaybackCompleted();
                    }
                }
            });

            //监听媒体流是否装载完成
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    medisaPreparedCompled();
                }
            });

            /**
             * @date: 2019/6/21 0021
             * @author: gaoxiaoxiong
             * @description:监听媒体错误信息
             **/
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mPlaybackInfoListener!=null){
                        mPlaybackInfoListener.onStateChanged(PLAYSTATUSD1);
                    }
                    Log.d(TAG, "OnError - Error code: " + what + " Extra code: " + extra);
                    switch (what) {
                        case -1004:
                            Log.d(TAG, "MEDIA_ERROR_IO");
                            break;
                        case -1007:
                            Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                            break;
                        case 200:
                            Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                            break;
                        case 100:
                            Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                            break;
                        case -110:
                            Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                            break;
                        case -1010:
                            Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                            break;
                    }
                    switch (extra) {
                        case 800:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                            break;
                        case 702:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                            break;
                        case 701:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 802:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 801:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                            break;
                        case 3:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                            break;
                        case 700:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                            break;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * @date: 2019/6/24 0024
     * @author: gaoxiaoxiong
     * @description: 加载媒体资源
     **/
    @Override
    public void loadMedia(String musiUrl) {
        if (musiUrl==null||musiUrl.equals("")){
            Log.i(TAG,"地址为空");
            return;
        }

        if (mPlaybackInfoListener!=null){
            mPlaybackInfoListener.onStateChanged(PLAYSTATUS5);
        }

        this.musiUrl = musiUrl;
        initializeMediaPlayer();
        try {
            mMediaPlayer.reset();//防止再次添加进来出现崩溃信息
            mMediaPlayer.setDataSource(musiUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @date: 2019/6/24 0024
     * @author: gaoxiaoxiong
     * @description:释放媒体资源
     **/
    @Override
    public void release() {
        if (mMediaPlayer != null) {
            stopUpdatingCallbackWithPosition(false);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * @date: 2019/6/24 0024
     * @author: gaoxiaoxiong
     * @description:判断是否正在播放
     **/
    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * @date: 2019/6/24 0024
     * @author: gaoxiaoxiong
     * @description:播放开始
     **/
    @Override
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS0);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    /**
     * @date: 2019/6/21 0021
     * @author: gaoxiaoxiong
     * @description:开启线程，获取当前播放的进度
     **/
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }

        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            loadMedia(musiUrl);
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS2);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    /**
     * @date: 2019/6/24 0024
     * @author: gaoxiaoxiong
     * @description:暂停
     **/
    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PLAYSTATUS1);
            }
        }
    }

    /**
     * @date: 2019/6/21 0021
     * @author: gaoxiaoxiong
     * @description:更新当前的进度
     **/
    private void updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    /**
     * @date: 2019/6/24 0024
     * @author: gaoxiaoxiong
     * @description:加载完成回调
     **/
    @Override
    public void medisaPreparedCompled() {
        int duration = mMediaPlayer.getDuration();//获取总时长
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
            mPlaybackInfoListener.onStateChanged(PLAYSTATUS4);
        }
    }

    /**
     * @date: 2019/6/21 0021
     * @author: gaoxiaoxiong
     * @description:滑动播放到某个位置
     **/
    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }


    /**
     * @date: 2019/6/21 0021
     * @author: gaoxiaoxiong
     * @description:播放完成回调监听
     **/
    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }
}
