package com.hxf.seckill.exception;

/**
 * 重复秒杀异常(运行期异常)
 * Created by Administrator on 2017/12/5.
 */
public class RepeatKillExecption extends  RuntimeException {
    public RepeatKillExecption() {
    }

    public RepeatKillExecption(String message) {
        super(message);
    }

    public RepeatKillExecption(String message, Throwable cause) {
        super(message, cause);
    }
}
