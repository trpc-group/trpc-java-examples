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
import com.tencent.trpc.core.rpc.RpcClientContext;
import com.tencent.trpc.core.rpc.TRpcProxy;
import com.tencent.trpc.server.container.TRPC;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于trpc-java.yaml配置的Client Demo。
 */
public class HelloClientOnYaml {

    private static final Logger logger = LoggerFactory.getLogger(HelloClientOnYaml.class);
    // 对于网关服务，则从外部请求报文里则解析出SERVICE_NAME和METHOD_NAME,本demo仅固定值来验证
    private static final String SERVICE_NAME = "trpc.test.demo.Hello";
    private static final String METHOD_NAME = "SayHello";

    public static void main(String[] args) throws Exception {
        try {
            //1、init&start tRPC 配置，主要是解析trpc-java.yaml
            TRPC.start();

            // 2、同步调用例子（更多用例请参考HelloClientNoneConfigFile.java，
            //              对于网关服务更适合使用HelloClientNoneConfigFile的模式，因为无须依赖trpc-java.yaml）
            RpcClientContext context = new RpcClientContext();
            context.setRpcServiceName(SERVICE_NAME);
            context.setRpcMethodName(METHOD_NAME);
            GatewayAPI helloAPI = TRpcProxy.getProxy(SERVICE_NAME, GatewayAPI.class);

            //请求参数
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("msg", "testSend");

            //发起调用
            GatewayClient.syncSend(context, helloAPI, requestMap);

        } finally {
            //结束demo
            GatewayClient.stop();
        }
    }

}

