package top.showtan.commodity_spike_system.redis;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author: sanli
 * @Date: 2019/3/20 11:05
 */
@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    //redis set封装
    public <T> void set(BaseKeyPre keyPre, String key, T value) {
        String valueStr = beanToString(value);
        Jedis jedis = jedisPool.getResource();
        String realKey = "" + keyPre.getKeyPre() + key;
        jedis.setex(realKey, keyPre.getExpire(), valueStr);
        closeJedis(jedis);
    }

    //redis get封装
    public <T> T get(BaseKeyPre keyPre, String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        String realKey = "" + keyPre.getKeyPre() + key;
        String value = jedis.get(realKey);
        T bean = stringToBean(value, clazz);
        closeJedis(jedis);
        return bean;
    }

    //redis del封装
    public void delete(BaseKeyPre keyPre, String key) {
        Jedis jedis = jedisPool.getResource();
        String realKey = "" + keyPre.getKeyPre() + key;
        jedis.del(realKey);
        closeJedis(jedis);
    }

    //redis decr封装
    public void decr(BaseKeyPre keyPre, String key) {
        String realKey = keyPre.getKeyPre() + key;
        Jedis jedis = jedisPool.getResource();
        jedis.decr(realKey);
        closeJedis(jedis);
    }

    //redis decr封装
    public void decrBy(BaseKeyPre keyPre, String key, Long value) {
        String realKey = keyPre.getKeyPre() + key;
        Jedis jedis = jedisPool.getResource();
        jedis.decrBy(realKey, value);
        closeJedis(jedis);
    }

    //redis incrf封装
    public void incr(BaseKeyPre keyPre, String key) {
        String realKey = keyPre.getKeyPre() + key;
        Jedis jedis = jedisPool.getResource();
        jedis.incr(realKey);
        closeJedis(jedis);
    }

    public void incrBy(BaseKeyPre keyPre, String key, Long value) {
        String realKey = keyPre.getKeyPre() + key;
        Jedis jedis = jedisPool.getResource();
        jedis.incrBy(realKey, value);
        closeJedis(jedis);
    }

    public void cleanAll() {
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
        closeJedis(jedis);
    }

    public static <T> T stringToBean(String value, Class<T> clazz) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (clazz == String.class) {
            return clazz.cast(value);
        } else if (clazz == int.class) {
            return clazz.cast((Integer.valueOf(value)).intValue());
        } else if (clazz == Integer.class) {
            return clazz.cast(Integer.valueOf(value));
        } else {
            return JSON.parseObject(value, clazz);
        }
    }

    public static <T> String beanToString(T value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        return JSON.toJSONString(value);
    }

    private void closeJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    private boolean isExist(String key) {
        Jedis jedis = jedisPool.getResource();
        return jedis.exists(key);
    }
}
