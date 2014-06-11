package crawler

import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import akka.actor.ReceiveTimeout
import com.typesafe.scalalogging.slf4j.LazyLogging
import readers.PdfBoxReader
import utils.{ConfigReader, FileUtils}
import spring.Runner
import scalax.file.Path
import java.net.URL
import scalax.io.JavaConverters._

class Main extends Actor with LazyLogging {

  import Receptionist._

  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  Runner.run()

  println("Writing output to log file")

  for (link <- ConfigReader.rootLinksList) {
    receptionist ! Get(link)
  }

  context.setReceiveTimeout(10.seconds)

  def receive = {
    case Result(url, set) =>
      processResult(url, set);
    case Failed(url) =>
      println(s"Failed to fetch '$url'\n")
    case ReceiveTimeout =>
      context.stop(self)
  }

  def processResult(url: String, set: Set[String]) = {
    val links = applyFilter(set - url)
    logger.info("\n")
    logger.info(links.toVector.sorted.map(trimToFileName).mkString(s"Results for '$url':\n", "\n", "\n"))
    val folder = trimRootResource(url)
    for (x <- links) {
      val trimmed = trimToFileName(x)
      logger.info("Writing " + trimmed + " to disk....")
      val content = new URL(x).asInput.bytes
      Path("tmp", folder, trimmed).write(content)
      logger.info("Finished " + trimmed)
    }

    PdfBoxReader.clear()

    for (x <- links) {
      val trimmed = trimToFileName(x)
      FileUtils.clean(trimmed)
      Path("refs", folder, trimmed+".txt").write("Starting ... \n\n")
      PdfBoxReader.readFile(folder, trimmed)
    }

    Runner.runPerson(PdfBoxReader.refs)
  }

  def trimRootResource(url: String): String = {
    val lastSlash = url.lastIndexOf("/")
    val prevLastSlash = url.substring(0, lastSlash).lastIndexOf("/")
    url.substring(prevLastSlash + 1, lastSlash)
  }

  def trimToFileName(url: String): String = {
    val slIndex = url.lastIndexOf("/")
    if (slIndex < 0) url else url.substring(slIndex + 1)
  }

  def applyFilter(links: Set[String]): Set[String] = {
    def nameFilter(a: String) = {
      val excluded = ConfigReader.excludedPrefixes
      excluded.filter(a.contains(_)).nonEmpty
    }

    links filterNot nameFilter
  }

  override def postStop(): Unit = {
    AsyncWebClient.shutdown()
  }
}