package com.hmall.common.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 李阳
 * @Date: 2025/07/29/8:45
 * @Description: MQ的消息转换器
 * 因为这个配置类需要不同的包来引用，因此要使用spring-factories自动装配的方式
 */
@Configuration
// 只有引入了amqp的微服务才会去加载这个配置类
@ConditionalOnClass(RabbitTemplate.class)
public class MqConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
