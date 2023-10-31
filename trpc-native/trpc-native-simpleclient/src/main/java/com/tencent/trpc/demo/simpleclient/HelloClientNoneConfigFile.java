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

package com.tencent.trpc.demo.simpleclient;

import com.tencent.trpc.core.common.ConfigManager;
import com.tencent.trpc.core.common.config.BackendConfig;
import com.tencent.trpc.core.common.config.ConsumerConfig;
import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;
import com.tencent.trpc.demo.api.HelloAPI;
import com.tencent.trpc.demo.api.HelloAsyncAPI;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * 不依赖配置文件的例子，是最原生的使用方式。是一种高级用法，一般情况下不建议在业务中直接这样使用，因为代码量偏大，管理配置不统一。
 */
public class HelloClientNoneConfigFile {

    private static final Logger logger = LoggerFactory.getLogger(HelloClientNoneConfigFile.class);
    private static final String SERVICE_URL = "ip://127.0.0.1:8090";
    private static final HelloReq REQUEST = HelloReq.newBuilder().setMsg("Hello tRPC-Java!").build();

    public static void main(String[] args) throws Exception {
        // 0.init&start tRPC
        // 設置serverConfig
        ConfigManager.getInstance().start();
        // 服务接口配置
        BackendConfig backendConfig = new BackendConfig();
        backendConfig.setNamingUrl(SERVICE_URL);

        // 1.同步调用例子
        ConsumerConfig<HelloAPI> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setServiceInterface(HelloAPI.class);
        // 获取Client反向代理，并创建连接
        HelloAPI proxy = backendConfig.getProxy(consumerConfig);
        // 同步调用
        HelloRsp result = proxy.sayHello(new RpcClientContext(), REQUEST);
        logger.info("tRPC Sync Call! request={} . response={}", REQUEST.getMsg(), result.getMsg());

        // 2.异步接口调用demo
        ConsumerConfig<HelloAsyncAPI> asyncConsumerConfig = new ConsumerConfig<>();
        asyncConsumerConfig.setServiceInterface(HelloAsyncAPI.class);
        // 获取Client反向代理，并创建连接
        // 通过 consumerConfig 获取 tRPC 容器中的 HelloAsyncAPI Client 代理
        HelloAsyncAPI helloAsyncAPI = backendConfig.getProxy(asyncConsumerConfig);
        // 异步调用
        CompletionStage<HelloRsp> future = helloAsyncAPI.sayHello(new RpcClientContext(), REQUEST);
        // 默认使用fork join pool 线程组处理后续逻辑。
        future.thenAccept(r -> logger.info("tRPC Async Call! request={} . response={}", REQUEST.getMsg(), r.getMsg()));

        TimeUnit.SECONDS.sleep(1);
        ConfigManager.getInstance().stop();
    }

}

