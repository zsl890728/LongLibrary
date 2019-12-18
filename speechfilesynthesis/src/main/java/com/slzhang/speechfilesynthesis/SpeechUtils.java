package com.slzhang.speechfilesynthesis;

import android.util.Log;

import java.util.List;

/**
 * 文字转语音
 * 工具入口
 */
public class SpeechUtils {
    private static final String TAG = "--SpeechUtils--";
    private static SpeechUtils speechUtils;
    private int speekTimes=1;
    private List<String> lsPath;

    public static SpeechUtils getInstance() {
        if (speechUtils == null) {
            speechUtils = new SpeechUtils();
        }
        return speechUtils;
    }

    /**
     * 播放次数
     * @param times
     */
    public SpeechUtils speekTimes(int times){
        speekTimes=times;
        return speechUtils;
    }

    public SpeechUtils content(String content){
        if(content==null||content.equals("")){
            Log.e(TAG,"content==null");
        }
        lsPath=VoiceFileManager.getInstance().getVoicePath(content);
        return speechUtils;
    }

    public void play(){
        PlayManager.getInstance().play(speekTimes, lsPath, new PlayManager.PlayManagerListener() {
            @Override
            public void playComplete() {

            }
        });
    }

}
