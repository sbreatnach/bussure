# bussure

Server backend for the Bussed application. It delivers a simple unified RESTful
API for viewing bus stops, buses and stop predictions.

## Installation

    lein deps

## Usage

Development run:

    lein trampoline ring server
    
Server deploy to Bluemix cloud:

    lein ring uberwar
    cf buildpacks
    emacs manifest.yml <-- update to latest Liberty buildpack
    cf push

## License

Copyright © 2016 Glicsoft

Distributed under the BSD License.
