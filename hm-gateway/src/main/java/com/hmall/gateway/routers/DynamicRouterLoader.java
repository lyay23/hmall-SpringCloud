package com.hmall.gateway.routers;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 李阳
 * @Date: 2025/07/27/9:56
 * @Description:
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicRouterLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;
    private final Set<String> routers=new HashSet<>();
    private final String dataId = "gateway-router.json";
    private final String group = "DEFAULT_GROUP";

    // 此注解代表项目启动时，加载路由配置，在这个Bean初始化的时候，会调用initRouterConfigListener方法
    @PostConstruct
    public void initRouterConfigListener() throws NacosException {
        log.info("初始化动态路由配置监听器");
        // 1. 项目启动时，先拉取一次配置，并且添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }


                    @Override
                    public void receiveConfigInfo(String configInfo) {
                      // 2. 监听到配置变更时，需要去更新路由表
                        updateRouterConfigListener(configInfo);
                    }
                });
        // 3. 第一次读取到配置，也需要更新到路由表中

        updateRouterConfigListener(configInfo);
    }

    // 4. 监听到配置变更时，需要去更新路由表
    public void updateRouterConfigListener(String configInfo) {

        log.info("监听到路由配置：{}", configInfo);
        //  1. 解析配置信息，转换为RouteDefinition
        List<RouteDefinition> routeDefinitionList = JSONUtil.toList(configInfo, RouteDefinition.class);

        // 2. 删除旧的路由表

        for (String id : routers) {
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        }
        // 清空旧的路由id表
        routers.clear();
        // 3. 更新路由表
        for (RouteDefinition routeDefinition : routeDefinitionList) {
            log.info("更新路由配置：{}", routeDefinition);
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            // 记录录音id，便于下次更新时删除
            routers.add(routeDefinition.getId());
        }

    }
}
