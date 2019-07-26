package com.voucher.manage2.redis;

import com.voucher.manage.tools.MyTestUtil;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class JedisUtil1 {

    private static ShardedJedisPool shardedJedisPool;

    //public ShardedJedisPool getShardedJedisPool() {
    //    return shardedJedisPool;
    //}

    @Autowired
    public void setShardedJedisPool(@Qualifier("shardedJedisPool1")ShardedJedisPool shardedJedisPool) {
        //方法前不能加static,加了无法注入
        JedisUtil1.shardedJedisPool = shardedJedisPool;
       // log.info("shardedJedisPool is set successfully！");
    }


    /**
     * @Title: getJedis
     * @Description: 获取shardedJedis实例
     * @return: ShardedJedis
     */
    public static ShardedJedis getShardedJedis() {
        ShardedJedis shardedJedis = null;
        try {
            if (shardedJedisPool != null) {
                shardedJedis = shardedJedisPool.getResource();
            }
        } catch (Exception e) {
           // log.error("[JedisUtil] get shardedJedis false:" + e);
        }
        return shardedJedis;
    }


    /**
     * @param shardedJedis: void
     * @Title: releaseResource
     * @Description: 释放shardedJedis资源
     */
    public static void releaseResource(ShardedJedis shardedJedis) {
        if (shardedJedis != null && shardedJedisPool != null) {
            // 最新版弃用shardedJedisPool.returnResource，用shardedJedis.close()替代
            shardedJedis.close();
        }
    }

    /**
     * @param key
     * @param value: void
     * @Title: setString
     * @Description: 写入String
     * @lastEditor: SP0010
     * @lastEdit: 2017年11月9日下午3:49:43
     */
    public static void setString(String key, String value) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                //可加上事务
                shardedJedis.set(key, value);
            }

        } catch (Exception e) {
           // log.error("set string into redis error:" + e);
        } finally {
            releaseResource(shardedJedis);
        }
    }

    /**
     * @param key
     * @param map: void
     * @Title: setMap
     * @Description: 写入map
     */
    public static void setMap(String key, Map<String, String> map) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            if (shardedJedis != null && !map.isEmpty()) {
                shardedJedis.hmset(key, map);
            }
        } catch (Exception e) {
           // log.error("set map into redis error:" + e);
        } finally {
            releaseResource(shardedJedis);
        }
    }

    /**
     * @param key
     * @param map: void
     * @Title: setMapPipelined
     * @Description: 批量循环插入时使用
     */
    public static void setMapPipelined(String key, Map<String, String> map) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            if (shardedJedis != null && !map.isEmpty()) {
                shardedJedis.pipelined().hmset(key, map);
            }
        } catch (Exception e) {
            //log.error("set map into redis error:" + e);
        } finally {
            releaseResource(shardedJedis);
        }
    }

    /**
     * @Title: getString
     * @Description: 获取String值
     * @param: key
     * @return: String
     */
    public static String getString(String key) {
        ShardedJedis shardedJedis = getShardedJedis();
        String valueString;
        try {
            if (shardedJedis == null) {
                valueString = null;
            } else {
                valueString = shardedJedis.get(key);
            }
        } finally {
            releaseResource(shardedJedis);
        }
        return valueString;
    }

    /**
     * @Title: getMap
     * @Description: 获取map
     * @param: @param
     * key
     */
    public static Map<String, String> getMap(String key) {
        ShardedJedis shardedJedis = getShardedJedis();
        Map<String, String> map = null;
        try {
            if (shardedJedis != null) {
                // 处理设备掉线后，设备map被置空
                Map<String, String> mapTemp = shardedJedis.hgetAll(key);
                map = mapTemp.isEmpty() ? null : mapTemp;
            }
        } catch (Exception e) {
            //log.error("shardedJedis.hkeys:{},get a exception：{}", key, e);
        } finally {
            releaseResource(shardedJedis);
        }
        return map;
    }

    /**
     * @Title: getStringInMap
     * @Description: 获取map中key的值
     * @param: key
     * @param: field
     * @param: @return
     * @return: String
     */
    public static String getStringInMap(String key, String field) {
        ShardedJedis shardedJedis = getShardedJedis();
        String value = null;
        try {
            if (shardedJedis == null || !shardedJedis.exists(key)) {
                value = null;
            } else {
                value = shardedJedis.hmget(key, field).get(0);
            }
        } finally {
            releaseResource(shardedJedis);
        }
        return value;
    }

    /**
     * @param key
     * @param field
     * @param value: void
     * @Title: setStringInMap
     * @Description: 设置key对应map中字段和值
     */
    public static void setStringInMap(String key, String field, String value) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
