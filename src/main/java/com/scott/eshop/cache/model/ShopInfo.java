package com.scott.eshop.cache.model;

/**
 * @ClassName ShopInfo
 * @Description
 * @Author 47980
 * @Date 2020/5/1 22:49
 * @Version V_1.0
 **/
public class ShopInfo {

    private Long id;

    private String name;

    private Integer level;

    private Double goodCommentRate;

    @Override
    public String toString() {
        return "ShopInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", goodCommentRate=" + goodCommentRate +
                '}';
    }

    public ShopInfo(Long id, String name, Integer level, Double goodCommentRate) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.goodCommentRate = goodCommentRate;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getGoodCommentRate() {
        return goodCommentRate;
    }

    public void setGoodCommentRate(Double goodCommentRate) {
        this.goodCommentRate = goodCommentRate;
    }
}
