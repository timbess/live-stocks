package hello.world

import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class Stock extends StatelessComponent {
  case class Props(symbol: String, currentPrice: BigDecimal)
  override def render(): ReactElement = p()(s"${props.symbol}: ${props.currentPrice}")
}
