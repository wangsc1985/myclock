package com.wang17.myclock.model;

public class Commodity {
    public Commodity(String item, String name, int unit, double cose) {
        this.item = item;
        this.name = name;
        this.unit = unit;
        this.cose = cose;
    }

    // 合约
    public String item;
    // 名称
    public String name;
    // 交易单位  比如：1000克/手
    public int unit;
    // 最小变动价位 0.05元/克
    public double cose;
}
