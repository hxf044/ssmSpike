package com.hxf.seckill.dao;

import com.hxf.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/11/22.
 */
public interface SuccessKilledDao {
    /**
     * 插入购买明细，可以过滤重复插入
     *
     * @param seckillId 秒杀商品
     * @param userPhone 用户电话
     * @return 添加成功返回成功条数，否则返回0
     */
    public int insertSuccessSeckilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone,@Param("state") int state);

    /**
     * 根据id查询SuccessKilled，并携带秒杀产品对象
     *
     * @param SeckillId 秒杀商品id
     * @return 查询成功返回SuccessKilled实体，否则返回null
     */
    public SuccessKilled queryByIdWithSeckill(@Param("seckillId") long SeckillId, @Param("userPhone") long userPhone);
}
