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

package com.tencent.trpc.demo.gatewayclient;

import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.GenericClient;
import com.tencent.trpc.core.rpc.RpcClientContext;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Demo Client共同逻辑类，是发送同步请求和异步请求的逻辑。
 */
public class GatewayClient {

    private static final Logger logger = LoggerFactory.getLogger(GatewayClient.class);

    public static Map syncSend(RpcClientContext context, GatewayAPI gatewayAPI, Map<String, Object> request) {
        try {
            // 同步调用
            Map result = gatewayAPI.invoke(context, request);
            logger.info("tRPC Sync Call! request={} . response={}", request, result);
            return result;
        } catch (Exception e) {
            logger.error("tRPC Sync Call error:{}", e);
            throw e;
        }
    }

    public static CompletionStage<Map> asyncSend(RpcClientContext context, GatewayAPI gatewayAPI,
            Map<String, Object> request) {
        try {
            // 异步调用
            CompletionStage<Map> future = gatewayAPI.asyncInvoke(context, request);
            // 默认使用fork join pool 线程组处理后续逻辑。
            future.thenAccept(r -> logger.info("tRPC Async Call! request={} . response={}", request, r)
            );
            return future;
        } catch (Exception e) {
            logger.error("tRPC Async Call error:{}", e);
            throw e;
        }
    }

    public static byte[] syncSendByte(RpcClientContext context, GenericClient genericClient, String request) {
        // 同步调用
        try {
            byte[] result = genericClient.invoke(context, request.getBytes());
            logger.info("tRPC Sync Byte Call! request={} . response={}", request, new String(result));
            return result;
        } catch (Exception e) {
            logger.error("tRPC Sync Byte Call error:{}", e);
            throw e;
        }
    }

    public static CompletionStage<byte[]> asyncSendByte(RpcClientContext context, GenericClient genericClient,
            String request) {
        try {
            //异步调用
            CompletionStage<byte[]> future = genericClient.asyncInvoke(context, request.getBytes());
            // 默认使用fork join pool 线程组处理后续逻辑。
            future.thenAccept(r -> logger.info("tRPC Async Byte Call! request={} . response={}", request, new String(r))
            );
            return future;
        } catch (Exception e) {
            logger.error("tRPC Async Byte Call error:{}", e);
            throw e;
        }
    }

    public static void stop() throws InterruptedException {
        //模拟等待一段时间,待异步调用结束后再退出。实际使用时，不建议使用此方式。
        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }

}

