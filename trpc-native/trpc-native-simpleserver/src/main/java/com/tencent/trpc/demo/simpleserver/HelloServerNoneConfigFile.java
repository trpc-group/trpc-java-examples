/*
 * Tencent is pleased to support the open source community by making tRPC available.
 *
 * Copyright (C) 2023 THL A29 Limited, a Tencent company. 
 * All rights reserved.
 *
 * If you have downloaded a copy of the tRPC source code from Tencent,
 * please note that tRPC source code is licensed under the Apache 2.0 License,
 * A copy of the Apache 2.0 License can be found in the LICENSE file.
 */

package com.tencent.trpc.demo.simpleserver;

import com.tencent.trpc.core.common.ConfigManager;
import com.tencent.trpc.core.common.config.ProviderConfig;
import com.tencent.trpc.core.common.config.ServiceConfig;
import com.tencent.trpc.demo.api.HelloAPI;

/**
 * 不依赖任何配置文件的Hello服务
 */
public class HelloServerNoneConfigFile {

    private static final String IP = "127.0.0.1";

    // 提供TCP协议的端口
    private static final int TCP_PORT = 8090;
    // 提供HTTP协议的端口
    private static final int HTTP_PORT = 8081;

    /**
     * 一份HelloServiceImpl代码逻辑，同时提供trpc的tcp接口 和 http接口 。
     * <p>
     * http接口默认访问方式为：http://127.0.0.1:8081/trpc/test/demo/Hello/SayHello?msg=Hello tRPC Java!
     * 或                   http://127.0.0.1:8081/trpc.test.demo.Hello/SayHello?msg=Hello tRPC Java!
     */
    public static void main(String[] args) {
        ConfigManager.getInstance().start();
        startService();
    }

    private static void startService() {
        // 1)服务接口配置 服务暴露ip,协议等信息配置,同时暴露tcp与http
        ServiceConfig tRpcServiceConfig = getTRpcServiceConfig();
        ServiceConfig httpServiceConfig = getHttpServiceConfig();
        ProviderConfig<HelloAPI> providerConfig = new ProviderConfig<>();
        providerConfig.setRef(new HelloServiceImpl());
        tRpcServiceConfig.addProviderConfig(providerConfig);
        httpServiceConfig.addProviderConfig(providerConfig);
        tRpcServiceConfig.export();
        httpServiceConfig.export();
    }

    private static ServiceConfig getTRpcServiceConfig() {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setIp(IP);
        serviceConfig.setNetwork("tcp");
        serviceConfig.setPort(TCP_PORT);
        return serviceConfig;
    }

    private static ServiceConfig getHttpServiceConfig() {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setIp(IP);
        serviceConfig.setPort(HTTP_PORT);
        serviceConfig.setProtocol("http");
        serviceConfig.setTransporter("jetty");
        return serviceConfig;
    }
}

