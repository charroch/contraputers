import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener, IClientChangeListener}
import com.android.ddmlib.{IDevice, AndroidDebugBridge, Client}
import play.api._

object Global extends GlobalSettings {

  def devices = AndroidDebugBridge.createBridge.getDevices

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

  override def onStart(app: Application) {
    Logger.info("Application has started")
    AndroidDebugBridge.addClientChangeListener(clientChangeListener)
    AndroidDebugBridge.addDebugBridgeChangeListener(debugBridgeListener)
    AndroidDebugBridge.addDeviceChangeListener(deviceChangeListener)
    AndroidDebugBridge.initIfNeeded(false)
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    AndroidDebugBridge.removeClientChangeListener(clientChangeListener)
    AndroidDebugBridge.removeDebugBridgeChangeListener(debugBridgeListener)
    AndroidDebugBridge.removeDeviceChangeListener(deviceChangeListener)
    AndroidDebugBridge.disconnectBridge()
    AndroidDebugBridge.terminate()
  }

}