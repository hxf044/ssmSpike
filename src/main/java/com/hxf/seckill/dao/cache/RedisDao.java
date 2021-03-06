package com.hxf.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.hxf.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Rdies 接口
 */
public class RedisDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;

    public RedisDao(String ip,int prot){
        this.jedisPool = new JedisPool(ip,prot) ;
    }
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    public Seckill get(long seckillId){
        try {
            Jedis  jedis = this.jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId ;
                /*没有实现内部序列化和反序列化
                * get -> byte() -> 反序列化 -> Object(Seckill)
                * */
                //自定义序列化
                // protostuff :pojo
             byte[]  bytes =  jedis.get(key.getBytes());
             if( bytes != null){
                 Seckill seckill = schema.newMessage();
                 ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                //seckill 反序列化
                 return seckill;
             }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.info(e.getMessage(),e);
        }
        return null;
    }

    public String put(Seckill seckill){
        try {
            Jedis  jedis = this.jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout = 60 * 60;//一个小时
               String reslut =  jedis.setex(key.getBytes(),timeout,bytes);
                return reslut;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.info(e.getMessage(),e);
        }
        return null;
    }
}
