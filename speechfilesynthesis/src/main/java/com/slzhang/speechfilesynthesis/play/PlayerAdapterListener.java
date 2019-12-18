package com.slzhang.speechfilesynthesis.play;

public interface PlayerAdapterListener {
    void loadMedia(String musiUrl);//加载媒体资源

    void release();//释放资源

    boolean isPlaying();//判断是否在播放

    void play();//开始播放

    void reset();//重置

    void pause();//暂停

    void medisaPreparedCompled();//完成媒体流的装载

    void seekTo(int position);//滑动到某个位置
}
