import React from 'react'

export const StockTracker = (props) => {
  const [currentState, setState] = React.useState({ inputText: "" })
  const { inputText } = currentState

  const handleChange = (e) => {
    e.preventDefault()
    setState({ ...currentState, inputText: e.target.value })
  }

  const handleAdd = (e) => {
    e.preventDefault()
    props.addStockSymbol(inputText)
    setState({ ...currentState, inputText: "" })
  }

  const handleRemove = e => {
    e.preventDefault()
    props.removeStockSymbol(inputText)
    setState({ ...currentState, inputText: "" })
  }

  return (<form>
        <input onChange={handleChange} value={inputText} />
        <button onClick={handleAdd}>
          Add
        </button>
        <button onClick={handleRemove}>
          Remove
        </button>
      </form>)
}