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

package com.tencent.trpc.demo.simpleclient;

import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello.HelloReq;
import com.tencent.trpc.demo.api.Hello.HelloRsp;
import com.tencent.trpc.demo.api.HelloAsyncAPI;
import com.tencent.trpc.server.container.TRPC;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import org.apache.commons.lang3.time.StopWatch;

/**
 * 基于trpc-java.yaml配置的Client Demo。
 */
public class HelloClientOnYaml {

    private static final Logger logger = LoggerFactory.getLogger(HelloClientOnYaml.class);
    private static final AtomicInteger SUCCESS = new AtomicInteger();
    private static final AtomicBoolean ISFAIL = new AtomicBoolean(false);
    private static final AtomicInteger FAIL = new AtomicInteger();
    private static long COUNT = 0;
    private static final HelloReq REQUEST = HelloReq.newBuilder().setMsg("Hello tRPC-Java!" + COUNT++).build();

    public static void main(String[] args) throws Exception {

        // init&start tRPC 配置，主要是解析trpc-java.yaml
        TRPC.start();
        StopWatch watch = StopWatch.createStarted();
        for (int i = 0; i < 10000; i++) {
            // 1.异步调用
            HelloAsyncAPI helloAsyncAPI = TRpcProxy.getProxy("asyncHello");

            if (SUCCESS.intValue() > 0 && SUCCESS.intValue() % 10 == 0) {
                Thread.sleep(1);
                if (SUCCESS.intValue() % 1000 == 0) {
                    System.out.println("SUCCESS:=" + SUCCESS);
                }
            }
            LockSupport.parkNanos(100);
            LockSupport.unpark(Thread.currentThread());
            if (ISFAIL.get()) {

                Thread.sleep(1);
                ISFAIL.set(false);
            }

            // 异步调用
            CompletionStage<HelloRsp> future = helloAsyncAPI.sayHello(new RpcClientContext(), REQUEST);
            // 默认使用fork join pool 线程组处理后续逻辑。
            future.toCompletableFuture().whenComplete((r, e) -> {
                if (e != null) {
                    FAIL.incrementAndGet();
                    ISFAIL.set(true);
                } else {
                    SUCCESS.incrementAndGet();
                }
            }).exceptionally((e) -> {
                e.printStackTrace();
                FAIL.incrementAndGet();
                ISFAIL.set(true);
                return null;
            });
        }
        watch.stop();
        System.out.println(watch.getTime(TimeUnit.MILLISECONDS));
        Thread.sleep(1000);
        System.out.println(SUCCESS);
        System.out.println(FAIL);

    }

}

