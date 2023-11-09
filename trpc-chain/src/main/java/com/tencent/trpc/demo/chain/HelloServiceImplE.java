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
import com.tencent.trpc.core.rpc.RpcServerContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello;
import com.tencent.trpc.demo.api.HelloAPI;
import com.tencent.trpc.demo.api.HelloAsyncAPI;

public class HelloServiceImplE extends HelloServiceBaseImpl implements HelloAPI {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImplE.class);


    @Override
    public Hello.HelloRsp sayHello(RpcContext context, Hello.HelloReq request) {
        logger.info(getClass().getName() + " receive:{}  context:{}", request, context);
        // 串行异步调用helloA  等待返回或超时后处理后续逻辑
        RpcClientContext rpcClientContext = ((RpcServerContext) context).newClientContext();
        callF(rpcClientContext);
        return super.sayHello(context, request);
    }

    private static void callF(RpcContext context) {
        String msg = "Hello tRPC-Java!";
        // 通过${name}在trpc-java.yaml中配置 获取 tRPC 容器中的 HelloAPI Client 代理
        HelloAsyncAPI asyncHelloF = TRpcProxy.getProxy("asyncHelloF");
        Hello.HelloReq requestF = Hello.HelloReq.newBuilder().setId("F").setMsg(msg).setCost(120).build();
        final long start = System.currentTimeMillis();
        asyncHelloF.sayHello(context, requestF).thenAccept(
                v -> logger.info("{} client call! cost = {} response :::::  msg={} ", requestF.getId(),
                        System.currentTimeMillis() - start, v.getMsg())).toCompletableFuture().join();
    }

}
