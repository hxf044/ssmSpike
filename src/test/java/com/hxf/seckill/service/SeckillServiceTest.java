package com.hxf.seckill.service;

import ch.qos.logback.classic.spi.LoggerContextListener;
import com.hxf.seckill.dto.Exposer;
import com.hxf.seckill.dto.SeckillExecution;
import com.hxf.seckill.entity.Seckill;
import com.hxf.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/12/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill = {}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);

    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1002;
        long userPhone = 12345678;
        String md5 = getMD5(id);
        SeckillExecution seckillExecution = seckillService.executeSeckill(id,userPhone,md5,0);
        logger.info("seckillExecution={}",seckillExecution);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/akhafkjh3i893398afakhhxf#**#(@@#*ih39i392ahjak@^#^#^##&hdhkhfkfh";
        //使用Spring工具类生成MD5字符串
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

}