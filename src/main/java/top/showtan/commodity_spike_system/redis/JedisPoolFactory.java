package top.showtan.commodity_spike_system.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: sanli
 * @Date: 2019/3/25 15:05
 */
@Component
public class JedisPoolFactory {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisConfig.getMaxTotal());
        config.setMaxIdle(redisConfig.getMaxIdle());
        config.setMinIdle(redisConfig.getMinIdle());
        return new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeOut(), redisConfig.getPassword());
    }
}
