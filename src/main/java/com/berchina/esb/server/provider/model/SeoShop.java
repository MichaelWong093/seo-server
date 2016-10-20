package com.berchina.esb.server.provider.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @Package com.berchina.esb.server.provider.model
 * @Description: TODO ( 店铺实体类 )
 * @Author xum
 * @Date 16/9/7 下午3:23
 * @Version V1.0
 */
public class SeoShop implements Serializable {

    private static final long serialVersionUID = -3461323389946027002L;

    /**
     * 商铺id
     */
    private String shopid;
    /**
     * 商铺名称
     */
    private String shopName;
    /**
     * 卖家昵称
     */
    private String trueName;
    /**
     * 商铺等级
     */
    private String shopLevelId;
    /**
     * 商铺来源
     */
    private String source;
    /**
     * 商铺logo
     */
    private String logo;
    /**
     * 商铺地址
     */
    private String address;
    /**
     * 经营范围
     */
    private String businessarea;
    /**
     * 总销量
     */
    private String totalSales;

    /**
     * 营业时间
     */
    private String hours;

    /**
     * 商品集合
     */
    private List<SeoGoods> goodsList = new LinkedList<SeoGoods>();

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getShopLevelId() {
        return shopLevelId;
    }

    public void setShopLevelId(String shopLevelId) {
        this.shopLevelId = shopLevelId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessarea() {
        return businessarea;
    }

    public void setBusinessarea(String businessarea) {
        this.businessarea = businessarea;
    }

    public String getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(String totalSales) {
        this.totalSales = totalSales;
    }

    public List<SeoGoods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<SeoGoods> goodsList) {
        this.goodsList = goodsList;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
}
