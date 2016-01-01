(ns bussure.cache)

; TODO: replace basic in-memory cache with proper cache
(def temp-cache (atom {}))

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

(defn save-to-cache
  "Stores the given data in the cache using the cache ID"
  [cache-id data]
  (swap! temp-cache assoc cache-id data)
  )

(defn data-by-id
  "Returns the data from the cache using the cache ID"
  [cache-id]
  (get @temp-cache cache-id nil)
  )
