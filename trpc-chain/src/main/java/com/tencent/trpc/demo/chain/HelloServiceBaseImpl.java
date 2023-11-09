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

import com.tencent.trpc.core.rpc.RpcContext;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;
import com.tencent.trpc.demo.api.HelloAPI;
import java.util.concurrent.TimeUnit;

public class HelloServiceBaseImpl implements HelloAPI {

    private static final int MIN_DEAL_TIME_MILL = 0;

    private static final int MAX_DEAL_TIME_MILL = 2000;

    @Override
    public HelloRsp sayHello(RpcContext context, HelloReq request) {
        HelloRsp.Builder rspBuilder = HelloRsp.newBuilder();
        rspBuilder.setMsg(request.getMsg() + "   request id= " + request.getId());
        deal(request.getCost());
        return rspBuilder.build();
    }

    private void deal(int cost) {
        if (cost <= MIN_DEAL_TIME_MILL) {
            return;
        }
        if (cost >= MAX_DEAL_TIME_MILL) {
            cost = MAX_DEAL_TIME_MILL;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(cost);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
