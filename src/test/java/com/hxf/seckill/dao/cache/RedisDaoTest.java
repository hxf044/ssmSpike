package com.hxf.seckill.dao.cache;

import com.hxf.seckill.dao.SeckillDao;
import com.hxf.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//配置Spring文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest{
    private long id = 1001;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    @Test
    public void testSeckill() throws Exception {
        Seckill seckill = redisDao.get(id);
        if(seckill == null){
            Seckill seckill1 = seckillDao.queryById(id);
            if(seckill1 != null){
              String rersult =   redisDao.put(seckill1);
              System.out.println("插入结果: "+rersult);
              seckill = redisDao.get(id);

            }
        }
        System.out.println(seckill);
    }


}