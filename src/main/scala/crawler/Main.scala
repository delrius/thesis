package crawler

import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import akka.actor.ReceiveTimeout
import com.typesafe.scalalogging.slf4j.LazyLogging
import readers.PdfBoxReader
import utils.FileUtils
import spring.Runner

class Main extends Actor with LazyLogging {

  import Receptionist._

  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  Runner.run()

  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2009_99/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2008_86/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2007_73/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2005_36/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2003_21/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2002_19-20/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2000_18/index.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/1999_16/index.html")

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
    //      for (x <- links) {
    //        val trimmed = trimToFileName(x)
    //        logger.info("Writing " + trimmed + " to disk....")
    //        val content = new URL(x).asInput.bytes
    //        Path("src", "main", "resources", "readers", folder, trimmed).write(content)
    //        logger.info("Finished " + trimmed)
    //      }

    PdfBoxReader.clear()

    for (x <- links) {
      val trimmed = trimToFileName(x)
      FileUtils.clean(trimmed)
      PdfBoxReader.readFile(folder, trimmed)
    }

    Runner.runPerson(PdfBoxReader.refs)
  }

  def trimRootResource(url: String): String = {
    val lastSlash = url.lastIndexOf("/")
    val naukmaAnchor = url.toLowerCase.indexOf("comp") + "comp".size + 1
    url.substring(naukmaAnchor, lastSlash)
  }

  def trimToFileName(url: String): String = {
    val slIndex = url.lastIndexOf("/")
    if (slIndex < 0) url else url.substring(slIndex + 1)
  }

  def applyFilter(links: Set[String]): Set[String] = {
    def nameFilter(a: String) = a.contains("_authors") || a.contains("_content") || a.contains("_preface") || a.contains("_abstract") || a.contains("_autors") || a.contains("_prefase") || a.contains("abstr")
    links filterNot nameFilter
  }

  override def postStop(): Unit = {
    AsyncWebClient.shutdown()
  }
}