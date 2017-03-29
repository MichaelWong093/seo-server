package com.berchina.seo.server.provider.utils;

/**
 * @Package com.berchina.seo.server.provider.utils
 * @Description: TODO ( 枚举类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:59
 * @Version V1.0
 */
public enum EnumUtils {

    SEO_HOTWORDS(Constants.SEO_HOT_WORDS),

    SEO_OTHER(Constants.SEO_OTHE),

    SEO_SHOP(Constants.SEO_SHOP_),

    SEO_GOODS(Constants.SEO_GOODS_),

    SEO_GOODSNAME(Constants.INDEX_GOODSNAME),

    SEO_TYPE(Constants.SEO_TYPE),

    SEO_TERMINAL(Constants.SEO_TERMINAL),

    SEO_LONGITUDE(Constants.SEO_LONGITUDE),

    SEO_LATITUDE(Constants.SEO_LATITUDE),

    SEO_ATTRIBUTE(Constants.SEO_ATTRIBUTE),

    SEO_SORT(Constants.SEO_SORT),

    SEO_ROWS(Constants.SEO_ROWS),

    SEO_START(Constants.SEO_START),

    SEO_RULE(Constants.SEO_RULE),

    SEO_CHANNEL(Constants.SEO_CHANNEL),

    SEO_CATEGORY(Constants.SEO_CATEGORYS_SHOP_),

    SEO_BRAND(Constants.SEO_BRAND),

    SEO_PAGE_SIZE(Constants.SEO_PAGE_SIZE),

    SEO_CURRENT_PAGE(Constants.SEO_CURRENT_PAGE);


    private final String fieldName;

    EnumUtils(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName() {
        return fieldName;
    }
}
