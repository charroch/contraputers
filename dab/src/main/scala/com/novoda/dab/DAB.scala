package com.novoda.dab

import com.android.ddmlib.IDevice
import com.novoda.dab.DAB.OnDevice

class DAB {

  implicit class RichDevice(device: IDevice) {
    import Default._
    def pp()(implicit printer: DevicePrinter) = printer.apply(_)

    def isRoot() = device.
  }

}

object DAB extends App {

  type OnDevice[T] = IDevice => T


  Console.println("Hello World: " + (args mkString ", "))
}


class DevicePrinter extends OnDevice[String] {
  def apply(device: IDevice): String = ???
}

object Default {
  implicit val console: DevicePrinter = _
}