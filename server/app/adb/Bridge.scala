package adb

import com.android.ddmlib.AndroidDebugBridge

object Bridge {
  def devices = AndroidDebugBridge.createBridge.getDevices
}
