package com.slzhang.speechfilesynthesis;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取对应字符串的对应  音频文件 路径 列表
 */
public class VoiceFileManager {

    public VoiceFileManager(String content){

    }

    public List<String> getVoicePath(String content){
        List<String> lsPath=new ArrayList<>();

        for(int i=0;i<content.length();i++){
            char ch=content.charAt(i);

        }
        return lsPath;
    }

    public String getPathByChar(char c){
        //通过数据库查询 对应字符的 文件路径

        return "";
    }
}
