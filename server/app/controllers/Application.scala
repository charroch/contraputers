package controllers

import play.api._
import play.api.mvc._
import adb.Bridge
import com.android.ddmlib.IDevice

object Application extends Controller {

  val toDevice: (IDevice) => Device = device =>
    new Device(
      device.getSerialNumber,
      device.getProperty("dhcp.wlan0.ipaddress"),
      device.getProperty("net.hostname"),
      device.getProperty("ro.build.version.release"),
      device.getProperty("ro.build.version.sdk"),
      device.getProperty("ro.product.manufacturer"),
      device.getProperty("ro.product.model")
    )

  def index = Action {
    Ok(views.html.devices(Bridge.devices.map(toDevice)))
  }
}

case class Device(val serial: String, val IP: String, val hostname: String, val release: String, val SDK: String, val manufacturer: String, val model: String)
