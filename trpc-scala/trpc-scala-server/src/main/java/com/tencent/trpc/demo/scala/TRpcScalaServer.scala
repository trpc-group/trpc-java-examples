package com.tencent.trpc.demo.scala

import com.tencent.trpc.server.container.TRPC

object TRpcScalaServer {
  def main(args: Array[String]): Unit = {
    TRPC.start()
  }
}
