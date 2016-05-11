package com.bmi.sharedeal.service.Server;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by Keith on 2015/5/5.
 */
@Table("t_apistatus")
public class ApiStatus {

    @Id
    private int id;

    @Column("apiName")
    private String apiName;          //api名称

    @Column("allCallCnt")
    private int allCallCnt = 0;          //调用次数

    @Column("failCallCnt")
    private int failCallCnt = 0;         //失败调用次数

    @Column("successCallCnt")
    private int successCallCnt = 0;      //成功调用次数

    @Column("minTimeCost")
    private int minTimeCost = 0;         //最小耗时(ms)

    @Column("maxTimeCost")
    private int maxTimeCost = 0;         //最大耗时(ms)

    @Column("averageTimeCost")
    private int averageTimeCost = 0;    //平均耗时(ms)

    @Column("allTimeCost")
    private long allTimeCost = 0;       //总耗时

    public int getId() {
        return id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public int getAllCallCnt() {
        return allCallCnt;
    }

    public void setAllCallCnt(int allCallCnt) {
        this.allCallCnt = allCallCnt;
    }

    public int getFailCallCnt() {
        return failCallCnt;
    }

    public void setFailCallCnt(int failCallCnt) {
        this.failCallCnt = failCallCnt;
    }

    public int getSuccessCallCnt() {
        return successCallCnt;
    }

    public void setSuccessCallCnt(int successCallCnt) {
        this.successCallCnt = successCallCnt;
    }

    public int getMinTimeCost() {
        return minTimeCost;
    }

    public void setMinTimeCost(int minTimeCost) {
        this.minTimeCost = minTimeCost;
    }

    public int getMaxTimeCost() {
        return maxTimeCost;
    }

    public void setMaxTimeCost(int maxTimeCost) {
        this.maxTimeCost = maxTimeCost;
    }

    public int getAverageTimeCost() {
        return averageTimeCost;
    }

    public void setAverageTimeCost(int averageTimeCost) {
        this.averageTimeCost = averageTimeCost;
    }

    public long getAllTimeCost() {
        return allTimeCost;
    }

    public void setAllTimeCost(long allTimeCost) {
        this.allTimeCost = allTimeCost;
    }

    public synchronized void addRecord(boolean isFail, int timeCost) {
        allCallCnt++;
        if (isFail) {
            failCallCnt++;
        } else {
            successCallCnt++;
        }

        //只统计成功调用的耗时情况
        if (!isFail) {
            if (timeCost < minTimeCost || minTimeCost == 0) {
                minTimeCost = timeCost;
            }

            if (timeCost > maxTimeCost) {
                maxTimeCost = timeCost;
            }

            allTimeCost += (long) timeCost;
            averageTimeCost = (int) (allTimeCost / successCallCnt);
        }
    }
}
