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

package com.tencent.trpc.demo.api;

import com.tencent.trpc.core.rpc.RpcContext;
import com.tencent.trpc.core.rpc.anno.TRpcMethod;
import com.tencent.trpc.core.rpc.anno.TRpcService;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;

/**
 * server(Fbp协议，仅cdg fit使用)
 */
@TRpcService(name = "trpc.test.demo.HelloFbp")
public interface HelloFbpAPI {

    /**
     * @param context tRPCContext信息
     * @param request 请求PB
     * @return HelloRsp PB对象
     */
    @TRpcMethod(name = "SayHello")
    HelloRsp sayHello(RpcContext context, HelloReq request);
}