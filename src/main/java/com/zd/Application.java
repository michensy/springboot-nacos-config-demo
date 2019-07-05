package com.zd;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.concurrent.Executor;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("启动成功！");
    }

    @Slf4j
    @RefreshScope
    @RestController
    static class TestController {

        @Value("${test.title:}")
        private String title;

        @GetMapping("/test")
        public String hello() {
            return title;
        }

        @GetMapping("/test2")
        public void configReflash() {
            try {
                String serverAddr = "127.0.0.1:8848";
                String dataId = "alibaba-nacos-config-client";
                String group = "DEFAULT_GROUP";
                Properties properties = new Properties();
                properties.put("serverAddr", serverAddr);
                // properties.put("namespace", "f33ad473-6ca6-4802-b1ea-048d5753a016");
                ConfigService configService = NacosFactory.createConfigService(properties);
                String content = configService.getConfig(dataId, group, 50000);
                System.out.println(content);
                configService.addListener(dataId, group, new Listener() {
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        System.out.println("recieve1:" + configInfo);
                    }
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
