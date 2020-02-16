package controllers

import actors.{StockSubscriberActor, SubscriberParent}
import actors.StockSubscriberActor._
import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Scheduler}
import akka.stream.Materializer
import akka.stream.scaladsl._
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import javax.inject._
import play.api.Logging
import play.api.libs.json._
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


/**
 * This class creates the actions and the websocket needed.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, subscriberParent: ActorRef[SubscriberParent.Message])(implicit ec: ExecutionContext, scheduler: Scheduler)
  extends AbstractController(cc) with Logging {
  implicit val messageFlowTransformer: MessageFlowTransformer[Command, Response] = MessageFlowTransformer.jsonMessageFlowTransformer[Command, Response]

  def ws: WebSocket = WebSocket.acceptOrResult[Command, Response] { request =>
    implicit val timeout = Timeout(5.seconds)
    subscriberParent.ask(replyTo => SubscriberParent.Create(request.id.toString, replyTo))
      .map(Right(_))
  }
}

