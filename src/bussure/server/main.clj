(ns bussure.server.main
  (:require [org.httpkit.server :as server]
            [io.aviso.rook :as rook]
            [ring.middleware.cors :as cors]
            [bussure.server.middleware :as local-middleware])
  )

(defn create-handler
  "Returns the Ring handler for the HTTP server"
  []
  (-> (rook/namespace-handler {:context ["v1"]
                               :default-middleware local-middleware/wrap-body-response-case}
                              ["buses" 'bussure.server.v1.buses]
                              ["stops" 'bussure.server.v1.stops])
      rook/wrap-with-standard-middleware
      (cors/wrap-cors :access-control-allow-origin [#".*"]
                      :access-control-allow-methods [:get])
      )
  )

(defn http-run
  "Starts running the HTTP server"
  [& args]
  (let [options {:port (Integer/parseInt (or (System/getenv "PORT") "8080"))}]
    (server/run-server (create-handler) options)
    )
  )
