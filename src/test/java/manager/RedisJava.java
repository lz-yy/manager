package manager;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.voucher.manage.tools.MyTestUtil;
import com.voucher.manage2.redis.JedisUtil1;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedis;
 
public class RedisJava {
    public static void main(String[] args) {
        //连接本地的 Redis 服务
    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "192.168.10.100", 6379, 10000, "redis",1);

        Jedis jedis;
        jedis= jedisPool.getResource();
        jedis.auth("redis");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        
        Set<String> keys=jedis.keys("*");
        for(String key:keys){
        	Object value=jedis.get(key);
        	System.out.println(key+" = ");
        	/*if(key.equals("[item_room]_columnName")){
        		jedis.del(key);
        	}*/
        	jedis.del(key);
        	MyTestUtil.print(value);
        }

    }
}