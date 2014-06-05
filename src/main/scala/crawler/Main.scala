package crawler

import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import akka.actor.ReceiveTimeout
import com.typesafe.scalalogging.slf4j.LazyLogging
import java.net.URL
import scalax.io.JavaConverters._
import scalax.file.Path
import readers.PdfBoxReader
import utils.FileUtils
import spring.Runner

class Main extends Actor with LazyLogging {

  import Receptionist._

  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  //  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/texts.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2009_99/index.html")
  //  receptionist ! Get("http://www.google.com/2")
  //  receptionist ! Get("http://www.google.com/3")
  //  receptionist ! Get("http://www.google.com/4")

  context.setReceiveTimeout(10.seconds)

  def receive = {
    case Result(url, set) => {
      val links = applyFilter(set - url)
      logger.info(links.toVector.sorted.map(trimToFileName(_)).mkString(s"Results for '$url':\n", "\n", "\n"));
      for (x <- links) {
        val trimmed = trimToFileName(x)
        logger.info("Writing " + trimmed + " to disk....")
        val content = new URL(x).asInput.bytes
        Path("src", "main", "resources", "readers", trimmed).write(content)
        logger.info("Finished " + trimmed)
      }
      for (x <- links) {
        val trimmed = trimToFileName(x)
        FileUtils.clean(trimmed)
        PdfBoxReader.readFile(trimmed)
      }

      Runner.main(new Array[String](0))
    }
    case Failed(url) =>
      println(s"Failed to fetch '$url'\n")
    case ReceiveTimeout =>
      context.stop(self)
  }

  def trimToFileName(url: String): String = {
    val slIndex = url.lastIndexOf("/")
    if (slIndex < 0) url else url.substring(slIndex + 1)
  }

  //TODO: extract to config
  def applyFilter(links: Set[String]) : Set[String] = {
    def nameFilter (a:String) = a.contains("_authors") || a.contains("_contents") || a.contains("_preface") || a.contains("_abstract")
    links filterNot nameFilter
  }


  override def postStop(): Unit = {
    AsyncWebClient.shutdown()
  }

}