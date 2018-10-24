--- 数据库初始化脚本 2017-11-22

---创建数据库
CREATE DATABASE seckill;
--使用数据库
user seckill;
--创建秒杀库存表
CREATE TABLE  seckill (
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品id',
`name` VARCHAR(120) NOT NULL COMMENT '商品名称',
`number` INT NOT NULL COMMENT '库存数量',
`start_time` TIMESTAMP  NOT NULL COMMENT '秒杀开始时间',
`end_time` TIMESTAMP  NOT NULL COMMENT '秒杀结束时间',
`create_time` TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY(seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表'
---初始化数据
INSERT  INTO
  seckill(name,number,start_time,end_time)
VALUES
  ('3000元秒杀价iphone8','100','2017-11-22 00:00:00','2017-11-23 00:00:00'),
  ('2000元秒杀价iphone7Pules','200','2017-11-22 00:00:00','2017-11-23 00:00:00'),
  ('5000元秒杀价iphoneX','300','2017-11-22 00:00:00','2017-11-23 00:00:00'),
  ('4000元秒杀价iphone8s','400','2017-11-22 00:00:00','2017-11-23 00:00:00'),
  ('2000元秒杀价iphone7','500','2017-11-22 00:00:00','2017-11-23 00:00:00')

--秒杀成功明细表
--用户登陆认证相关的信息
CREATE TABLE success_killed(
  `seckill_id` bigint NOT NULL  COMMENT '秒杀商品id',
  `user_phone` bigint NOT NULL  COMMENT '用户手机号',
  `state` tinyint NOT NULL DEFAULT  -1 COMMENT '状态 -1表示无效，0 成功，1 已支付，2,已发货，3 已收货',
  `create_time` TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY  KEY(seckill_id,user_phone),/*联合主键*/
  key idx_create_time(create_time)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='秒杀库存表'
--链接数据库控制台
--创建秒杀存储过程
DELIMITER $$
CREATE PROCEDURE execute_seckill_Bf(
		IN v_seckill_id BIGINT,
		IN v_phone BIGINT,
		IN v_state INT, IN v_kill_time TIMESTAMP,
		OUT r_result INT
)
BEGIN
	DECLARE save_count INT DEFAULT 0 ;
	START TRANSACTION;
  insert IGNORE INTO success_killed(seckill_id, user_phone , create_time,state )
		VALUES (v_seckill_id ,v_phone ,v_kill_time ,v_state );
  SELECT ROW_COUNT() into save_count ;
	IF (save_count = 0 ) THEN
			ROLLBACK ;
			SET r_result = -1;
	ELSEIF(save_count < 0 ) THEN
			ROLLBACK;
			SET r_result = -2;
	ELSE
			UPDATE seckill
				SET number = number -1
				WHERE
					seckill_id = v_seckill_id
				AND start_time < v_kill_time
				AND end_time > v_kill_time
				AND number > 0 ;
		  SELECT ROW_COUNT() INTO save_count;
			IF(save_count = 0) THEN
					ROLLBACK;
					SET r_result = 0;
		  ELSEIF(save_count < 0) THEN
					ROLLBACK;
					SET r_result = -2;
			ELSE
					COMMIT;
					SET r_result = 1;
			END IF;
	 END IF;
END
$$

