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

package com.tencent.trpc.demo.coroutine.controller;

import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.HelloAPI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求方法和路径
 */
@RestController
public class HelloController {

    @GetMapping(value = "/hello/http")
    public String testHttp(String message) {
        // curl -X GET "http://localhost:8088/hello/http?message=hello"
        HelloAPI proxy = TRpcProxy.getProxy("testHttpClient", HelloAPI.class);
        return proxy.sayHello(new RpcClientContext(),
                HelloReq.newBuilder().setMsg(message).build()).getMsg();
    }

    @GetMapping(value = "/hello/trpc")
    public String testTrpc(String message) {
        // curl -X GET "http://localhost:8088/hello/trpc?message=hello"
        HelloAPI proxy = TRpcProxy.getProxy("testTrpcClient", HelloAPI.class);
        return proxy.sayHello(new RpcClientContext(),
                HelloReq.newBuilder().setMsg(message).build()).getMsg();
    }

}
