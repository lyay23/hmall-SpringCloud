package com.hmall.trade.constants;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 李阳
 * @Date: 2025/07/30/16:13
 * @Description: 交换机，队列，路由键常量等等名字
 */
public interface MqConstants {
    // 交换机名称
    String DELAY_EXCHANGE_NAME="trade.delay.direct";

    // 队列名称
    String DELAY_ORDER_QUEUE_NAME="trade.delay.order.queue";

    // 路由键
    String DELAY_ORDER_KEY="delay.order.query";

}
