package top.showtan.commodity_spike_system.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.message.MiaoshaMessage;
import top.showtan.commodity_spike_system.redis.RedisService;

/**
 * @Author: sanli
 * @Date: 2019/4/2 15:45
 */
@Service
@Transactional
public class MiaoshaSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage message) {
        String miaoshaMessage = RedisService.beanToString(message);
        rabbitTemplate.convertAndSend(RabbitConfig.MIAOSHA_EXCHANGE, RabbitConfig.MIAOSHA_KEY, miaoshaMessage);
    }
}
