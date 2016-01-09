(ns bussure.core.environment)

(def production?
  "Is the current environment production?"
  (= (System/getenv "ENVIRONMENT") "production")
  )
