package com.scott.eshop.cache.model;

import java.util.Date;

/**
 * 商品信息
 * @ClassName ProductInfo
 * @Description
 * @Author 47980
 * @Date 2020/5/1 20:18
 * @Version V_1.0
 **/
public class ProductInfo {

    private Long id;

    private String name;

    private Double price;

    private String pictureUrl;

    private String specification;

    private String service;

    private String color;

    private String size;

    private Long shopId;

    private String modifiedTime;

    @Override
    public String toString() {
        return "ProductInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", specification='" + specification + '\'' +
                ", service='" + service + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", shopId=" + shopId +
                ", modifiedTime='" + modifiedTime + '\'' +
                '}';
    }

    public ProductInfo(Long id, String name, Double price, String pictureUrl, String specification, String service, String color, String size, Long shopId, String modifiedTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.specification = specification;
        this.service = service;
        this.color = color;
        this.size = size;
        this.shopId = shopId;
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public ProductInfo() {
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
