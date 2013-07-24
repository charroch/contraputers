package controllers

import play.api.mvc.{WebSocket, Action, Controller}
import com.android.ddmlib.{IShellOutputReceiver, IDevice, RawImage, AndroidDebugBridge}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import adb.Bridge
import com.android.ddmlib.log.{EventLogParser, LogReceiver}
import com.android.ddmlib.log.LogReceiver.{LogEntry, ILogListener}
import java.io.OutputStream
import java.nio.charset.Charset
import com.android.ddmlib.log.EventContainer.EventValueType
import com.android.ddmlib.logcat.LogCatReceiverTask
import play.mvc.Http.Request
import play.mvc.BodyParser.AnyContent
import play.mvc.Result
import org.h2.engine.User

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
    implicit request =>
      Ok(views.html.device(Bridge.devices.find(_.getSerialNumber == serial).map(toDevice).get))
  }

  def logcatreceiver(o: OutputStream) = new IShellOutputReceiver {

    def addOutput(data: Array[Byte], offset: Int, length: Int) {
      o.write(data, offset, length);
    }

    def flush() {
      o.flush()
    }

    def isCancelled: Boolean = false
  }

  def logcat(serial: String) = WebSocket.using[String] {
    request =>
      val enumerator = Enumerator.outputStream {
        os =>
          val d = AndroidDebugBridge.createBridge.getDevices.find(_.getSerialNumber == serial).get
          d.executeShellCommand("logcat -v long", logcatreceiver(os), 0)
      }.map(new String(_, Charset.forName("utf-8")).replace("\n", "<br />\n"))
      val in = Iteratee.consume[String]()
      val out = enumerator
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

//
//trait DeviceAction {
//  def Authenticated(f: (IDevice, Request[AnyContent]) => Result) = {
//    Action {
//      request =>
//    }
//  }
//}
