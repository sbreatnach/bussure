(defproject bussure "0.2.0"
  :description "Backend for the Bussed application. Serves transport
                information from various sources in unified format"
  :url "http://example.com/FIXME"
  :license {:name "BSD License"
            :url "http://www.linfo.org/bsdlicense.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]

                 ; General
                 [prismatic/schema "1.0.4"]
                 [cheshire "5.5.0"]
                 [camel-snake-kebab "0.3.2"]
                 [clj-time "0.9.0"]

                 ; Caching
                 [org.clojure/core.cache "0.6.4"]

                 ; HTTP handling
                 [io.aviso/rook "0.1.39"
                  :exclusions [org.clojure/tools.logging]]
                 [ring "1.4.0"]
                 [ring-cors "0.1.7"]
                 [http-kit "2.1.18"]

                 ;; logging
                 [org.clojure/tools.logging "0.2.4"
                  :exclusions [log4j/log4j
                               commons-logging/commons-logging
                               org.slf4j/slf4j-api
                               org.slf4j/slf4j-log4j12]]
                 [ch.qos.logback/logback-classic "1.0.11"]
                 [org.slf4j/log4j-over-slf4j "1.7.2"]

                 ; addons
                 [com.newrelic.agent.java/newrelic-agent "3.24.1"]
                 [yleisradio/new-reliquary "1.0.0"
                  :exclusions [com.newrelic.agent.java/newrelic-agent]]
                 ]
  :target-path "target/%s"
  :plugins [[lein-ring "0.12.3"]]
  :ring {:handler bussure.server.main/handler}
  :profiles {:uberjar {:aot :all}})
