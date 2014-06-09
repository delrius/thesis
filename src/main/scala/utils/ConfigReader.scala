package utils

import com.typesafe.config.{Config, ConfigFactory}
import java.io.File
import scala.collection.JavaConversions._

object ConfigReader {
  val conf: Config = ConfigFactory.load()

  val fileConf: Config = ConfigFactory.parseFile(new File("application.conf"))

  val namespace: String = "naukma.app"

  def conf[T](name: String)(f: (Config, String) => T): T = {
    val hasPath = fileConf.hasPath(namespace)
    if (hasPath) {
      val config = fileConf.getConfig(namespace)
      if (config.hasPath(name)) {
        f(config, name)
      } else {
        f(conf.getConfig(namespace), name)
      }
    } else {
      f(conf.getConfig(namespace), name)
    }
  }

  def stringConf(name: String) = conf[String](name) { (conf, name) => conf.getString(name)}
  def stringListConf(name: String) = conf[List[String]](name) { (conf, name) => conf.getStringList(name).toList}
  def objListConf(name: String) = conf[List[List[String]]](name) { (conf, name) => conf.getAnyRefList(name).asInstanceOf[java.util.List[List[String]]].toList}
  def objListConfJava(name: String) = conf[java.util.List[java.util.List[String]]](name) { (conf, name) => conf.getAnyRefList(name).asInstanceOf[java.util.List[java.util.List[String]]] }


  lazy val downloadCondition = stringConf("downloadCondition")
  lazy val anchorTagFinder = stringConf("anchorTagFinder")
  lazy val hrefPartFinder = stringConf("hrefPartFinder")
  lazy val rootLinksList = stringListConf("rootLinksList")
  lazy val authorVariationsJava = objListConfJava("authorVariations")
}