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

## Future Integration Investigations

### Dublin Bus

There doesn't appear to be a method of retrieving real-time bus data, only stops. To get information about a specific stop

curl 'https://www.dublinbus.ie/RTPI/Sources-of-Real-Time-Information/?searchtype=view&searchquery=315'

To get locations of stops by area:

curl "https://www.dublinbus.ie/Templates/Public/RoutePlannerService/RTPIMapHandler.ashx?ne=53.416089,-6.150421&sw=53.271997,-6.384567&zoom=12&czoom=16&_=1521702845178"

## License

Copyright © 2016 Glicsoft

Distributed under the BSD License.
