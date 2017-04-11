package com.berchina.seo.server.provider.utils;

/**
 * @Package com.berchina.seo.server.provider.utils
 * @Description: TODO ( 物流标示 )
 * @Author rxbyes
 * @Date 2017 上午8:51
 * @Version V1.0
 */
public enum LogisticsEnum {

    WDZT("1", "网店自提"),
    SHSM("2", "送货上门"),
    WLPS("5", "物流配送");

    /**
     * 物流编码
     */
    private String code;

    /**
     * 物流名称
     */
    private String name;

    LogisticsEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
