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

package com.tencent.trpc.demo.exception.handler;

import com.tencent.trpc.core.exception.TRpcException;
import com.tencent.trpc.spring.exception.annotation.EnableTRpcHandleException;
import com.tencent.trpc.spring.exception.annotation.TRpcExceptionHandler;
import com.tencent.trpc.spring.exception.annotation.TRpcExceptionHandlerRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@TRpcExceptionHandlerRegistry
@EnableTRpcHandleException
public class ExceptionHandlerConfiguration {

    @TRpcExceptionHandler
    public Response handleCustomizeException(CustomizeException e) {
        return Response.of("10001", "customize exception");
    }

    @TRpcExceptionHandler
    public Response handleTRpcException(TRpcException e) {
        int code = e.isFrameException() ? e.getCode() : e.getBizCode();
        return Response.of(String.valueOf(code), e.getMessage());
    }

    @TRpcExceptionHandler
    public Response handleException(Exception e) {
        return Response.of("999", "system error");
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Response {

        private String id;
        private String msg;
    }
}
