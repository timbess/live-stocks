/*
 * This file is part of the nivo project.
 *
 * Copyright 2016-present, Raphaël Benitte.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import React, {Component, useEffect, useState} from 'react'
import range from 'lodash/range'
import last from 'lodash/last'
import {generateDrinkStats} from '@nivo/generators'
import {Defs} from '@nivo/core'
import {Line} from '@nivo/line'
import {area, curveMonotoneX} from 'd3-shape'
import * as time from 'd3-time'
import {timeFormat} from 'd3-time-format'

const data = generateDrinkStats(18)
const commonProperties = {
    width: 900,
    height: 400,
    margin: {top: 20, right: 20, bottom: 60, left: 80},
    data,
    animate: true,
    enableSlices: 'x',
}


export class RealTimeChart extends Component {
    constructor(props) {
        super(props)

        const date = new Date()
        date.setMinutes(0)
        date.setSeconds(0)
        date.setMilliseconds(0)

        this.state = {
            dataA: range(100).map(i => ({
                x: time.timeMinute.offset(date, i * 30),
                y: 10 + Math.round(Math.random() * 20),
            })),
            dataB: range(100).map(i => ({
                x: time.timeMinute.offset(date, i * 30),
                y: 30 + Math.round(Math.random() * 20),
            })),
            dataC: range(100).map(i => ({
                x: time.timeMinute.offset(date, i * 30),
                y: 60 + Math.round(Math.random() * 20),
            })),
        }

        this.formatTime = timeFormat('%Y %b %d')
    }

    componentDidMount() {
        this.timer = setInterval(this.next, 100)
    }

    componentWillUnmount() {
        clearInterval(this.timer)
    }

    next = () => {
        const dataA = this.state.dataA.slice(1)
        dataA.push({
            x: time.timeMinute.offset(last(dataA).x, 30),
            y: 10 + Math.round(Math.random() * 20),
        })
        const dataB = this.state.dataB.slice(1)
        dataB.push({
            x: time.timeMinute.offset(last(dataB).x, 30),
            y: 30 + Math.round(Math.random() * 20),
        })
        const dataC = this.state.dataC.slice(1)
        dataC.push({
            x: time.timeMinute.offset(last(dataC).x, 30),
            y: 60 + Math.round(Math.random() * 20),
        })

        this.setState({dataA, dataB, dataC})
    }

    render() {
        const {dataA, dataB, dataC} = this.state

        return (
            <Line
                {...commonProperties}
                margin={{top: 30, right: 50, bottom: 60, left: 50}}
                data={[
                    {id: 'A', data: dataA},
                    {id: 'B', data: dataB},
                    {id: 'C', data: dataC},
                ]}
                xScale={{type: 'time', format: 'native'}}
                yScale={{type: 'linear', max: 100}}
                axisTop={{
                    format: '%H:%M',
                    tickValues: 'every 4 hours',
                }}
                axisBottom={{
                    format: '%H:%M',
                    tickValues: 'every 4 hours',
                    legend: `${this.formatTime(dataA[0].x)} ——— ${this.formatTime(last(dataA).x)}`,
                    legendPosition: 'middle',
                    legendOffset: 46,
                }}
                axisRight={{}}
                enablePoints={false}
                enableGridX={true}
                curve="monotoneX"
                animate={false}
                motionStiffness={120}
                motionDamping={50}
                isInteractive={false}
                enableSlices={false}
                useMesh={true}
                theme={{
                    axis: {ticks: {text: {fontSize: 14}}},
                    grid: {line: {stroke: '#ddd', strokeDasharray: '1 2'}},
                }}
            />
        )
    }
}

const GrowingLine = () => {
    const [points, setPoints] = useState([{x: 0, y: 50}])

    useEffect(() => {
        if (points.length === 101) return

        setTimeout(() => {
            setPoints(p => {
                const prev = p[p.length - 1]

                return [
                    ...p,
                    {
                        x: prev.x + 1,
                        y: Math.max(Math.min(prev.y + Math.random() * 10 - 5, 100), 0),
                    },
                ]
            })
        }, 300)
    }, [points, setPoints])

    return (
        <Line
            {...commonProperties}
            yScale={{
                type: 'linear',
                min: 0,
                max: 'auto',
            }}
            xScale={{
                type: 'linear',
                min: 0,
                max: 'auto',
            }}
            data={[{id: 'serie', data: points}]}
            axisBottom={{
                tickValues: 4,
            }}
            axisLeft={{
                tickValues: 4,
            }}
            isInteractive={false}
            animate={false}
            enableArea={true}
        />
    )
}

const AreaLayer = ({series, xScale, yScale, innerHeight}) => {
    const areaGenerator = area()
        .x(d => xScale(d.data.x))
        .y0(d => Math.min(innerHeight, yScale(d.data.y - 40)))
        .y1(d => yScale(d.data.y + 10))
        .curve(curveMonotoneX)

    return (
        <>
            <Defs
                defs={[
                    {
                        id: 'pattern',
                        type: 'patternLines',
                        background: 'transparent',
                        color: '#3daff7',
                        lineWidth: 1,
                        spacing: 6,
                        rotation: -45,
                    },
                ]}
            />
            <path
                d={areaGenerator(series[0].data)}
                fill="url(#pattern)"
                fillOpacity={0.6}
                stroke="#3daff7"
                strokeWidth={2}
            />
        </>
    )
}

const styleById = {
    cognac: {
        strokeDasharray: '12, 6',
        strokeWidth: 2,
    },
    vodka: {
        strokeDasharray: '1, 16',
        strokeWidth: 8,
        strokeLinejoin: 'round',
        strokeLinecap: 'round',
    },
    rhum: {
        strokeDasharray: '6, 6',
        strokeWidth: 4,
    },
    default: {
        strokeWidth: 1,
    },
}

const DashedLine = ({series, lineGenerator, xScale, yScale}) => {
    return series.map(({id, data, color}) => (
        <path
            key={id}
            d={lineGenerator(
                data.map(d => ({
                    x: xScale(d.data.x),
                    y: yScale(d.data.y),
                }))
            )}
            fill="none"
            stroke={color}
            style={styleById[id] || styleById.default}
        />
    ))
}
