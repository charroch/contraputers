import sbt._
import sbt.Keys._

object DabBuild extends Build {

  lazy val androidHome = SettingKey[File]("android-home", "root dir of android")

  lazy val DAB = Project(
    id = "dab",
    base = file("dab"),
    settings = Project.defaultSettings ++ conscript.Harness.conscriptSettings ++ Seq(
      name := "dab",

      organization := "com.novoda",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.2",
      androidHome := file(System.getenv("ANDROID_HOME")),
      resolvers ++= Seq(
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases" at "http://oss.sonatype.org/content/repositories/releases"
      ),
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2" % "1.11",
        "com.typesafe.akka" %% "akka-actor" % "2.2.0"
      ),
      unmanagedJars in Compile <<= androidHome map {
        androidHome: File => (androidHome / "tools/lib/" ** (
          "monkeyrunner.jar" || "chimpchat.jar" || "hierarchyviewer*" || "guava*" || "ddm*" || "swt*")).classpath
      }
    )
  )

  import sbt._
  import Keys._
  import play.Project._

  val appName = "server"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    cache
  )


  val server = play.Project(appName, appVersion, appDependencies, file("server")).settings(
    androidHome := file(System.getenv("ANDROID_HOME")),
    unmanagedJars in Compile <<= androidHome map {
      androidHome: File => (androidHome / "tools/lib/" ** "ddm*").classpath
    }
  )

}
