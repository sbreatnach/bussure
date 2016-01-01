(ns bussure.buses
  (:require [clojure.tools.logging :as log]
            [bussure.provider.bus-eireann :as be])
  )

(defn by-area
  "Returns the list of buses in the given area"
  [area]
  (log/trace "Retrieving buses for area" area)
  ; TODO: add caching for responses
  (be/buses-by-area area)
  )
