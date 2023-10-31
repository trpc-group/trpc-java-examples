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

package com.tencent.trpc.demo.polaris.service;

import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.RpcContext;
import com.tencent.trpc.core.rpc.RpcServerContext;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;
import com.tencent.trpc.demo.api.HelloAsyncAPI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.springframework.stereotype.Service;

@Service
public class AsyncServiceImpl implements HelloAsyncAPI {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public CompletionStage<HelloRsp> sayHello(RpcContext context, HelloReq request) {
        RpcServerContext serverContext = (RpcServerContext) context;
        logger.info(getClass().getName() + " receive:{}, context:{}", request, serverContext);
        return CompletableFuture.supplyAsync(() -> {
            HelloRsp.Builder rspBuilder = HelloRsp.newBuilder();
            rspBuilder.setMsg("server received: " + request.getMsg());
            return rspBuilder.build();
        });
    }
}