package com.hxf.seckill.dao;

import com.hxf.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/22.
 */
//秒杀商品接口 利用 Alt+Enter 就可以生成快捷键，可以快速创建JUINT4的TEST类
public interface SeckillDao {
    /**
     * 减秒杀商品库存接口
     *
     * @param seckillId 秒杀商品id
     * @param killTime  时间
     * @return 执行成功，返回影响条数，否则返回0
     */
    public int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 查询一条秒杀商品信息
     *
     * @param seckillId 秒杀商品id
     * @return 查询成功返回实体，否则返回null
     */
    public Seckill queryById(long seckillId);

    /**
     * 分页查询秒杀商品信息
     *
     * @param offset 偏移量
     * @param limit  获取条数
     * @return 查询成功返回对应条数，否则返回null
     */
    //java在传入数据的时候会把queryAll(ioffset, limit)变为queryAll(agr0, agr1)，所以mybatis会找不到我的形参，我们需要加上如下注解才可以
    public List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 执行秒杀的存储过程
     * @param map 传入参数
     */
    public void killSeckillProcedure(Map<String,Object> map) ;
}

