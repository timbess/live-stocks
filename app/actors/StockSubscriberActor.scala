package actors

import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import livestocks.models.StockSubscriber._
import yahoofinance.YahooFinance

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

object StockSubscriberActor {
  sealed trait Message
  object Message {
    case class InitializeStream(replyTo: ActorRef[Flow[Command, Response, NotUsed]]) extends Message
    case class ClientCommand(command: Command) extends Message
  }

  def apply(): Behavior[Message] = Behaviors.setup(implicit context =>
    new StockSubscriberActor().behavior
  )
}

class StockSubscriberActor(implicit context: ActorContext[StockSubscriberActor.Message]) {
  import StockSubscriberActor._

  implicit val system: ActorSystem[_] = context.system

  private val subscriptions: mutable.Set[String] = mutable.HashSet.empty
  private val subscriptionStream: Source[Response, Cancellable] = Source.tick(0.seconds, 500.millis, ())
    .flatMapConcat { _ =>
      if (subscriptions.nonEmpty) {
        val results = YahooFinance.get(subscriptions.toArray)
        Source(results.asScala.view.mapValues(_.getQuote.getPrice).filter(_._2 != null).map { case (k, v) => Response.StockPrice(k, v) }.toList)
      } else Source.empty
    }

  private val inputCommands: Sink[Command, Future[Done]] = Sink.foreach[Command] {
    case Command.AddSubscription(symbol) =>
      addSubscription(symbol)
    case Command.RemoveSubscription(symbol) =>
      removeSubscription(symbol)
  }

  private lazy val websocketFlow: Flow[Command, Response, NotUsed] =
    Flow.fromSinkAndSourceCoupled(inputCommands, subscriptionStream)

  def behavior: Behavior[StockSubscriberActor.Message] = Behaviors.receiveMessage {
    case Message.InitializeStream(replyTo) =>
      replyTo ! websocketFlow
      Behaviors.same
    case Message.ClientCommand(Command.AddSubscription(symbol)) =>
      addSubscription(symbol)
      Behaviors.same
    case Message.ClientCommand(Command.RemoveSubscription(symbol)) =>
      removeSubscription(symbol)
      Behaviors.same
  }

  private def addSubscription(symbol: String) = {
    context.log.info(s"Adding symbol: $symbol")
    subscriptions += symbol
  }

  private def removeSubscription(symbol: String) = {
    context.log.info(s"Removing symbol: $symbol")
    subscriptions -= symbol
  }
}
