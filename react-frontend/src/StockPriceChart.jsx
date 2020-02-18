/*
 * This file is part of the nivo project.
 *
 * Copyright 2016-present, Raphaël Benitte.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { Line, ResponsiveLine } from '@nivo/line'
import { timeFormat } from 'd3-time-format'
import * as _ from 'lodash'
import React, { Component } from 'react'
import moment from 'moment'

const commonProperties = {
  width: 900,
  height: 400,
  margin: { top: 20, right: 20, bottom: 60, left: 80 },
}


export class StockPriceChart extends Component {
  constructor(props) {
    super(props)
    this.formatTime = timeFormat('%Y %b %d %H:%M:%S')
  }

  render() {
    return (
      <Line
        {...commonProperties}
        data={this.props.data}
        xScale={{ type: 'time', format: 'native', min: moment().subtract(10, 'seconds').toDate(), max: moment().add('1', 'seconds').toDate() }}
        yScale={{ type: 'linear', min: 0, max: 'auto' }}
        axisTop={null}
        axisRight={null}
        axisBottom={{
          format: '%H:%M:%S',
          tickValues: 4,
          legends: 'Date'
        }}
        enableGridX={false}
        curve="monotoneX"
        animate={false}
        motionStiffness={300}
        motionDamping={40}
        isInteractive={true}
        enableSlices={'x'}
        useMesh={false}
        theme={{
          axis: { ticks: { text: { fontSize: 14 } } },
          grid: { line: { stroke: '#ddd', strokeDasharray: '1 2' } },
        }}
        legends={[
          {
            anchor: 'bottom-right',
            direction: 'column',
            justify: false,
            translateX: 100,
            translateY: 0,
            itemsSpacing: 0,
            itemDirection: 'left-to-right',
            itemWidth: 80,
            itemHeight: 20,
            itemOpacity: 0.75,
            symbolSize: 12,
            symbolShape: 'circle',
            symbolBorderColor: 'rgba(0, 0, 0, .5)',
            effects: [
              {
                on: 'hover',
                style: {
                  itemBackground: 'rgba(0, 0, 0, .03)',
                  itemOpacity: 1
                }
              }
            ]
          }
        ]}
      />
    )
  }
}