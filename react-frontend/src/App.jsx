import React, { useEffect } from 'react'
import logo from './logo.svg'
import './App.css'
import * as _ from 'lodash'
import moment, { max } from 'moment'
import { StockPriceChart } from './StockPriceChart'
import { StockTracker } from './StockTracker'

class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      chartData: {},
      lines: [],
      subscriptions: [],
    }
    this.ws = new WebSocket("ws://localhost:9000/ws")
  }

  setChart(newChartData) {
    this.setState({ ...this.state, chartData: newChartData })
  }

  setLines(newLines) {
    this.setState({ ...this.state, lines: newLines })
  }

  setSubscriptions(newSubscriptions) {
    this.setState({ ...this.state, subscriptions: newSubscriptions })
  }

  componentDidMount() {
    this.ws.onmessage = msg => {
      const chartData = this.state.chartData
      console.log(msg.data)
      const row = JSON.parse(msg.data)
      const symbol = row.StockPrice.symbol
      const price = row.StockPrice.price
      console.log(chartData)
      this.setChart({
        ...chartData,
        [symbol]: (chartData[symbol] || []).concat({ x: moment(), y: price })
      })
    }
    this.interval = setInterval(() => {
      const chartData = this.state.chartData
      const minTime = moment().subtract(30, 'seconds')
      const newChartData = _.reduce(
        chartData,
        (acc, prices, symbol) => {
          acc[symbol] = prices.filter(row => row.x.isAfter(minTime))
          return acc
        },
        {}
      )
      this.setLines(_.map(chartData, (prices, symbol) => ({ id: symbol, color: 'hsl(194, 70%, 50%)', data: prices.map(row => ({ ...row, x: row.x.toDate() })) })))
      this.setChart(newChartData)
    }, 100)
  }

  componentWillUnmount() {
    clearInterval(this.interval)
  }


  render() {
    const addStockSymbol = symbol =>
      this.ws.send(JSON.stringify({ AddSubscription: { symbol } }))

    const removeStockSymbol = symbol =>
      this.ws.send(JSON.stringify({ RemoveSubscription: { symbol } }))

    return (
      <div className="App">
        <StockTracker addStockSymbol={addStockSymbol} removeStockSymbol={removeStockSymbol} />
        <StockPriceChart data={this.state.lines} />
      </div>
    )
  }

}

export default App;
