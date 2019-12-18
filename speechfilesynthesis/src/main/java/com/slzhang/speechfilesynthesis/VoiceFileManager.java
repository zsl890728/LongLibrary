package com.slzhang.speechfilesynthesis;

import android.util.Log;

import com.slzhang.sqllitepal.MyDBManager;
import com.slzhang.sqllitepal.VoiceData;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取对应字符串的对应  音频文件 路径 列表
 * 避免多次读取数据库 影响效率，这里一次性读取
 */
public class VoiceFileManager {

    private static VoiceFileManager voiceFileManager;
    private List<VoiceData> lsVoiceData;

    public static VoiceFileManager getInstance() {
        if (voiceFileManager == null) {
            voiceFileManager = new VoiceFileManager();
        }
        return voiceFileManager;
    }

    /**
     * 获取指定字符串 对应的音频路径列表
     *
     * @param content
     * @return
     */
    public List<String> getVoicePath(String content) {
        if (lsVoiceData == null) {
            lsVoiceData = MyDBManager.getInstance().getAllVoiceData();
        }
        List<String> lsPath = new ArrayList<>();

        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if(!getPathByChar(ch).equals("")){
                lsPath.add(getPathByChar(ch));
            }else{
                Log.e("----","未查询到字符"+ch+"对应音频文件");
            }
        }
        return lsPath;
    }

    private String getPathByChar(char c) {
        //通过数据库查询 对应字符的 文件路径
        for(VoiceData voiceData:lsVoiceData){
            if(voiceData.getCh().equals(c+"")){
                return voiceData.getPath();
            }
        }
        return "";
    }

    /**
     * 更新音频文件
     */
    public void updateVoiceData() {

    }

}
