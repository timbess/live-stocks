package livestocks.models

import julienrf.json.derived
import play.api.libs.json.Format

object StockSubscriber {

  sealed trait Command
  object Command {
    implicit val commandsFormatter: Format[Command] = derived.oformat()
    case class AddSubscription(symbol: String) extends Command
  }

  sealed trait Response
  object Response {
    implicit val queriesFormatter: Format[Response] = derived.oformat()
    case class StockPrice(symbol: String, price: BigDecimal) extends Response
  }

}
