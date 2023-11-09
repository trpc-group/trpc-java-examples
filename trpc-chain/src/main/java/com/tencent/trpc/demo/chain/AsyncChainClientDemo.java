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
import com.tencent.trpc.demo.api.HelloAsyncAPI;
import com.tencent.trpc.server.container.TRPC;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * 模拟调用链路过程1：串行调用A，返回后并行调用B和C，等待BC都结束后，调用E，E返回结果后结束。
 * <p>
 * 模拟调用链路过程2：串行调用A，返回后并行调用B和C，等待BC任何一个服务响应后，调用E，E返回结果后结束。
 * <p>
 * 模拟调用链路过程3：并行调用A、B、C。等待结果后，串行调用D，返回结果后结束。
 * <p>
 * 此类参考：chainDemo1()  chainDemo2()  chainDemo3() 方法的实现。  因为init()方法很原始，不建议在工程中直接使用，因为可靠性比较弱，建议用北极星等方式使用client。
 */
public class AsyncChainClientDemo {

    private static final Logger logger = LoggerFactory.getLogger(AsyncChainClientDemo.class);

    private static HelloAsyncAPI asyncHelloA;
    private static Hello.HelloReq requestA;

    private static HelloAsyncAPI asyncHelloB;
    private static Hello.HelloReq requestB;

    private static HelloAsyncAPI asyncHelloC;
    private static Hello.HelloReq requestC;

    private static HelloAsyncAPI asyncHelloD;
    private static Hello.HelloReq requestD;

    private static HelloAsyncAPI asyncHelloE;
    private static Hello.HelloReq requestE;



    public static void main(String[] args) throws Exception {
        // start tRPC，主要是解析trpc-java.yaml,启动模拟的服务。
        TRPC.start();
        asyncDemo();

        TimeUnit.MILLISECONDS.sleep(2000);
    }

    private static void init() {
        // 通过${name}在trpc-java.yaml中配置 获取 tRPC 容器中的 HelloAPI Client 代理
        String msg = "Hello tRPC-Java!";
        asyncHelloA = TRpcProxy.getProxy("asyncHelloA");
        requestA = Hello.HelloReq.newBuilder().setId("A").setMsg(msg).setCost(10).build();

        asyncHelloB = TRpcProxy.getProxy("asyncHelloB");
        requestB = Hello.HelloReq.newBuilder().setId("B").setMsg(msg).setCost(30).build();

        asyncHelloC = TRpcProxy.getProxy("asyncHelloC");
        requestC = Hello.HelloReq.newBuilder().setId("C").setMsg(msg).setCost(50).build();

        asyncHelloD = TRpcProxy.getProxy("asyncHelloD");
        requestD = Hello.HelloReq.newBuilder().setId("D").setMsg(msg).setCost(1200).build();

        asyncHelloE = TRpcProxy.getProxy("asyncHelloE");
        requestE = Hello.HelloReq.newBuilder().setId("E").setMsg(msg).setCost(20).build();
    }

    /**
     *
     */
    public static void asyncDemo() throws ExecutionException, InterruptedException, TimeoutException {
        init();
        // 异步调用helloA  等待返回或超时后处理后续逻辑
        asyncCall(asyncHelloA, requestA);
        // 模拟调用链路过程1：并行访问B和C服务，任何一个服务调用完成后，继续执行。
        asyncAnyOfDemo();
        // 模拟调用链路过程2：并行访问B和C服务，都完成后继续执行。
        asyncAllOfDemo();
        // 模拟调用链路过程3：并行调用B、C、D。等待全部结束后，串行调用E，返回结果后结束。 其中D服务做了降级处理。
        asyncAllOfTimeoutDemo();
        // 异步调用helloE
        asyncCall(asyncHelloE, requestE);
    }

    /**
     * 模拟调用链路过程1：并行访问B和C服务，任何一个服务调用完成后，继续执行。
     */
    private static void asyncAnyOfDemo() throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        final String[] resultB = new String[1];
        final String[] resultC = new String[1];
        CompletableFuture.anyOf(getFuture(resultB, asyncHelloB, requestB), getFuture(resultC, asyncHelloC, requestC))
                .get(1000, TimeUnit.MILLISECONDS);
        //一个为空，一个有数据。
        logger.info("B & C  cost = {}ms ,anyOf response ::::: B = {} ,  C = {}", System.currentTimeMillis() - start,
                resultB[0], resultC[0]);
    }

    /**
     * 模拟调用链路过程2：并行访问B和C服务，都完成后继续执行。
     */
    private static void asyncAllOfDemo() throws InterruptedException, ExecutionException, TimeoutException {
        final long start = System.currentTimeMillis();
        final String[] resultB = new String[1];
        final String[] resultC = new String[1];
        CompletableFuture.allOf(getFuture(resultB, asyncHelloB, requestB), getFuture(resultC, asyncHelloC, requestC))
                .get(1000, TimeUnit.MILLISECONDS);
        logger.info("B & C  cost = {}ms ,allOf response ::::: B = {} ,  C = {}", System.currentTimeMillis() - start,
                resultB[0], resultC[0]);
    }

    /**
     * 模拟调用链路过程3：并行调用B、C、D。等待全部结束后，串行调用E，返回结果后结束。 其中D服务做了降级处理。
     */
    private static void asyncAllOfTimeoutDemo() throws InterruptedException, ExecutionException, TimeoutException {
        final long start = System.currentTimeMillis();
        final String[] resultB = new String[1];
        final String[] resultC = new String[1];
        final String[] resultD = new String[1];
        CompletableFuture.allOf(getFuture(resultB, asyncHelloB, requestB), getFuture(resultC, asyncHelloC, requestC),
                getFuture(resultD, asyncHelloD, requestD)).get(2000, TimeUnit.MILLISECONDS);
        logger.info(" B & C & D  cost = {}ms , allOf response ::::: B = {} ,  C = {} ,D = {}",
                System.currentTimeMillis() - start, resultB[0], resultC[0], resultD[0]);
    }


    private static void asyncCall(HelloAsyncAPI asyncHello, Hello.HelloReq request)
            throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        final String[] result = new String[1];
        asyncHello.sayHello(new RpcClientContext(), request)
                .thenAccept(v -> result[0] = v.getMsg()).toCompletableFuture().get(1000, TimeUnit.MILLISECONDS);
        logger.info("{} client call! cost = {} response :::::  msg={} ", request.getId(),
                System.currentTimeMillis() - start, result);
    }

    private static CompletableFuture<Hello.HelloRsp> getFuture(String[] result, HelloAsyncAPI proxy,
            Hello.HelloReq request) {

        return proxy.sayHello(new RpcClientContext(), request)
                .whenComplete((helloRsp, throwable) -> result[0] = helloRsp.getMsg()).exceptionally((e -> {
                    //在实际业务中需要思考，根据错误类型进行降级。 另外需要处理降级失败的逻辑。
                    logger.info("{} Service出现了异常:{} 类型为:{} ,开始模拟降级处理。", request.getId(), e.getMessage(),
                            e.getCause().getClass().getName());
                    result[0] = request.getId() + " exceptionally deal result.";
                    return null;
                })).toCompletableFuture();
    }
}