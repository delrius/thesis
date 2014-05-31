package crawler

import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import akka.actor.ReceiveTimeout

class Main extends Actor {

  import Receptionist._

  val receptionist = context.actorOf(Props[Receptionist], "receptionist")
  
//  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/texts.html")
  receptionist ! Get("http://www.nbuv.gov.ua/ujrn/Soc_Gum/NaUKMA/Comp/2009_99/index.html")
//  receptionist ! Get("http://www.google.com/2")
//  receptionist ! Get("http://www.google.com/3")
//  receptionist ! Get("http://www.google.com/4")
  
  context.setReceiveTimeout(10.seconds)
  
  def receive = {
    case Result(url, set) =>
      println(set.toVector.sorted.mkString(s"Results for '$url':\n", "\n", "\n"))
    case Failed(url) =>
      println(s"Failed to fetch '$url'\n")
    case ReceiveTimeout =>
      context.stop(self)
  }
  
  override def postStop(): Unit = {
    AsyncWebClient.shutdown()
  }
  
}