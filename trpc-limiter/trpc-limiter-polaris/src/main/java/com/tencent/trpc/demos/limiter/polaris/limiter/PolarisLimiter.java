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

package com.tencent.trpc.demos.limiter.polaris.limiter;

import com.tencent.polaris.ratelimit.api.core.LimitAPI;
import com.tencent.polaris.ratelimit.api.rpc.QuotaRequest;
import com.tencent.polaris.ratelimit.api.rpc.QuotaResponse;
import com.tencent.polaris.ratelimit.api.rpc.QuotaResultCode;
import com.tencent.polaris.ratelimit.factory.LimitAPIFactory;
import com.tencent.trpc.core.common.ConfigManager;
import com.tencent.trpc.core.filter.spi.Filter;
import com.tencent.trpc.core.logger.Logger;
import com.tencent.trpc.core.logger.LoggerFactory;
import com.tencent.trpc.core.rpc.Invoker;
import com.tencent.trpc.core.rpc.Request;
import com.tencent.trpc.core.rpc.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class PolarisLimiter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PolarisLimiter.class);

    private final LimitAPI limitAPI = LimitAPIFactory.createLimitAPI();

    @Override
    public CompletionStage<Response> filter(Invoker<?> filterChain, Request req) {
        QuotaRequest quotaRequest = new QuotaRequest();
        // 设置 namespace 为全局的 namespace
        quotaRequest.setNamespace(ConfigManager.getInstance().getGlobalConfig().getNamespace());
        // 被调北极星服务名
        quotaRequest.setService("trpc.trpc_java.trpc_java_demo.TestPolarisTRPC");
        quotaRequest.setCount(1);
        Map<String, String> labels = new HashMap();
        // method 维度限流，对所有方法都限流
        labels.put("method", ".*");
        // 根据 labels 查询北极星控制台限流规则，判断是否应该限流或者放通
        quotaRequest.setLabels(labels);
        QuotaResponse quotaResponse = limitAPI.getQuota(quotaRequest);
        if (QuotaResultCode.QuotaResultOk == quotaResponse.getCode()) {
            return filterChain.invoke(req);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Filter.super.getOrder();
    }
}
