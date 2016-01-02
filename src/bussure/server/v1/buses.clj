(ns bussure.server.v1.buses
  (:require [ring.util.response :as r]
            [bussure.core.location :as location]
            [bussure.resources.buses :as buses])
  )

(defn index
  "Lists all buses in the system"
  [params]
  (let [area (location/area-from-bounds params)
        data (if area (buses/by-area area) [])]
    (r/response data)
    )
  )
