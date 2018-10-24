package com.hxf.seckill.dto;

import com.hxf.seckill.entity.SuccessKilled;
import com.hxf.seckill.enums.SeckillStateEnum;

/**
 * Created by Administrator on 2017/12/5.
 */
public class SeckillExecution {
    //商品id
    private long secckillId;
    //状态
    private int state;
    //状态解释
    private String stateInfo;
    //秒杀成功对象
    private SuccessKilled successKilled;

    public SeckillExecution(long secckillId, SeckillStateEnum seckillStateEnum, SuccessKilled successKilled) {
        this.secckillId = secckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SeckillExecution(long secckillId, SeckillStateEnum seckillStateEnum) {
        this.secckillId = secckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
    }

    public long getSecckillId() {
        return secckillId;
    }

    public void setSecckillId(long secckillId) {
        this.secckillId = secckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "secckillId=" + secckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
