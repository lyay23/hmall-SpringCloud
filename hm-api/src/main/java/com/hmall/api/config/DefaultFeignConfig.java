package com.hmall.api.config;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 李阳
 * @Date: 2025/07/19/16:31
 * @Description:
 */

import org.springframework.context.annotation.Bean;
import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }

    /**
     * 将用户信息添加请求头
     * @return
     */
    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {

                /**
                 * 微服务调用微服务的请求是从网关传过来的，
                 * 所以这时请求头中是有用户信息的，
                 * 只需要从请求头中获取到用户信息，添加到请求头中即可
                 * 如何请求再次传递给下一个微服务，这样就将用户信息传递给了其他微服务了
                 */
                Long userId = UserContext.getUser();
                if (userId != null){
                    requestTemplate.header("user-info", userId.toString());
                }
            }
        };
    }

    @Bean
    public ItemClientFallbackFactory itemClientFallbackFactory(){
        return new ItemClientFallbackFactory();
    }
}
