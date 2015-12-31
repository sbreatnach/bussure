(ns bussure.run
  (:require [bussure.server.main :as main])
  (:gen-class)
  )

(defn -main
  "Starting point for the Bussure server"
  [& args]
  (apply main/http-run args)
  )
