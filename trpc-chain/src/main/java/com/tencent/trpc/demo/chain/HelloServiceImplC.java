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
import com.tencent.trpc.core.rpc.RpcContext;
import com.tencent.trpc.demo.api.Hello;
import com.tencent.trpc.demo.api.HelloAPI;

public class HelloServiceImplC extends HelloServiceBaseImpl implements HelloAPI {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImplC.class);

    @Override
    public Hello.HelloRsp sayHello(RpcContext context, Hello.HelloReq request) {
        logger.info(getClass().getName() + " receive:{} ", request);
        return super.sayHello(context, request);
    }
}
