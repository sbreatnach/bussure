(ns bussure.provider.bus-eireann
  (:require [bussure.core.location :as location]
            [bussure.core.transport :as transport]
            [bussure.cache :as cache]
            [org.httpkit.client :as http]
            [camel-snake-kebab.core :as case]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [clojure.tools.logging :as log]
            [clojure.string :as cljstr]
            [cheshire.core :as json])
  )

(def cache-key "buseireann")

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

(defn area->params
  "Returns the request query parameters as extracted from the given area"
  [area]
  (let [be-top-left (position->be-coords (:north-west-position area))
        be-bottom-right (position->be-coords (:south-east-position area))]
    {:longitude_west (:longitude be-top-left)
     :longitude_east (:longitude be-bottom-right)
     :latitude_north (:latitude be-top-left)
     :latitude_south (:latitude be-bottom-right)}
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Server Requests

(defn route-object-converter
  "Converts the Javascript object represented in the given route response body
  into standard Clojure data structure"
  [data]
  (let [[_ matched] (re-matches #"^.*?(\x7b.+\x7d).*$" data)
        clean-data (cljstr/replace matched #"(\"direction_extensions\": \{.+?\},)" "")]
    (json/parse-string clean-data case/->kebab-case-keyword)
    )
  )

(defn make-be-request
  "Makes a request to the given BusEireann resource with the supplied query
  params. Returns nil if failed, or the response body otherwise"
  [resource & [params converter]]
  (let [uri (str "http://www.buseireann.ie/inc/proto/" resource ".php")
        converter (or converter
                      #(json/parse-string % case/->kebab-case-keyword)
                      )
        options (if params {:query-params params} {})
        {:keys [status body]} @(http/get uri options)]
    (log/trace "Requested" uri "; params:" params
               "; Response status:" status "; body:" body)
    (when (= status 200)
      (converter body)
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
  (let [params (area->params area)
        data (make-be-request "stopPointTdi" params)]
    (filter seq (for [[_ value] (:stop-point-tdi data)]
                  (stop-point->stop value)))
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Predictions

(defn refresh-routes
  "Refreshes all the routes available in the cache for Bus Eireann's system,
  keyed by unique ID"
  []
  (let [data (make-be-request "routes" {} route-object-converter)]
    (doseq [[_ value] (:route-tdi data)
            :when (map? value)
            :let [uid (:duid value)]]
      (cache/save-to-cache (cache/route-cache-id cache-key uid)
                           (transport/->Route uid (:short-name value)))
      )
    )
  )

(defn stop-passage->prediction
  "Converts the raw stop passage data into standardised stop prediction. Uses
  the given "
  [stop-passage now]
  (when (map? stop-passage)
    (let [due-data (or (:departure-data stop-passage)
                             (:arrival-data stop-passage))
          vehicle-id (-> stop-passage :vehicle-duid :duid)
          route-id (-> stop-passage :route-duid :duid)
          due-time (or (:actual-passage-time-utc due-data)
                       (:scheduled-passage-time-utc due-data))]
      (when (and due-data vehicle-id route-id due-time (> due-time now))
        (transport/->Prediction
          ; bus constructed from either cached data or placeholder data as
          ; read from passage, plus the route as read from cache
          (assoc
            (or (cache/data-by-id (cache/vehicle-cache-id cache-key vehicle-id))
                (transport/->Bus ""
                                 (-> due-data :multilingual-direction-text :default-value)
                                 (location/->Position 0.0 0.0)))
            :route (cache/data-by-id (cache/route-cache-id cache-key route-id)))
          due-time
          )
        )
      )
    )
  )

(defn stop-predictions-by-id
  "Retrieves the predictions for a stop specified by the given ID"
  [stop-id]
  (let [params {:_ (time-coerce/to-long (time/now))
                :stop_point stop-id}
        _ (refresh-routes)
        data (make-be-request "stopPassageTdi" params)
        now (time-coerce/to-epoch (time/now))]
    (sort-by :due-time
             (filter seq (for [[_ value] (:stop-passage-tdi data)]
                           (stop-passage->prediction value now)))
             )
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Buses

(defn vehicle->bus
  "Converts the given raw vehicle data into standardised bus"
  [vehicle]
  (when (and (map? vehicle) (:trip-duid vehicle))
    (transport/->Bus (:duid vehicle)
                     "unknown"
                     (location/->Position (:latitude vehicle)
                                          (:longitude vehicle)))
    )
  )

(defn buses-by-area
  "Retrieves the list of buses for a specific area"
  [area]
  (let [params (area->params area)
        data (make-be-request "vehicleTdi" params)]
    (filter seq (for [[_ value] (:vehicle-tdi data)]
                  (vehicle->bus value)))
    )
  )
