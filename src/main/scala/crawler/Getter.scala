package crawler

import akka.actor.Actor
import akka.pattern.pipe
import java.util.concurrent.Executor
import akka.actor.Status
import scala.concurrent.ExecutionContext
import utils.ConfigReader

object Getter {
  case object Done
  case object Abort
}

class Getter(url: String, depth: Int) extends Actor {
  import Getter._

  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  def client: WebClient = AsyncWebClient

  client get url pipeTo self

  def prepareLink(body: String): String = {
    val str = url.substring(0, url.lastIndexOf("/")+1)
    str + body
  }

  def receive = {
    case body: String =>
      for (link <- findLinks(body))
          context.parent ! Controller.Check(prepareLink(link), depth)
      stop()
    case _: Status.Failure => stop()
    case Abort             => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }

  val A_TAG = ConfigReader.anchorTagFinder.r
  val HREF_ATTR = ConfigReader.hrefPartFinder.r

  def findLinks(body: String): Iterator[String] = {
    for {
      anchor <- A_TAG.findAllMatchIn(body)
      HREF_ATTR(dquot, quot, bare) <- anchor.subgroups
    } yield if (dquot != null) dquot
    else if (quot != null) quot
    else bare
  }

}