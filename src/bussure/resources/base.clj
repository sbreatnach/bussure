(ns bussure.resources.base
  (:require [bussure.cache :as cache])
  )

(defn retrieve-cached-resource
  "Retrieves the resource by ID from the cache or direct, if cached data does
  not exist."
  [cache-id resource-fn & [options]]
  (let [cached? (cache/in-cache? cache-id)
        output (if cached? (cache/data-by-id cache-id) (resource-fn))]
    (when (not cached?)
      (cache/save-to-cache cache-id output options)
      )
    output
    )
  )
