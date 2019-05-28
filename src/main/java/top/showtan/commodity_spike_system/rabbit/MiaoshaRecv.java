package top.showtan.commodity_spike_system.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.message.MiaoshaMessage;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.service.MiaoshaService;

/**
 * @Author: sanli
 * @Date: 2019/4/2 15:50
 */
@Service
@Transactional
public class MiaoshaRecv {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = {RabbitConfig.MIAOSHA_QUEUE})
    public void miaoshaRecv(String msg) {
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(msg, MiaoshaMessage.class);
        miaoshaService.doMiaosha(miaoshaMessage);
    }
}
