package com.berchina.seo.server.provider.client;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.client.base.Request;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.EnumUtils;
import com.berchina.seo.server.provider.utils.SerialNumber;
import com.berchina.seo.server.provider.utils.StringUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.client
 * @Description: TODO ( 搜索入参 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:33
 * @Version V1.0
 */
public class SeoRequest extends Request implements Serializable {

    private static final long serialVersionUID = 1179463358432194392L;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 品牌
     */
    private String brand;

    /**
     * 排序
     */
    private String sort;

    /**
     * 排序规则
     */
    private String rule;

    /**
     * 类目搜索
     */
    private String category;

    /**
     * 属性
     */
    private String attribute;

    /**
     * @ 特殊字段标记
     * @ multiple condition 简称 mc
     */
    private String other;

    /**
     * 页数
     */
    private String start;

    /**
     * 每页显示条数
     */
    private String rows;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 区域位置,码表字典表示
     */
    private String region;

    /**
     * 经度0°——180°（东行,标注E）0°——180°（西行,标注W）
     */
    private String longitude;

    /**
     * 纬度 0°——90°N、0°——90°S
     */
    private String latitude;

    /**
     * 普通商品搜索类型，0：普通商品，1：特色频道
     */
    private String type;


    public SeoRequest() {
    }

    /**
     * 搜索关键字处理
     *
     * @param request 用户请求信息
     */
    public SeoRequest(HttpServletRequest request) {
        /**
         * 请求业务时间
         */
        super.setTime(format.format(new Date()));

        /**
         * 业务序列号
         */
        super.setSerialNum(SerialNumber.getInstance().generaterNextNumber());

        /**
         * 请求数据转化为 map 对象
         */
        Map map = JSON.parseObject(StringUtil.requestDataConvert(request), Map.class);

        /**
         * 渠道实例
         */
        super.setInstance(StringUtil
                .setBussinesRequestMethod(StringUtil.StringConvert(map.get(EnumUtils.SEO_CHANNEL.getName()))));

        /**
         * 全站搜索,处理请求的关键词
         */
        String hotWords = StringUtil.StringConvert(map.get(EnumUtils.SEO_HOTWORDS.getName()));

        if (!StringUtils.isEmpty(hotWords)) {
            super.setHotwords(hotWords);
        }

        /**
         * Collection 实例名称
         */
        super.setChannel(StringUtil.getSolrInstance(StringUtil.StringConvert(map.get(EnumUtils.SEO_CHANNEL.getName()))));

        /**
         * 商品属性
         */
        this.setAttribute(StringUtil.StringConvert(map.get(EnumUtils.SEO_ATTRIBUTE.getName())));

        /**
         * 品牌
         */
        this.setBrand(StringUtil.StringConvert(map.get(EnumUtils.SEO_BRAND.getName())));

        /**
         * 类目
         */
        this.setCategory(StringUtil.StringConvert(map.get(EnumUtils.SEO_CATEGORY.getName())));

        /**
         * 排序
         */
        this.setSort(StringUtil.StringConvert(map.get(EnumUtils.SEO_SORT.getName())));

        /**
         * 其他
         */
        this.setOther(StringUtil.StringConvert(map.get(EnumUtils.SEO_OTHER.getName())));

        /**
         * 全站搜索区分商品来源，如：特色频道
         */
        this.setType(StringUtil.StringConvert(map.get(EnumUtils.SEO_TYPE.getName())));

        /**
         * 分页结束条数
         */
        this.setRows(StringUtil.StringConvert(map.get(EnumUtils.SEO_ROWS.getName())));

        /**
         * 分页起始条数
         */
        this.setStart(StringUtil.StringConvert(map.get(EnumUtils.SEO_START.getName())));

        /**
         * 排序规则
         */
        this.setRule(StringUtil.StringConvert(map.get(EnumUtils.SEO_RULE.getName())));

        /**
         * 设备来源
         */
        this.setTerminal(StringUtil.StringConvert(map.get(EnumUtils.SEO_TERMINAL.getName())));

        /**
         * 经度
         */
        this.setLongitude(StringUtil.StringConvert(map.get(EnumUtils.SEO_LONGITUDE.getName())));

        /**
         * 纬度
         */
        this.setLatitude(StringUtil.StringConvert(map.get(EnumUtils.SEO_LATITUDE.getName())));
    }

    public String getStart() {
        if (StringUtils.isEmpty(this.start)) {
            this.start = StringUtil.StringConvert(Constants.ZERO);
        }
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getRows() {
        if (StringUtils.isEmpty(this.rows)) {
            this.rows = StringUtil.StringConvert(Constants.THIRTH);
        }
        return rows;
    }

    public String getRule() {
        if (StringUtils.isEmpty(this.rule)) {
            this.rule = Constants.DESC;
        }
        return rule;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (StringUtil.notNull(type)) {
            this.type = type;
        } else {
            this.type = "1";
        }
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "SeoRequest{" +
                "brand='" + brand + '\'' +
                ", sort='" + sort + '\'' +
                ", rule='" + rule + '\'' +
                ", category='" + category + '\'' +
                ", attribute='" + attribute + '\'' +
                ", other='" + other + '\'' +
                ", start='" + start + '\'' +
                ", rows='" + rows + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", region='" + region + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
