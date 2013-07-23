package com.novoda.dab

import akka.actor.{Props, ActorSystem}
import akka.event.Logging
import com.novoda.dab.actors.{Start, DeviceManager}

object BonoboChat {

  val system = ActorSystem()

  val log = Logging(system, BonoboChat.getClass().getName())

  def main(args: Array[String]): Unit = run()

  def run() = {
    log.debug("Initializing chat system.")
    val manager = system.actorOf(Props[DeviceManager], "manager")
    manager ! Start
  }
}
