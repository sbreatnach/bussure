(ns bussure.core.transport
  (:require [schema.core :as schema]
            [bussure.core.location :as location])
  (:import [bussure.core.location Position])
  )

(schema/defrecord Stop [id :- schema/Str
                        name :- schema/Str
                        position :- Position
                        public-id :- schema/Str])

(schema/defrecord Route [id :- schema/Str
                         name :- schema/Str])

(schema/defrecord Bus [id :- schema/Str
                       name :- schema/Str
                       position :- Position] {(schema/optional-key :route) Route})

(schema/defrecord Prediction [bus :- Bus
                              due-time :- schema/Num])
