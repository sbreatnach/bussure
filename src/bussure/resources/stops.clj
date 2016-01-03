(ns bussure.resources.stops
  (:require [clojure.tools.logging :as log]
            [bussure.provider.bus-eireann :as be]
            [bussure.cache :as cache]
            [bussure.resources.base :as base])
  )

(defn by-area
  "Returns the list of stops by area supplied"
  [area]
  (log/debug "Retrieving stops for area" area)
  (let [cache-id (cache/resource-area-cache-id area "stops")]
    (base/retrieve-cached-resource cache-id
                                   #(be/stops-by-area area)
                                   {:ttl cache/ttl-1-day})
    )
  )

(defn by-id
  "Returns the stop data and predictions for the given stop ID. Stop data must
  be in cache otherwise nothing is returned."
  [stop-id]
  (log/debug "Retrieving stop predictions for ID" stop-id)
  (let [cache-id (cache/stop-cache-id stop-id)]
    (base/retrieve-cached-resource cache-id #(be/stop-predictions-by-id stop-id))
    )
  )
