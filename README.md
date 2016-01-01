# bussure

Server backend for the Bussed application. It delivers a simple unified RESTful
API for viewing bus stops, buses and stop predictions.

## Installation

    lein deps

## Usage

Development run:

    lein run
    
Server deploy run:

    lein uberjar
    java -jar target/bussure-0.1.0-standalone.jar

## License

Copyright © 2016 Glicsoft

Distributed under the BSD License.
