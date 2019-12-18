package com.slzhang.sqllitepal;

import org.litepal.crud.LitePalSupport;

public class VoiceData extends LitePalSupport {
    int id;
    String name;
    String path;
    String ch;
    //根据需要，判定是否需要每个音频文件 添加 版本，还是整体版本控制
    int version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
