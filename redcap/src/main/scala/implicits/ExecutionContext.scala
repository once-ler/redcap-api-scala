package com.eztier.redcap.implicits

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object ExecutionContext {
  implicit val system = ActorSystem("redcap-actor-system")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val logger = system.log
}