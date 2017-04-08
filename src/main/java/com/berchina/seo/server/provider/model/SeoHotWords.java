package com.berchina.seo.server.provider.model;

import java.io.Serializable;

/**
 * @Package com.berchina.seo.server.provider.model
 * @Description: TODO ( )
 * @Author yanhuiqing
 * @Date 2016年9月14日 上午10:24:27
 * @Version V1.0
 */
public class SeoHotWords implements Serializable {

    private static final long serialVersionUID = -3639773043865854740L;
    /**
     * 热词
     */
    private String keyword;
    /**
     * 热词全拼
     */
    private String pinyin;
    /**
     * 热词缩写
     */
    private String abbre;

    /**
     * 出现的次数
     */
    private long frequency;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getAbbre() {
        return abbre;
    }

    public void setAbbre(String abbre) {
        this.abbre = abbre;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "SeoHotWords{" +
                "hotWord='" + keyword + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", abbre='" + abbre + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
