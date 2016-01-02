(ns bussure.cache
  (:require [clojure.core.cache :as backend]
            [clojure.tools.logging :as log])
  )

(def ttl-30-secs (* 1000 30))
(def ttl-1-day (* 1000 60 60 24))

; TODO: replace basic in-memory cache with proper cache
(def MemCache (atom (-> {}
                        (backend/fifo-cache-factory :threshold 2048)
                        (backend/ttl-cache-factory :ttl ttl-30-secs))))

(defn resource-area-cache-id
  "Generates the cache ID unique for the given resource and area"
  [area resource]
  (str "resource:" resource
       ":" (-> area :north-west-position :latitude)
       ":" (-> area :north-west-position :longitude)
       ":" (-> area :south-east-position :latitude)
       ":" (-> area :south-east-position :longitude))
  )

(defn stop-cache-id
  "Generates the unique cache ID for the given stop ID"
  [stop-id]
  (str "stop:" stop-id)
  )

(defn vehicle-cache-id
  "Generates the cache ID for the given vehicle ID"
  [provider vehicle-id]
  (str "vehicle:" provider ":" vehicle-id)
  )

(defn route-cache-id
  "Generates the cache ID used for the given route ID"
  [provider route-id]
  (str "route:" provider ":" route-id)
  )

(defn in-cache?
  [cache-id]
  (log/trace "Checking in cache" cache-id)
  (backend/has? @MemCache cache-id)
  )

(defn save-to-cache
  "Stores the given data in the cache using the cache ID"
  [cache-id data & [{:keys [ttl]}]]
  (log/trace "Saving to cache" cache-id data)
  (swap! MemCache #(backend/miss % cache-id data))
  )

(defn data-by-id
  "Returns the data from the cache using the cache ID"
  [cache-id]
  (log/trace "Retrieving from cache" cache-id)
  (get (swap! MemCache #(backend/hit % cache-id)) cache-id)
  )
