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

package com.tencent.trpc.demo.exception.service;

import com.tencent.trpc.core.exception.TRpcException;
import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.RpcContext;
import com.tencent.trpc.core.rpc.RpcServerContext;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;
import com.tencent.trpc.demo.api.HelloAPI;
import com.tencent.trpc.demo.exception.handler.CustomizeException;
import com.tencent.trpc.spring.exception.annotation.TRpcHandleException;
import org.springframework.stereotype.Service;

/**
 * HelloAPI 接口实现类
 */
@Service
public class HelloServiceFuncImpl implements HelloAPI {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceFuncImpl.class);

    /**
     * sayHello 方法使用统一异常处理，但不处理 NullPointerException 异常
     */
    @TRpcHandleException(exclude = NullPointerException.class)
    @Override
    public HelloRsp sayHello(RpcContext context, HelloReq request) {
        // trpc-cli -verbose -func /trpc.test.demo.Hello/SayHello -target ip://127.0.0.1:8085 -body '{"msg": "hello"}'
        RpcServerContext serverContext = (RpcServerContext) context;
        logger.info(getClass().getName() + " receive:{}, context:{}", request, serverContext);
        String msg = request.getMsg();
        switch (msg) {
            case "custom":
                throw CustomizeException.newException(999, "throw CustomizeException");
            case "trpc":
                throw TRpcException.newBizException(-1, "throw TRpcException");
            case "npe":
                throw new NullPointerException();
        }
        throw TRpcException.newBizException(999, "trpc biz exception");
    }
}
