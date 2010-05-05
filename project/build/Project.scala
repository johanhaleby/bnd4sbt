import sbt._
import de.element34.sbteclipsify._

class Bnd4SbtPluginProject(info: ProjectInfo) extends PluginProject(info) with Eclipsify {

  lazy val aquteRepo = "aQute Repository" at "http://www.aqute.biz/repo"

  lazy val bnd = "biz.aQute" % "bndlib" % "0.0.384"
  lazy val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  lazy val junit = "junit" % "junit" % "4.7" % "test"
  lazy val mockito = "org.mockito" % "mockito-all" % "1.8.4" % "test"
}
