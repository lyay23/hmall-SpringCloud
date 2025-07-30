package com.hmall.trade.listener;

import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 李阳
 * @Date: 2025/07/29/8:55
 * @Description: MQ支付状态监听器(接收方)
 */
@Component
@RequiredArgsConstructor
public class PayStatusListener {

    private final IOrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.pay.success.queue", durable = "true"),
            exchange = @Exchange(name = "pay.direct"),
            key = "pay.success"
    ))

    /**
     * orderId: 订单id由发送方传入，哪一个订单支付成功了，就传入哪个订单id
     */
    public void listenPaySuccess(Long orderId) {

        // 1. 查询订单
        Order byId = orderService.getById(orderId);
        // 2. 判断订单状态，是否为未支付
       if(byId==null || byId.getStatus() != 1){

           // 2.1 如果不是未支付状态，直接返回
           return;
       }

        // 3. 标记订单为已支付
        orderService.markOrderPaySuccess(orderId);
    }
}
