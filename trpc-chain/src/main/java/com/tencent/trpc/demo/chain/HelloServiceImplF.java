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

package com.tencent.trpc.demo.chain;

import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.RpcContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello;
import com.tencent.trpc.demo.api.HelloAPI;
import com.tencent.trpc.demo.api.HelloAsyncAPI;

public class HelloServiceImplF extends HelloServiceBaseImpl implements HelloAPI {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImplF.class);

    private static HelloAsyncAPI asyncHelloG;
    private static Hello.HelloReq requestG;

    @Override
    public Hello.HelloRsp sayHello(RpcContext context, Hello.HelloReq request) {
        logger.info(getClass().getName() + " receive:{}  context:{}", request, context);
        callF();
        return super.sayHello(context, request);
    }

    private static void callF() {
        String msg = "Hello tRPC-Java!";
        // 通过${name}在trpc-java.yaml中配置 获取 tRPC 容器中的 HelloAPI Client 代理
        HelloAsyncAPI asyncHelloG = TRpcProxy.getProxy("asyncHelloG");
        Hello.HelloReq requestG = Hello.HelloReq.newBuilder().setId("G").setMsg(msg).setCost(100).build();
        long start = System.currentTimeMillis();
        final String[] result = new String[1];
        asyncHelloG.sayHello(new RpcClientContext(), requestG).thenAccept(v -> result[0] = v.getMsg())
                .toCompletableFuture().join();
        logger.info("{} client call! cost = {} response :::::  msg={} ", requestG.getId(),
                System.currentTimeMillis() - start, result);
    }
}
