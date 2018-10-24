package com.hxf.seckill.dao;

import com.hxf.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/12/1.
 *
 * 配置Spring和junit整合，juint启动时加载springIOC容器
 * spring-test,juint
 * 注意事项：如果用的juint测试，需要先注释
 *
 *  <<dependency>
 <groupId>ch.qos.logback</groupId>
 <artifactId>logback-core</artifactId>
 <version>1.1.7</version>
 </dependency>
 <dependency>
 <groupId>ch.qos.logback</groupId>
 <artifactId>logback-classic</artifactId>
 <version>1.1.7</version>
 </dependency>
 两个包，不然测试会报错
 */
@RunWith(SpringJUnit4ClassRunner.class)
//配置Spring文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {
    //注入DAO实现类依赖
    @Resource
    private SeckillDao seckillDao;
    @Test
    public void queryById() throws Exception {
        long id = 1000;
      Seckill seckill =  seckillDao.queryById(id);
      System.out.println(seckill.getName());
      System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {
        List<Seckill> lists = seckillDao.queryAll(0,10);
        for(Seckill obj:lists){
            System.out.println(obj);
        }

    }
    @Test
    public void reduceNumber() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2017-11-22 02:02:02");
       int updateConut =  seckillDao.reduceNumber(1000L,date);
        System.out.println("成功条数："+updateConut);
    }



}