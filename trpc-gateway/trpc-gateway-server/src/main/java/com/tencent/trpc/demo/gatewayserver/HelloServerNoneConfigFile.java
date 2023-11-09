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

package com.tencent.trpc.demo.gatewayserver;

import com.tencent.trpc.core.common.ConfigManager;
import com.tencent.trpc.core.common.config.ProviderConfig;
import com.tencent.trpc.core.common.config.ServiceConfig;
import com.tencent.trpc.demo.api.HelloAPI;


/**
 * 不依赖任何配置文件的Hello服务
 */
public class HelloServerNoneConfigFile {

    private static final String IP = "127.0.0.1";

    // 提供TRPC原生协议的端口
    private static final int TRPC_PORT = 8090;

    /**
     * 提供trpc原生tcp接口
     */
    public static void main(String[] args) {
        ConfigManager.getInstance().start();
        startService();
    }

    private static void startService() {
        // 服务接口配置 服务暴露ip,协议等信息配置,暴露tcp原生端口
        ServiceConfig tRpcServiceConfig = getTRpcServiceConfig();
        ProviderConfig<HelloAPI> providerConfig = new ProviderConfig<>();
        providerConfig.setRef(new HelloServiceImpl());
        tRpcServiceConfig.addProviderConfig(providerConfig);
        tRpcServiceConfig.export();
    }

    private static ServiceConfig getTRpcServiceConfig() {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setIp(IP);
        serviceConfig.setNetwork("tcp");
        serviceConfig.setPort(TRPC_PORT);
        return serviceConfig;
    }

}

