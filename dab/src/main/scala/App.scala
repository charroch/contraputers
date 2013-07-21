package com.novoda

import com.android.hierarchyviewerlib.device.DeviceBridge

/** The launched conscript entry point */
class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(App.run(config.arguments))
  }
}

object App {
  /** Shared by the launched version and the runnable version,
   * returns the process status code */
  def run(args: Array[String]): Int = {
    DeviceBridge.initDebugBridge("/opt/android-sdk-linux/platform-tools/adb")
    println("Hello World: " + args.mkString(" ") )

    //DeviceBridge.getDevices.foreach(d => d.getProperty()

    0
  }
  /** Standard runnable class entrypoint */
  def main(args: Array[String]) {
    System.exit(run(args))
  }
}

case class Exit(val code: Int) extends xsbti.Exit
