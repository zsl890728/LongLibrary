package com.slzhang.speechfilesynthesis;

import android.util.Log;

public class SpeechUtils {
    private static final String TAG = "--SpeechUtils--";
    private static SpeechUtils speechUtils;
    private int speekTimes=1;

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
        return speechUtils;
    }
}
