(ns bussure.stops
  (:require [clojure.tools.logging :as log]
            [bussure.provider.bus-eireann :as be])
  )

(defn by-area
  "Returns the list of stops by area supplied"
  [area]
  (log/trace "Retrieving stops for area" area)
  ; TODO: build up caching
  (be/stops-by-area area)
  )

(defn by-id
  "Returns the stop data and predictions for the given stop ID. Stop data must
  be in cache otherwise nothing is returned."
  [stop-id]
  {}
  )
