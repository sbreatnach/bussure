(ns bussure.provider.bus-eireann
  (:require [bussure.core.location :as location]
            [bussure.core.transport :as transport]
            [org.httpkit.client :as http]
            [camel-snake-kebab.core :as case]
            [clojure.tools.logging :as log]
            [cheshire.core :as json])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Location conversion

(defrecord BELocation [latitude longitude])

(defn be-coords->position
  "Returns the position from the given BusEireann coordinates"
  [latitude longitude]
  ; no idea where this coordinate system originates
  (location/->Position (/ latitude 3600000.0)
                       (/ longitude 3600000.0))
  )

(defn position->be-coords
  "Returns the BusEireann coords from the given position"
  [position]
  (->BELocation (int (* (:latitude position) 3600000.0))
                (int (* (:longitude position) 3600000.0)))
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Server Requests

(defn make-be-request
  "Makes a request to the given BusEireann resource with the supplied query
  params. Returns nil if failed, or the response body otherwise"
  [resource & [params]]
  (let [uri (str "http://www.buseireann.ie/inc/proto/" resource ".php")
        options (if params {:query-params params} {})
        {:keys [status body]} @(http/get uri options)]
    (log/trace "Requested" uri "; params:" params
               "; Response status:" status "; body:" body)
    (when (= status 200)
      (json/parse-string body case/->kebab-case-keyword)
      )
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Stops

(defn stop-point->stop
  "Converts the raw stop point data into a stop. Returns nil if given data is
  invalid"
  [stop-point]
  (when (map? stop-point)
    (transport/->Stop (:duid stop-point)
                      (:long-name stop-point)
                      (be-coords->position (:latitude stop-point) (:longitude stop-point))
                      (:code stop-point))
    )
  )

(defn stops-by-area
  "Retrieves the list of stops for a specific area"
  [area]
  (let [be-top-left (position->be-coords (:north-west-position area))
        be-bottom-right (position->be-coords (:south-east-position area))
        params {:longitude_west (:longitude be-top-left)
                :longitude_east (:longitude be-bottom-right)
                :latitude_north (:latitude be-top-left)
                :latitude_south (:latitude be-bottom-right)}
        data (make-be-request "stopPointTdi" params)]
    (log/trace "Raw stop data:" data)
    (filter seq (for [[_ value] (:stop-point-tdi data)]
                  (stop-point->stop value)))
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Buses

(defn buses-by-area
  "Retrieves the list of buses for a specific area"
  [area]
  )
