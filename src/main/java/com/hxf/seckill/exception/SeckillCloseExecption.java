package com.hxf.seckill.exception;

/**
 * 秒杀关闭异常,秒杀结束
 * Created by Administrator on 2017/12/5.
 */
public class SeckillCloseExecption extends RuntimeException {
    public SeckillCloseExecption() {
    }

    public SeckillCloseExecption(String message) {
        super(message);
    }

    public SeckillCloseExecption(String message, Throwable cause) {
        super(message, cause);
    }
}

