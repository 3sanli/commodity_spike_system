package top.showtan.commodity_spike_system.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: sanli
 * @Date: 2019/4/2 15:22
 */
@Configuration
public class RabbitConfig {
    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String MIAOSHA_EXCHANGE = "miaosha.exchange";
    public static final String MIAOSHA_KEY = "miaosha.key";

    @Bean
    public Queue miaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE);
    }

    @Bean
    public TopicExchange miaoshaExchange() {
        return new TopicExchange(MIAOSHA_EXCHANGE);
    }

    @Bean
    public Binding miaoshaBinding(@Autowired Queue miaoshaQueue, @Autowired TopicExchange miaoshaExchange) {
        return BindingBuilder.bind(miaoshaQueue).to(miaoshaExchange).with(MIAOSHA_KEY);
    }
}
