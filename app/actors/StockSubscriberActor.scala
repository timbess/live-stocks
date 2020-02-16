package actors

import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import julienrf.json.derived
import play.api.libs.json.Format
import yahoofinance.YahooFinance

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

object StockSubscriberActor {
  sealed trait Command
  object Command {
    implicit val commandsFormatter: Format[Command] = derived.oformat()
    case class AddSubscription(symbol: String) extends Command
  }

  sealed trait Message
  object Message {
    case class InitializeStream(replyTo: ActorRef[Flow[Command, Response, NotUsed]]) extends Message
    case class ClientCommand(command: Command) extends Message
  }

  sealed trait Response
  object Response {
    implicit val queriesFormatter: Format[Response] = derived.oformat()
    case class StockPrice(symbol: String, price: BigDecimal) extends Response
  }


  def apply(): Behavior[Message] = Behaviors.setup(implicit context =>
    new StockSubscriberActor().behavior
  )
}

class StockSubscriberActor(implicit context: ActorContext[StockSubscriberActor.Message]) {
  import StockSubscriberActor._

  implicit val system: ActorSystem[_] = context.system

//  private val (hubSink, hubSource) = MergeHub.source[Response](perProducerBufferSize = 16)
//    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
//    .run()


  private val subscriptions: mutable.Set[String] = mutable.HashSet.empty
  private val subscriptionStream: Source[Response, Cancellable] = Source.tick(0.seconds, 1.second, ())
    .flatMapConcat { _ =>
      if (subscriptions.nonEmpty) {
        val results = YahooFinance.get(subscriptions.toArray)
        Source(results.asScala.view.mapValues(_.getQuote.getPrice).map { case (k, v) => Response.StockPrice(k, v) }.toList)
      } else Source.empty
    }

  private val inputCommands: Sink[Command, Future[Done]] = Sink.foreach[Command] {
    case Command.AddSubscription(symbol) =>
      addSubscription(symbol)
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
  }

  private def addSubscription(symbol: String) = {
    subscriptions += symbol
  }
}