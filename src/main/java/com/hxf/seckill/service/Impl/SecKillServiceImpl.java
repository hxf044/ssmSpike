package com.hxf.seckill.service.Impl;

import com.hxf.seckill.dao.SeckillDao;
import com.hxf.seckill.dao.SuccessKilledDao;
import com.hxf.seckill.dao.cache.RedisDao;
import com.hxf.seckill.dto.Exposer;
import com.hxf.seckill.dto.SeckillExecution;
import com.hxf.seckill.entity.Seckill;
import com.hxf.seckill.entity.SuccessKilled;
import com.hxf.seckill.enums.SeckillStateEnum;
import com.hxf.seckill.exception.RepeatKillExecption;
import com.hxf.seckill.exception.SeckillCloseExecption;
import com.hxf.seckill.exception.SeckillException;
import com.hxf.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/6.，使用 Ctrl+Q 就可以查看方法的注解
 */
//@Component表示所有组件，它会字段匹配，@Service 、@Dao、@Conroller
@Service
public class SecKillServiceImpl implements SeckillService {
    /*日志*/
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /*dao层接口*/
    //注入service依赖，@Autowired、@Resource、@Inject
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;
    //md5的字符串，用混淆来加密MD5
    private final String slat = "akhafkjh3i893398afakhhxf#**#(@@#*ih39i392ahjak@^#^#^##&hdhkhfkfh";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 10);
    }

    /**
     * 优化,采用redis缓存，提高数据提取熟读，先去redis中查找，如果不存在就去查询数据库，然后保存到redis中
     * @param seckillId 秒杀商品id
     * @return 查到结果返回数据，否则返回null
     */
    public Seckill getById(long seckillId) {
        Seckill seckill = redisDao.get(seckillId);
        if(seckill == null){
            seckill = seckillDao.queryById(seckillId);
            //1.判断秒杀商品的库是否有这条数据，没有返回false
            if (seckill != null) {
                redisDao.put(seckill);
            }
        }
        return seckill;
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //优化，缓存优化
        Seckill  seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        //查看秒杀是否开启
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //当前系统时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 使用注解控制事物方法的优点：
     * 1.开发团队达成一致约定，明确标注事物方法的编程风格
     * 2.保证事物方法的时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，或者只读操作不需要事物控制
     * 4.先插入后修改，可以避免重复秒杀的执行插入和修改操作，这种写就可以判断插入是否成功，不成功就不执行修改，速度上优化很多
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5, int state) throws SeckillException, RepeatKillExecption, SeckillCloseExecption {
        //1.先判断MD5是否正确
        if (md5 == null || !md5.equals(getMD5(seckillId))) {//如果MD5不存在，或者不存在，就直接抛异常
            throw new SeckillException("没有该商品");
        }
        try {
            //1.插入秒杀数据
            int insertCount = successKilledDao.insertSuccessSeckilled(seckillId, userPhone, state);
            if (insertCount <= 0) {//如果插入数据失败，说明数据库已经有了这条数据，直接抛出重复秒杀异常
                throw new RepeatKillExecption("重复秒杀");
            } else {
                Date nowTime = new Date();
                //2.减库存，减库存行级锁
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {//如果减库存失败，直接抛出异常
                    throw new SeckillCloseExecption("秒杀结束");
                } else {   //3.获取秒杀数据，然后返回数据
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseExecption e1) {
            throw e1;
        } catch (RepeatKillExecption e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所以编译期异常，转化为运行期异常
            throw new SeckillException("秒杀异常" + e.getMessage());
        }
    }

    /**
     * 执行秒杀的存储过程
     * @param seckillId 秒杀商品id
     * @param userPhone 用户电话
     * @param md5  加密
     * @param state  状态
     * @return  返回对应数据
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5, int state){
        //1.先判断MD5是否正确
        if (md5 == null || !md5.equals(getMD5(seckillId))) {//如果MD5不存在，或者不存在，就直接抛异常
            throw new SeckillException("没有该商品");
        }
        Date nowTime = new Date();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("userPhone",userPhone);
        map.put("killTime",nowTime);
        map.put("state",state);
        map.put("result",null);
        try {
            seckillDao.killSeckillProcedure(map);
           int result =   MapUtils.getInteger(map,"result",-2);
           if( result == 1){
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return   new SeckillExecution(seckillId,SeckillStateEnum.StateOf(result),successKilled) ;
           }else{
                return   new SeckillExecution(seckillId,SeckillStateEnum.StateOf(result)) ;
           }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            //所以编译期异常，转化为运行期异常
            throw new SeckillException("秒杀异常" + e.getMessage());
        }
    }

    /**
     * 生成MD5加密
     *
     * @param seckillId 秒杀商品id
     * @return 返回MD5字符串
     */
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        //使用Spring工具类生成MD5字符串
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
