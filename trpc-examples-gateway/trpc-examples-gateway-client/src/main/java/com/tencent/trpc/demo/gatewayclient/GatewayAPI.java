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

import com.tencent.trpc.core.rpc.RpcClientContext;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * 通用的请求接口
 */
public interface GatewayAPI {

    /**
     * @param context tRPCContext信息
     * @param body 请求PB
     * @return Map PB对象
     */
    Map invoke(RpcClientContext context, Map<String, Object> body);

    /**
     * @param context tRPCContext信息
     * @param body 请求PB
     * @return CompletionStage PB对象
     */
    CompletionStage<Map> asyncInvoke(RpcClientContext context, Map<String, Object> body);

}
