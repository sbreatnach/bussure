(ns bussure.server.v1.stops
  (:require [ring.util.response :as r]
            [bussure.core.location :as location]
            [bussure.resources.stops :as stops])
  )

(defn index
  "Lists all stops in the system"
  [params]
  (let [area (location/area-from-bounds params)
        data (if area (stops/by-area area) [])]
    (r/response data)
    )
  )

(defn show
  "Return stop prediction data for the uniquely identified stop"
  [id]
  (r/response (stops/by-id id))
  )
