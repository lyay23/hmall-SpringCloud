package com.hmall.trade.listener;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constants.MqConstants;
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
 * @Date: 2025/07/30/16:27
 * @Description: 订单延迟消息监听器
 */
@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;
    private final PayClient payClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstants.DELAY_ORDER_QUEUE_NAME),
            exchange = @Exchange(name = MqConstants.DELAY_EXCHANGE_NAME,delayed = "true"),
            key = MqConstants.DELAY_ORDER_KEY
    ))
    public void listenOrderDelayMessage(Long orderId) {
         // 1. 查询订单
        Order order = orderService.getById(orderId);
        // 2. 检测订单状态，需要查询订单状态
        if(order==null || order.getStatus()!=1){
            // 订单不存在，或者订单状态不是待支付，直接结束
            return;
        }
        // 3. 未支付需要查询订单流水状态
        PayOrderDTO payOrderDTO = payClient.queryPayOrderByBizOrderNo(orderId);

        // 4. 判断是否支付
        if(payOrderDTO!=null && payOrderDTO.getStatus()==3){
            // 4.1 支付了，修改订单状态\
            orderService.markOrderPaySuccess(orderId);
        }else{
            // 4.2 未支付，关闭订单
            orderService.canselOrder(orderId);
        }




    }
}
