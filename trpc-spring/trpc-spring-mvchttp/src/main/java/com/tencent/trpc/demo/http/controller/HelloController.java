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

package com.tencent.trpc.demo.http.controller;

import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.HelloAPI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/test")
    public String testHttp() {
        // curl http://127.0.0.1:8083/test
        HelloAPI proxy = TRpcProxy.getProxy("testHttp", HelloAPI.class);
        return proxy.sayHello(new RpcClientContext(),
                HelloReq.newBuilder().setMsg("hello").build()).getMsg();
    }
}
