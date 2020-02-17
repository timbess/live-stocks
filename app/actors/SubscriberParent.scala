package actors

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.stream.scaladsl.Flow
import com.google.inject.Provides
import livestocks.models.StockSubscriber
import play.api.libs.concurrent.ActorModule

import scala.concurrent.ExecutionContext

object SubscriberParent extends ActorModule {
  type Message = Create

  final case class Create(id: String, replyTo: ActorRef[Flow[StockSubscriber.Command, StockSubscriber.Response, NotUsed]])

  @Provides def apply()(implicit ec: ExecutionContext, scheduler: Scheduler): Behavior[Create] = {
    Behaviors.setup { context =>
      Behaviors.logMessages {
        Behaviors.receiveMessage {
          case Create(id, replyTo) =>
            val child = context.spawn(StockSubscriberActor(), s"userActor-$id")
            child ! StockSubscriberActor.Message.InitializeStream(replyTo)
            Behaviors.same
        }
      }
    }
  }
}
