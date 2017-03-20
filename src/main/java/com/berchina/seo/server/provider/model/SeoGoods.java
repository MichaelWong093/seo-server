package com.berchina.seo.server.provider.model;

import java.io.Serializable;

/**
 * @Package com.berchina.seo.server.provider.model
 * @Description: TODO ( 商品展示信息 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 下午9:34
 * @Version V1.0
 */
public class SeoGoods implements Serializable {

    private static final long serialVersionUID = -2122499546044965478L;

    /**
     * 热词名称
     */
    private String hotwords;

    /**
     * 商品价格
     */
    private String prices;

    /**
     * 商品图片
     */
    private String picture;

    /**
     * 商品销量
     */
    private String sales;

    /**
     * 评论
     */
    private String comment;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品来源
     */
    private String source;

    /**
     * 商品 ID
     */
    private String goodsId;

    /**
     * 活动标签
     */
    private String activityLabel;

    /**
     * 商品类型
     */
    private String type;

    /**
     * 积分支付标示
     */
    private int integralflag;

    public int getIntegralflag() {
        return integralflag;
    }

    public void setIntegralflag(int integralflag) {
        this.integralflag = integralflag;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getHotwords() {
        return hotwords;
    }

    public void setHotwords(String hotwords) {
        this.hotwords = hotwords;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SeoGoods [hotwords=" + hotwords + ", prices=" + prices
                + ", picture=" + picture + ", sales=" + sales + ", comment="
                + comment + ", shopName=" + shopName + ", source=" + source
                + ", goodsId=" + goodsId + ", activityLabel=" + activityLabel + "]";
    }

}
