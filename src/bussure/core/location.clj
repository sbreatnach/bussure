(ns bussure.core.location
  (:require [schema.core :as schema])
  )

(schema/defrecord Position
  [latitude :- schema/Num
   longitude :- schema/Num]
  )

(defprotocol AreaChecks
  (contains-position? [position])
  )

(schema/defrecord Area
  [north-west-position :- Position
   south-east-position :- Position]
  AreaChecks
  (contains-position?
    [position]
    (boolean (and (>= (:latitude north-west-position)
                      (:latitude position)
                      (:latitude south-east-position))
                  (<= (:longitude north-west-position)
                      (:longitude position)
                      (:longitude south-east-position))))
    )
  )

(defn area-from-bounds
  "Creates an area from the bounding box area specified"
  [bounds]
  (let [top (Float/parseFloat (:top bounds))
        left (Float/parseFloat (:left bounds))
        bottom (Float/parseFloat (:bottom bounds))
        right (Float/parseFloat (:right bounds))]
    (when (and top left bottom right)
      (->Area (->Position top left) (->Position bottom right))
      )
    )
  )