//			if (shardedJedis != null && shardedJedis.exists(key)) {
            if (shardedJedis != null) {
                shardedJedis.hset(key, field, value);
            }
        } finally {
            releaseResource(shardedJedis);
        }
    }

    /**
     * @Title: exist
     * @Description: 验证是否存在key
     * @param: @param
     * @param: @return
     * @return: boolean
     */
    public static boolean exists(String key) {
        ShardedJedis shardedJedis = getShardedJedis();
        boolean exist = false;
        try {
            if (shardedJedis == null || !shardedJedis.exists(key)) {
                exist = false;
            } else {
                exist = true;
            }
        } finally {
            releaseResource(shardedJedis);
        }
        return exist;
    }


    /**
     * @param key
     * @Title: deleteData
     * @Description: 删除数据
     * @return: boolean
     */
    public static boolean deleteData(String key) {
        ShardedJedis shardedJedis = getShardedJedis();
        boolean exist = false;
        try {
            if (shardedJedis == null || !shardedJedis.exists(key)) {
                return true;
            } else {
                shardedJedis.del(key);
            }
        } finally {
            releaseResource(shardedJedis);
        }
        return exist;
    }

    /**
     * @param key
     * @param expireSecond
     * @param value
     * @Title: setEx
     * @Description: 设置key过期时间
     */
    public static void setEx(String key, int expireSecond, String value) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            shardedJedis.setex(key, expireSecond, value);
        } catch (Exception e) {
           // log.error("set data to redis unsuccessfully:{}", e);
        } finally {
            releaseResource(shardedJedis);
        }
    }

    public static boolean exist(String key) {
        ShardedJedis shardedJedis = getShardedJedis();
        boolean result = false;
        try {
            result = shardedJedis.exists(key);
        } catch (Exception e) {
            //log.error("judge data of redis unsuccessfully:{}", e);
        } finally {
            releaseResource(shardedJedis);
        }
        return result;

    }

    /**
     * @Author lz
     * @Description: s设置过期时间
     * @param: [key, expireSecond]
     * @Date: 2018/11/28 11:16
     **/
    public static void setEx(String key, int expireSecond) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            shardedJedis.expire(key, expireSecond);
        } catch (Exception e) {
            //log.error("set expire to redis unsuccessfully:{}", e);
        } finally {
            releaseResource(shardedJedis);
        }
    }

    public static List<String> bgetString(int timeout, String key) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            return shardedJedis.blpop(timeout, key);
        } catch (Exception e) {
            //log.error("getStringBlock from redis unsuccessfully:{}", e);
        } finally {
            releaseResource(shardedJedis);
        }
        return null;
    }

    public static void lpushString(String key, String value) {
        ShardedJedis shardedJedis = getShardedJedis();
        try {
            shardedJedis.lpush(key, value);
        } catch (Exception e) {
           // log.error("getStringBlock from redis unsuccessfully!", e);
        } finally {
            releaseResource(shardedJedis);
        }
    }
    
    public static <T> T getObject(String key) {
        if (ObjectUtils.isNotEmpty(key)) {
            ShardedJedis shardedJedis = getShardedJedis();
            try {
                return ObjectUtils.unserialize(shardedJedis.get(key.getBytes()));
            } catch (Exception e) {
               System.out.println("setUserDTO to redis unsuccessfully!"+ e);
            } finally {
                releaseResource(shardedJedis);
            }
        }
        return null;
    }
    
	public static void setObject(String key, Object object) {
		System.out.print("object==========");
		MyTestUtil.print(object);
		if (ObjectUtils.isNotEmpty(key, object)) {
			ShardedJedis shardedJedis = getShardedJedis();
			try {
				if (shardedJedis != null) {
					System.out.println("key======"+key.getBytes());
					MyTestUtil.print(object);
					shardedJedis.set(key.getBytes(), ObjectUtils.serialize(object));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("set map into redis error:" + e);
			} finally {
				releaseResource(shardedJedis);
			}
		}
	}
}
