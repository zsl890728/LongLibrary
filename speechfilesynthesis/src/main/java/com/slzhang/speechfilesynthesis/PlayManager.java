package com.slzhang.speechfilesynthesis;

import com.slzhang.speechfilesynthesis.play.MediaPlayerHolder;
import com.slzhang.speechfilesynthesis.play.PlaybackInfoListener;

import java.util.List;

/**
 * 播放工具
 */
public class PlayManager {
    private static PlayManager playManager;

    public static PlayManager getInstance() {
        if (playManager == null) {
            playManager = new PlayManager();
        }
        return playManager;
    }

    public void play(int times,List<String> lsPath,PlayManagerListener playManagerListener) {
        playOnce(times,0,lsPath,playManagerListener);
    }

    public void playOnce(final int times,final int index,final List<String> lsPath,final PlayManagerListener playManagerListener){
        MediaPlayerHolder mediaPlayerIngHolder = new MediaPlayerHolder();
        mediaPlayerIngHolder.setmPlaybackInfoListener(new PlaybackInfoListener() {
            @Override
            public void onDurationChanged(int duration) {

            }

            @Override
            public void onPositionChanged(int position) {

            }

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onPlaybackCompleted() {//播放完成
                if(index+1<lsPath.size()){
                    playOnce(times,index+1,lsPath,playManagerListener);
                }else{
                    if(times-1==0){//全部次数播放完成
                        if(playManagerListener!=null){
                            playManagerListener.playComplete();
                        }
                    }else{
                        playOnce(times-1,index+1,lsPath,playManagerListener);
                    }
                }

            }
        });
        mediaPlayerIngHolder.loadMedia(lsPath.get(index));//加载音乐，不是属于播放的状态
        mediaPlayerIngHolder.play();//开始播放，注意：需要等到onStateChanged == PLAYSTATUS4才可以调用这个方法
    }

    public interface PlayManagerListener{
        void playComplete();
    }
}
