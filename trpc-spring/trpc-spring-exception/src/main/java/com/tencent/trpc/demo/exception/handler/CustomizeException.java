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

public class CustomizeException extends RuntimeException {

    private int code;

    public CustomizeException() {
    }

    private CustomizeException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    private CustomizeException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public static CustomizeException newException(int code, String message,
            Throwable cause) {
        return new CustomizeException(code, message, cause);
    }

    public static CustomizeException newException(int code, String message) {
        return new CustomizeException(code, message);
    }


    public String getCode() {
        return String.valueOf(code);
    }

    public void setCode(int code) {
        this.code = code;
    }
}
