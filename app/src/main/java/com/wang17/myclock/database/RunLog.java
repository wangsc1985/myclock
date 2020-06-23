package com.wang17.myclock.database;

import com.wang17.myclock.model.DateTime;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2016/10/28.
 */

public class RunLog {
    private UUID id;
    private DateTime runTime;
    private String tag;
    private String item;
    private String message;
    private long updateTime=1;
    private long syncTime=1;
    private int status=1;// 值为-1，说明此数据已删除。

    public RunLog(UUID id) {
        this.id = id;
    }

    public RunLog(String item, String message) {
        this.id = UUID.randomUUID();
        this.runTime = new DateTime();
        this.tag = new DateTime(runTime.getTimeInMillis()).toLongDateTimeString();
        this.item = item;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DateTime getRunTime() {
        return runTime;
    }

    public void setRunTime(DateTime runTime) {
        this.runTime = runTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
