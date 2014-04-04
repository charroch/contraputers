package com.novoda.adb

import akka.actor.{ActorLogging, Actor, ActorSystem, Props}
import akka.event.Logging
import akka.kernel.Bootable
import java.util.concurrent.atomic.AtomicInteger
import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener, IClientChangeListener}
import com.android.ddmlib.{IDevice, AndroidDebugBridge, Client}

object DebugAndroidBridge {
}

class DebugAndroidBridge extends Bootable {
  val system = ActorSystem("dab")

  def startup = {
    val a = system.actorOf(DeviceActor.props("hello"))
    val b = system.actorOf(DeviceActor.props("hello"))
    a ! "one"
    b ! "one"
    a ! "one"
    b ! "one"
    a ! "one"
    a ! "one"
    b ! "one"
  }

  def shutdown = {
    system.shutdown()
  }
}

object DeviceActor {

  /**
   * Create Props for an actor of this type.
   * @param name The name to be passed to this actor’s constructor.
   * @return a Props for creating this actor, which can then be further configured
   *         (e.g. calling `.withDispatcher()` on it)
   */
  def props(serial: String): Props = Props(classOf[DeviceActor], serial)
}

class DeviceActor(name: String) extends Actor {

  val atom = new AtomicInteger(1);
  val system = ActorSystem()
  val log = Logging(system, DebugAndroidBridge.getClass().getName())

  def receive = {
    case x ⇒ {
      log.info(x.toString + atom.incrementAndGet())
      Thread.sleep(2000)
    }
  }
}

/**
 * Device manager manages all devices attached to this machine.
 */
class DeviceManager extends Actor with ActorLogging {

  val devices = Vector.empty[DeviceActor]

  val clientChangeListener = new IClientChangeListener {
    def clientChanged(client: Client, index: Int) {
      log.warning("Client changed: " + client)
    }
  }

  val debugBridgeListener = new IDebugBridgeChangeListener {
    def bridgeChanged(bridge: AndroidDebugBridge) {
      log.warning("Bridged changed: " + bridge + " and connection is: " + bridge.isConnected);
    }
  }

  val deviceChangeListener = new IDeviceChangeListener {
    def deviceConnected(device: IDevice) {
    }

    def deviceDisconnected(device: IDevice) {
    }

    def deviceChanged(device: IDevice, changeMask: Int) {
    }
  }

  def receive = {
    case Start => {
      log.info("Starting server")
    }
  }
}

sealed trait ManagerCommand

sealed trait Reply

case object Start extends ManagerCommand

case object Started extends Reply

object DeviceManager {
}


