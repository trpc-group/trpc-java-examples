package com.tencent.trpc.demo.scala

import com.tencent.trpc.core.logger.LoggerFactory
import com.tencent.trpc.core.rpc.{RpcClientContext, TRpcProxy}
import com.tencent.trpc.demo.api.Hello.HelloReq
import com.tencent.trpc.demo.api.HelloAPI
import com.tencent.trpc.server.container.TRPC

object TRpcScalaClient {

  private val logger = LoggerFactory.getLogger(TRpcScalaClient.getClass)
  private val request = HelloReq.newBuilder().setMsg("Hello tRPC-Java!")


  def main(args: Array[String]): Unit = {
    TRPC.start()
    val helloTrpc = TRpcProxy.getProxy("testTrpcClient", classOf[HelloAPI])
    logger.debug(helloTrpc.sayHello(new RpcClientContext(), request.build()).getMsg)
  }
}
