package controllers

import play.api.mvc.{WebSocket, Action, Controller}
import com.android.ddmlib.{IDevice, RawImage, AndroidDebugBridge}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import adb.Bridge
import com.android.ddmlib.log.LogReceiver
import com.android.ddmlib.log.LogReceiver.{LogEntry, ILogListener}
import java.io.OutputStream

object Device extends Controller {

  val toDevice: (IDevice) => DeviceView = device =>
    new DeviceView(
      device.getSerialNumber,
      device.getProperty("dhcp.wlan0.ipaddress"),
      device.getProperty("net.hostname"),
      device.getProperty("ro.build.version.release"),
      device.getProperty("ro.build.version.sdk"),
      device.getProperty("ro.product.manufacturer"),
      device.getProperty("ro.product.model")
    )

  def index(serial: String) = Action {
    Ok(views.html.device(Bridge.devices.find(_.getSerialNumber == serial).map(toDevice).get))
  }

  def listener(o: OutputStream): LogReceiver.ILogListener = new ILogListener {
    def newEntry(p1: LogEntry) {
    }

    def newData(p1: Array[Byte], p2: Int, p3: Int) {
      o.write(p1, p2, p3)
    }
  }

  def logcat(serial: String) = WebSocket.using[Array[Byte]] {
    request =>
      val enumerator = Enumerator.outputStream {
        os =>
          val d = AndroidDebugBridge.createBridge.getDevices.find(_.getSerialNumber == serial).get
          d.runEventLogService(new LogReceiver(listener(os)))
      }
      val in = Iteratee.consume[Array[Byte]]()
      val out = enumerator >>> Enumerator.eof
      (in, out)
  }


  def screenshot(serial: String) = Action {
    val route = routes.Device.screenshotRaw(serial)
    val html = s"<img src='$route' class='img-polaroid'>"
    Ok(html)
  }

  def screenshotRaw(serial: String) = Action {
    val rawImage = AndroidDebugBridge.createBridge.getDevices.find(_.getSerialNumber == serial).map(_.getScreenshot).get
    val enumerator = Enumerator.outputStream {
      os =>
        ImageIO.write(image(rawImage), "png", os);
        os.close()
    }
    Ok.chunked(enumerator >>> Enumerator.eof).as("image/jpeg")
  }

  def image(raw: RawImage) = {
    val image = new BufferedImage(raw.width, raw.height, BufferedImage.TYPE_INT_ARGB)
    var index = 0;
    var IndexInc = raw.bpp >> 3;
    for (y <- 0 until raw.height) {
      for (x <- 0 until raw.width) {
        var value = raw.getARGB(index);
        index += IndexInc;
        image.setRGB(x, y, value);
      }
    }
    image
  }

}
