package com.tencent.trpc.demo.scala.service

import com.tencent.trpc.core.logger.{Logger, LoggerFactory}
import com.tencent.trpc.core.rpc.{RpcContext, RpcServerContext}
import com.tencent.trpc.demo.api.{Hello, HelloAPI}

class HelloServiceImpl extends HelloAPI{

  private val logger: Logger = LoggerFactory.getLogger(classOf[HelloServiceImpl])

  override def sayHello(context: RpcContext, request: Hello.HelloReq): Hello.HelloRsp = {
    val serverContext =  context.asInstanceOf[RpcServerContext]
    logger.info(classOf[HelloServiceImpl].getName + "receive:{}, context:{}", request, serverContext)
    val repBuilder = Hello.HelloRsp.newBuilder()
    repBuilder.setMsg("receive: " + request.getMsg)
    repBuilder.build()
  }
}
