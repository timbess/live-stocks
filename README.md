# Stock checker
Provides live stock prices from Yahoo Finance API

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

