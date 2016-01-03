(ns bussure.resources.buses
  (:require [clojure.tools.logging :as log]
            [bussure.provider.bus-eireann :as be]
            [bussure.cache :as cache]
            [bussure.resources.base :as base])
  )

(defn by-area
  "Returns the list of buses in the given area"
  [area]
  (log/debug "Retrieving buses for area" area)
  (let [cache-id (cache/resource-area-cache-id area "buses")]
    (base/retrieve-cached-resource cache-id #(be/buses-by-area area))
    )
  )
