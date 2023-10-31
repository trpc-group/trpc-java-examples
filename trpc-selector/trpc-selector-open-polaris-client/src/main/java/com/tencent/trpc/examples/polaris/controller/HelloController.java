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

package com.tencent.trpc.examples.polaris.controller;

import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.core.utils.RpcContextUtils;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.HelloAPI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求方法和路径
 */
@RestController
public class HelloController {

    private static final String HTTP_CLIENT = "testHttpClient";
    private static final String TRPC_CLIENT = "testTrpcClient";

//    @Resource(name = "helloServiceImpl")
//    HelloAPI helloAPI;

    @GetMapping(value = "/polaris/http/hello")
    public String httpClient(String message) {
        // curl http://localhost:8088/polaris/http/hello?message=trpc-java
        HelloAPI proxy = TRpcProxy.getProxy(HTTP_CLIENT, HelloAPI.class);
        return proxy.sayHello(new RpcClientContext(),
                HelloReq.newBuilder().setMsg(message).build()).getMsg();
    }

    @GetMapping(value = "/polaris/trpc/hello")
    public String trpcClient(String message) {
        // curl http://localhost:8088/polaris/trpc/hello?message=trpc-java
        HelloAPI proxy = TRpcProxy.getProxy(TRPC_CLIENT, HelloAPI.class);
        return proxy.sayHello(new RpcClientContext(),
                HelloReq.newBuilder().setMsg(message).build()).getMsg();
    }

    @GetMapping(value = "testRule")
    public String testRule() {
        RpcClientContext clientContext = new RpcClientContext();
        RpcContextUtils.putRequestAttachValue(clientContext, "selector-meta-env", "BASE");
        RpcContextUtils.putRequestAttachValue(clientContext, "selector-meta-set", "dcp_set11");
        RpcContextUtils.putRequestAttachValue(clientContext, "selector-meta-env_type", "DEV");
        HelloAPI testRuleRouter = TRpcProxy.getProxy("testRuleRouter", HelloAPI.class);
        return testRuleRouter.sayHello(clientContext,
                HelloReq.newBuilder().setMsg("message").build()).getMsg();
    }
}
