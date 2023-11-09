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
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.demo.api.Hello;
import com.tencent.trpc.demo.api.HelloAPI;
import com.tencent.trpc.server.container.TRPC;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 同步的demo，包括：串行执行、并行执行、出现超时后降级的demo
 * 1.同步调用helloA  等待返回或超时后处理后续逻辑,然后包括以下三种情况的demo：
 * <p>
 * 模拟调用链路过程1：串行调用A，返回后并行调用B和C，等待BC都结束后，调用E，E返回结果后结束。
 * <p>
 * 模拟调用链路过程2：串行调用A，返回后并行调用B和C，等待BC任何一个服务响应后，调用E，E返回结果后结束。
 * <p>
 * 模拟调用链路过程3：并行调用A、B、C、D。等待结果后，串行调用D，返回结果后结束。
 * <p>
 * 因为init()方法很原始，不建议在工程中直接使用，因为可靠性比较弱，建议用北极星等方式使用client。@TODO 做一个Client 池的Demo
 */
public class SyncChainClientDemo {

    private static final Logger logger = LoggerFactory.getLogger(SyncChainClientDemo.class);

    private static HelloAPI helloA;
    private static Hello.HelloReq requestA;

    private static HelloAPI helloB;
    private static Hello.HelloReq requestB;

    private static HelloAPI helloC;
    private static Hello.HelloReq requestC;

    private static HelloAPI helloD;
    private static Hello.HelloReq requestD;

    private static HelloAPI helloE;
    private static Hello.HelloReq requestE;

    public static void main(String[] args) throws Exception {
        // start tRPC，主要是解析trpc-java.yaml,启动模拟的服务。
        TRPC.start();
        syncDemo();
    }

    private static void init() {
        String msg = "Hello tRPC-Java!";
        helloA = TRpcProxy.getProxy("helloA");
        requestA = Hello.HelloReq.newBuilder().setId("A").setMsg(msg).setCost(10).build();

        helloB = TRpcProxy.getProxy("helloB");
        requestB = Hello.HelloReq.newBuilder().setId("B").setMsg(msg).setCost(30).build();

        helloC = TRpcProxy.getProxy("helloC");
        requestC = Hello.HelloReq.newBuilder().setId("C").setMsg(msg).setCost(50).build();

        helloD = TRpcProxy.getProxy("helloD");
        requestD = Hello.HelloReq.newBuilder().setId("D").setMsg(msg).setCost(1200).build();

        helloE = TRpcProxy.getProxy("helloE");
        requestE = Hello.HelloReq.newBuilder().setId("E").setMsg(msg).setCost(20).build();

    }

    public static void syncDemo() throws ExecutionException, InterruptedException, TimeoutException {
        init();
        // 同步调用helloA  等待返回或超时后处理后续逻辑
        syncCall(helloA, requestA);
        syncCall(helloA, requestA);
        syncCall(helloA, requestA);
        // 模拟调用链路过程1：并行访问B和C服务，任何一个服务调用完成后，继续执行。
        syncAnyOfDemo();
        // 模拟调用链路过程2：并行访问B和C服务，都完成后继续执行。
        syncAllOfDemo();
        // 模拟调用链路过程3：并行调用B、C、D。等待全部结束后，串行调用E，返回结果后结束。 其中D服务做了降级处理。
        syncAllOfTimeoutDemo();
        // 同步调用helloE
        syncCall(helloE, requestE);
    }


    /**
     * 模拟调用链路过程1：并行访问B和C服务，任何一个服务调用完成后，继续执行。
     */
    private static void syncAnyOfDemo() throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        final String[] resultB = new String[1];
        final String[] resultC = new String[1];
        CompletableFuture.anyOf(getFuture(resultB, helloB, requestB), getFuture(resultC, helloC, requestC))
                .get(1000, TimeUnit.MILLISECONDS);
        //一个为空，一个有数据。
        logger.info("B & C  cost = {}ms ,anyOf response ::::: B = {} ,  C = {}", System.currentTimeMillis() - start,
                resultB[0], resultC[0]);
    }

    /**
     * 模拟调用链路过程2：并行访问B和C服务，都完成后继续执行。
     */
    private static void syncAllOfDemo() throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        final String[] resultB = new String[1];
        final String[] resultC = new String[1];
        CompletableFuture.allOf(getFuture(resultB, helloB, requestB), getFuture(resultC, helloC, requestC))
                .get(1000, TimeUnit.MILLISECONDS);
        logger.info("B & C  cost = {}ms ,allOf response ::::: B = {} ,  C = {}", System.currentTimeMillis() - start,
                resultB[0], resultC[0]);
    }

    /**
     * 模拟调用链路过程3：并行调用B、C、D。等待全部结束后，串行调用E，返回结果后结束。 其中D服务做了降级处理。
     */
    private static void syncAllOfTimeoutDemo() throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        final String[] resultB = new String[1];
        final String[] resultC = new String[1];
        final String[] resultD = new String[1];
        CompletableFuture.allOf(getFuture(resultB, helloB, requestB), getFuture(resultC, helloC, requestC),
                getFuture(resultD, helloD, requestD)).get(2000, TimeUnit.MILLISECONDS);
        logger.info(" B & C & D  cost = {}ms , allOf response ::::: B = {} ,  C = {} ,D = {}",
                System.currentTimeMillis() - start, resultB[0], resultC[0], resultD[0]);
    }

    private static void syncCall(HelloAPI proxy, Hello.HelloReq request) {
        long start = System.currentTimeMillis();
        Hello.HelloRsp result = proxy.sayHello(new RpcClientContext(), request);
        logger.info("{} client call! cost = {}ms response ::::: id={} msg={} ", request.getId(),
                System.currentTimeMillis() - start, result.getId(), result.getMsg());
    }

    private static CompletableFuture<Void> getFuture(String[] result, HelloAPI proxy, Hello.HelloReq request) {

        return CompletableFuture.runAsync(() -> result[0] = proxy.sayHello(new RpcClientContext(), request).getMsg())
                .exceptionally((e -> {
                    //在实际业务中需要思考，根据错误类型进行降级。
                    logger.info("{} Service出现了异常:{} 类型为:{} ,开始模拟降级处理。", request.getId(), e.getMessage(),
                            e.getCause().getClass().getName());
                    result[0] = request.getId() + " exceptionally deal result.";
                    return null;
                }));
    }
}
