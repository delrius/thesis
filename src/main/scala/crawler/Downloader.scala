package crawler

import akka.actor.Actor
import akka.pattern.pipe
import java.util.concurrent.Executor
import akka.actor.Status
import scala.concurrent.{Promise, ExecutionContext}
import scala.io.Source
import scalax.io._

object Downloader {
  case object Done
  case object Abort
}

class Downloader(url: String) extends Actor {
  import Getter._

//  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
//  def client: WebClient = AsyncWebClient
//
//  client get url pipeTo self

  def receive = {
    case body: String =>
//      for (link <- findLinks(body))
//          context.parent ! Controller.Check(prepareLink(link), depth)
      stop()
    case _: Status.Failure => stop()
    case Abort             => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }


  def download(url: String) = {

    val p = Promise[Boolean]

//    val fut = future {
//      val in = Source.fromURL(url)
//      val fileName = url.substring(url.lastIndexOf("/"))
//      val out = Resource
//      Iterator.continually(in.getLines()).takeWhile(_ != -1).foreach(n => out.)
//    }
  }
}