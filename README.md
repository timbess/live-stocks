# Live stocks
Provides live stock prices from Yahoo Finance API

## Local Setup

### Backend
1. Run `sbt run` to start API

### React Frontend
1. Install Yarn
2. Go to `react-frontend` directory
2. Run `yarn install && yarn start` to start UI

### Slinky Frontend
1. Install Yarn
2. Run `sbt frontendDev` to start UI


## Architecture Diagram

                                                        Live Stocks

                                    +------------------------------------------------+
                                    |                                                |
                                    |                                                |
                    1. Incoming     |     +------------+                             |
    +------------+     Websocket    |     |            |                             |
    |            |                  |     | Subscriber |                             |
    |            +------------------------> Parent     |                             |
    |   Client   |                  |     |            |                             |
    |            <----------+       |     +-----+------+                             |
    |            |          |       |           |                                    |
    +------------+          |       |           +------------+ 2. Create subscriber  |
                            |       |                        |                       |          +----------------+
                            |       |                 +------v-----+    Polls every  |          |                |
                            |       |                 |            |    Second       |          |                |
                            +-------------------------+ Subscriber +---------------------------->  Yahoo API     |
                                    |                 |            <----------------------------+                |
                                    | 3. Return WS    |            |                 |          |                |
                                    | flow to handle  +------------+                 |          +----------------+
                                    | adding new stocks                              |
                                    | and streaming in                               |
                                    | price changes                                  |
                                    |                                                |
                                    |                                                |
                                    |                                                |
                                    +------------------------------------------------+

