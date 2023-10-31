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

import com.tencent.trpc.core.common.ConfigManager;
import com.tencent.trpc.core.common.config.BackendConfig;
import com.tencent.trpc.core.common.config.ConsumerConfig;
import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.GenericClient;
import com.tencent.trpc.core.rpc.RpcClientContext;
import java.util.HashMap;
import java.util.Map;

/**
 * 不依赖配置文件的例子，是最原生的使用方式，是一种高级用法。普通业务不建议直接这样使用，但网关服务建议这种方式。
 */
public class HelloClientNoneConfigFile {

    private static final Logger logger = LoggerFactory.getLogger(HelloClientNoneConfigFile.class);

    // 对于网关服务，则从外部请求报文里则解析出SERVICE_URL、SERVICE_NAME和METHOD_NAME,本demo仅固定值来验证
    private static final String SERVICE_URL = "ip://127.0.0.1:8090";
    private static final String SERVICE_NAME = "trpc.test.demo.Hello";
    private static final String METHOD_NAME = "SayHello";

    public static void main(String[] args) throws Exception {
        try {
            // 1.init&start tRPC
            ConfigManager.getInstance().start();

            //2.发起请求
            //同步调用、json序列化、原生trpc协议
            send(false, true);
            //同步调用、无序列化、原生trpc协议
            send(false, false);
            //异步调用、json序列化、原生trpc协议
            send(true, true);
            //异步调用、无序列化、原生trpc协议
            send(true, false);

        } finally {
            //结束demo
            GatewayClient.stop();
        }
    }

    private static void send(boolean isAsync, boolean isJsonSerialization) {
        //请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("msg", "testSend");
        String requestString = "{\"msg\":\"testSendByte\"}";

        //context赋值
        RpcClientContext context = new RpcClientContext();
        context.setRpcServiceName(SERVICE_NAME);
        context.setRpcMethodName(METHOD_NAME);

        // 服务接口配置
        BackendConfig backendConfig = new BackendConfig();
        backendConfig.setNamingUrl(SERVICE_URL);
        backendConfig.setSerialization("json");

        //json序列化
        if (isJsonSerialization) {
            ConsumerConfig<GatewayAPI> consumerConfig = new ConsumerConfig<>();
            consumerConfig.setServiceInterface(GatewayAPI.class);
            // 获取Client反向代理
            GatewayAPI proxy = backendConfig.getProxy(consumerConfig);
            if (!isAsync) {
                //同步方式
                GatewayClient.syncSend(context, proxy, requestMap);
            } else {
                //异步方式
                GatewayClient.asyncSend(context, proxy, requestMap);
            }
        } else {
            //无序列化
            ConsumerConfig<GenericClient> consumerConfig = new ConsumerConfig<>();
            consumerConfig.setServiceInterface(GenericClient.class);
            // 获取Client反向代理
            GenericClient proxy = backendConfig.getProxy(consumerConfig);
            if (!isAsync) {
                //同步方式
                GatewayClient.syncSendByte(context, proxy, requestString);
            } else {
                //异步方式
                GatewayClient.asyncSendByte(context, proxy, requestString);
            }
        }
    }

}

