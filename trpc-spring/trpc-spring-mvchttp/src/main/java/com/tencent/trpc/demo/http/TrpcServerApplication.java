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

package com.tencent.trpc.demo.http;

import com.tencent.trpc.spring.boot.starters.annotation.EnableTRpc;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 基于SpringBoot配置得服务
 */
@SpringBootApplication
@EnableTRpc
public class TrpcServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(TrpcServerApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
