package com.hxf.seckill.service;

import com.hxf.seckill.dto.Exposer;
import com.hxf.seckill.dto.SeckillExecution;
import com.hxf.seckill.entity.Seckill;
import com.hxf.seckill.exception.RepeatKillExecption;
import com.hxf.seckill.exception.SeckillCloseExecption;
import com.hxf.seckill.exception.SeckillException;

import java.util.List;

/**
 * 高并发秒杀service接口：站在“使用者”角度设计接口
 * 三个方面：方法定义粒度、参数（简单，简洁）、返回类型（return 类型/异常）
 * Created by Administrator on 2017/12/5.
 */

public interface SeckillService {

    /**
     * 查询全部的秒杀产品
     *
     * @return 查询成功返回LIst集合，否则返回null集合
     */
    public List<Seckill> getSeckillList();

    /**
     * 查询一条秒杀数据
     *
     * @param seckillId 秒杀商品id
     * @return 查询成功返回Seckill 对象，否则返回null对象
     */
    public Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     *
     * @param seckillId 秒杀开启返回接口地址，否则返回系统时间和秒杀时间
     */
    public Exposer exportSeckillUrl(long seckillId);


    /**
     * 执行秒杀操作
     *
     * @param seckillId 商品id
     * @param userPhone 用户电话
     * @param md5       md5加密数据
     * @return 成功返回SeckillExection对象
     * @throws SeckillException      秒杀相关业务异常
     * @throws RepeatKillExecption   重复秒杀异常
     * @throws SeckillCloseExecption 秒杀关闭异常
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5, int state) throws SeckillException, RepeatKillExecption, SeckillCloseExecption;

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5, int state) throws SeckillException, RepeatKillExecption, SeckillCloseExecption;
}
