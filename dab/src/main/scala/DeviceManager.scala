package com.novoda.dab.actors

import akka.actor.{ActorLogging, Actor}
import akka.event.Logging
import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener, IClientChangeListener}
import com.android.ddmlib.{IDevice, AndroidDebugBridge, Client}

class DeviceManager extends Actor with ActorLogging {

  val clientChangeListener = new IClientChangeListener {
    def clientChanged(client: Client, index: Int) {
    }
  }

  val debugBridgeListener = new IDebugBridgeChangeListener {
    def bridgeChanged(bridge: AndroidDebugBridge) {
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
