package com.hxf.seckill.enums;

/**
 * 使用枚举表示常量数据字段
 * Created by Administrator on 2017/12/8.
 */
public enum SeckillStateEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KELL(-1,"重复秒杀"),
    INSERT_EROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改"),
    ;
    private int state;
    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
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

    public static SeckillStateEnum StateOf(int index){
        for(SeckillStateEnum state : values()){
            if(state.getState() == index){
                return state;
            }
        }
        return null;
    }

    public static String getInfoBySate(int index){
        for(SeckillStateEnum state : values()){
            if(state.getState() == index){
                return state.getStateInfo();
            }
        }
        return "";
    }
}
