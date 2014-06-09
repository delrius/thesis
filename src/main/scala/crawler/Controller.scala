package crawler

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.ReceiveTimeout
import scala.concurrent.duration._
import akka.actor.ActorRef
import utils.ConfigReader

object Controller {

  case class Check(url: String, depth: Int)

  case class Result(links: Set[String])

}

class Controller extends Actor with ActorLogging {

  import Controller._

  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  context.setReceiveTimeout(10.seconds)

  def getterProps(url: String, depth: Int): Props = Props(new Getter(url, depth))

  def receive = {
    case Check(url, depth) =>
      log.debug("{} checking {}", depth, url)
      if (!cache(url) && depth > 0) {
        url match {
          case DOWNLOAD_CONDITION(c) =>
          case _ => children += context.actorOf(getterProps(url, depth - 1))
        }
      }
      cache += url
    case Getter.Done =>
      children -= sender
      if (children.isEmpty)
        context.parent ! Result(cache)
    case ReceiveTimeout =>
      context.children foreach (_ ! Getter.Abort)
  }

  val DOWNLOAD_CONDITION = ConfigReader.downloadCondition.r

}