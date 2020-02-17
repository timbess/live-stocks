package hello.world

import org.scalajs.dom.Event
import org.scalajs.dom.html
import org.scalajs.dom.raw.WebSocket
import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import livestocks.models.StockSubscriber
import livestocks.models.StockSubscriber.Response
import play.api.libs.json.Json

import scala.collection.immutable.{SortedMap, SortedSet}

@react class StockTracker extends Component {

  case class Props(wsUrl: String)

  case class State(symbols: SortedMap[String, BigDecimal], inputText: String, disabled: Boolean)

  val ws = new WebSocket(props.wsUrl)

  override def componentDidMount(): Unit = {
    ws.onopen = _ => setState(state.copy(disabled = false))
    ws.onmessage = e => {
      Json.parse(e.data.asInstanceOf[String]).as[StockSubscriber.Response] match {
        case Response.StockPrice(symbol, price) =>
          setState(
            state.copy(symbols =
              if (state.symbols.contains(symbol)) {
                state.symbols.updated(symbol, price)
              } else {
                state.symbols
              })
          )
      }
    }
  }

  override def initialState: State = State(SortedMap.empty, "", disabled = true)

  def handleChange(e: SyntheticEvent[html.Input, Event]): Unit = {
    e.preventDefault()
    setState(state.copy(inputText = e.target.value))
  }

  def handleSubmit(e: SyntheticEvent[html.Form, Event]): Unit = {
    e.preventDefault()
    ws.send(Json.toJson(StockSubscriber.Command.AddSubscription(state.inputText): StockSubscriber.Command).toString())
    setState(state.copy(symbols = state.symbols.updated(state.inputText, 0), inputText = ""))
  }

  def removeSymbol(symbol: String): Unit = {
    ws.send(Json.toJson(StockSubscriber.Command.RemoveSubscription(symbol): StockSubscriber.Command).toString())
    setState(state.copy(symbols = state.symbols.removed(symbol)))
  }

  override def render(): ReactElement = {
    div(
      state.symbols.zipWithIndex.map { case ((symbol, price), idx) => Stock(symbol = symbol, currentPrice = price, handleRemove = removeSymbol).withKey(idx.toString) },
      form(onSubmit := (handleSubmit(_)))(
        input(onChange := (handleChange(_)),
          disabled := state.disabled,
          value := state.inputText),
        button("Add Stock")
      )
    )
  }
}
