import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{Dist, outputDirectory, distJvmOptions}

object HelloKernelBuild extends Build {
  val Organization = "com.novoda.dab"
  val Version = "1.0.0"
  val ScalaVersion = "2.10.2"

  lazy val androidHome = SettingKey[File]("android-home", "root dir of android")

  lazy val DABKernel = Project(
    id = "dab-kernel",
    base = file("dab"),
    settings = defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Dependencies.helloKernel,
      distJvmOptions in Dist := "-Xms256M -Xmx1024M",
      outputDirectory in Dist := file("target/dab-dist"),
      androidHome := file(System.getenv("ANDROID_HOME")),
      resolvers ++= Seq(
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases" at "http://oss.sonatype.org/content/repositories/releases"
      ),
      unmanagedJars in Compile <<= androidHome map {
        androidHome: File => (androidHome / "tools/lib/" ** "ddm*").classpath
      }
    )
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    crossPaths := false,
    organizationName := "Novoda LTD",
    organizationHomepage := Some(url("http://www.novoda.com"))
  )

  lazy val defaultSettings = buildSettings ++ Seq(
    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
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

  server.dependsOn(DABKernel)
}

object Dependencies {

  import Dependency._

  val helloKernel = Seq(
    akkaKernel, akkaSlf4j, logback, specs
  )
}

object Dependency {

  // Versions
  object V {
    val Akka = "2.2.0"
  }

  val akkaKernel = "com.typesafe.akka" %% "akka-kernel" % V.Akka
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % V.Akka
  val logback = "ch.qos.logback" % "logback-classic" % "1.0.0"
  val specs = "org.specs2" %% "specs2" % "1.11"
}