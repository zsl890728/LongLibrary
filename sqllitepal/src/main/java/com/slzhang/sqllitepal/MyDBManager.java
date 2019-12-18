package com.slzhang.sqllitepal;

import org.litepal.LitePal;

import java.util.List;

/**
 * 数据库工具类
 */
public class MyDBManager {
    private static MyDBManager myDBManager;

    public static MyDBManager getInstance() {
        if (myDBManager != null) {
            myDBManager = new MyDBManager();
        }
        return myDBManager;
    }

    /**
     * 添加音频文件数据
     *
     * @param id
     * @param name
     * @param path
     * @param ch
     * @param version
     */
    public void addVoiceData(int id, String name, String path, String ch, int version) {
        VoiceData voiceData = new VoiceData();
        voiceData.setId(id);
        voiceData.setName(name);
        voiceData.setPath(path);
        voiceData.setCh(ch+"");
        voiceData.setVersion(version);
        voiceData.save();
    }

    /**
     * 修改音频文件数据
     *
     * @param name
     * @param path
     * @param ch
     * @param version
     */
    public void updateVoiceData(String name, String path, char ch, int version) {
        VoiceData voiceData = new VoiceData();
        voiceData.setName(name);
        voiceData.setPath(path);
        voiceData.setVersion(version);
        voiceData.updateAll("ch = ?", ch + "");
    }

    /**
     * 获取所有音频文件信息
     * @return
     */
    public List<VoiceData> getAllVoiceData() {
        List<VoiceData> lsVoice = LitePal.findAll(VoiceData.class);
        return lsVoice;
    }
}
