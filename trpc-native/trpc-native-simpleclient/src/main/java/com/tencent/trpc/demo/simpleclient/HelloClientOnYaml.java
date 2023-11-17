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
import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;
import com.tencent.trpc.demo.api.HelloAPI;
import com.tencent.trpc.demo.api.HelloAsyncAPI;
import com.tencent.trpc.server.container.TRPC;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * 基于trpc-java.yaml配置的Client Demo。
 */
public class HelloClientOnYaml {

    private static final Logger logger = LoggerFactory.getLogger(HelloClientOnYaml.class);
    private static final HelloReq REQUEST = HelloReq.newBuilder().setMsg("Hello tRPC-Java!").build();

    public static void main(String[] args) throws Exception {
        // init&start tRPC 配置，主要是解析trpc-java.yaml
        TRPC.start();
        // 同步调用
        HelloAPI helloAPI = TRpcProxy.getProxy("trpc.test.demo.Hello");
        HelloRsp result = helloAPI.sayHello(new RpcClientContext(), REQUEST);
        logger.info("tRPC Sync Call! request={} . response={}", REQUEST.getMsg(), result.getMsg());

        // 异步调用
        HelloAsyncAPI helloAsyncAPI = TRpcProxy.getProxy("asyncHello");
        CompletionStage<HelloRsp> future = helloAsyncAPI.sayHello(new RpcClientContext(), REQUEST);
        future.thenAccept(r -> logger.info("tRPC Async Call! request={} . response={}", REQUEST.getMsg(), r.getMsg()));
        TimeUnit.SECONDS.sleep(1);
        ConfigManager.getInstance().stop();
    }

}

