package hello.world

import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.{Event, html}

@react class Stock extends StatelessComponent {
  case class Props(symbol: String, currentPrice: BigDecimal, handleRemove: String => Unit)
  def clickRemove(e: SyntheticEvent[html.Button, Event]): Unit = {
    e.preventDefault()
    props.handleRemove(props.symbol)
  }
  override def render(): ReactElement =
    div(s"${props.symbol}: ${props.currentPrice}",
      button(onClick := (clickRemove(_)))("-"))
}
