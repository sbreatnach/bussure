(ns bussure.server.middleware
  (:require [camel-snake-kebab.core :as case]
            [camel-snake-kebab.extras :as extras]
            [clojure.tools.logging :as log])
  )

(defn wrap-body-response-case
  "Ensures the body being converted in responses has expected key case"
  [handler & [metadata]]
  (fn [request]
    (let [response (handler request)
          body (:body response)]
      (assoc response
        :body
        (if (coll? body)
          (extras/transform-keys case/->camelCaseString body)
          body
          )
        )
      )
    )
  )
