package com.wang17.myclock.database;

import java.util.UUID;

public class Stock {
    private UUID id;
    private String code;
    private String name;
    private double cost;
    private int type; // 0是股票，1是期货多单，2是期货空单
    private double amount;
    private String exchange;

    public Stock() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExchange() {
        return exchange==null?"":exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    /**
     * 0：股票；1：期货多单；-1：期货空单
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * 0：股票；1：期货多单；-1：期货空单
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }
}
