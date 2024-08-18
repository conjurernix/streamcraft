(ns streamcraft.http-server.core
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [streamcraft.logging.api :as log]
            [streamcraft.protocols.api.provider.http-handler :as handler])
  (:import (java.time Duration)
           (org.eclipse.jetty.server Server)
           (org.eclipse.jetty.server.handler.gzip GzipHandler)
           (org.eclipse.jetty.websocket.server.config JettyWebSocketServletContainerInitializer JettyWebSocketServletContainerInitializer$Configurator)))

(defn- add-gzip-handler!
  "Makes Jetty server compress responses. Optional but recommended."
  [server]
  (.setHandler server
               (doto (GzipHandler.)
                 (.setIncludedMimeTypes
                   (-> ["text/css" "text/plain" "text/javascript"
                        "application/javascript" "application/json"
                        "image/svg+xml"]
                       (into-array)))                       ; only compress these
                 (.setMinGzipSize 1024)
                 (.setHandler (.getHandler server)))))

(defn- configure-websocket!
  "Tune Jetty Websocket config for Electric compat." [server]
  (JettyWebSocketServletContainerInitializer/configure
    (.getHandler server)
    (reify JettyWebSocketServletContainerInitializer$Configurator
      (accept [_this _servletContext wsContainer]
        (.setIdleTimeout wsContainer (Duration/ofSeconds 60))
        (.setMaxBinaryMessageSize wsContainer (* 100 1024 1024)) ; 100M - temporary
        (.setMaxTextMessageSize wsContainer (* 100 1024 1024)) ; 100M - temporary
        ))))

(defrecord JettyHttpServer
  [config handler-provider server]
  component/Lifecycle
  (start [this]
    (log/info! :starting-component {:component this})
    (-> this
        (assoc :server
               (-> (handler/get-handler handler-provider)
                   (jetty/run-jetty (merge
                                      {:configurator (fn [server]
                                                       (configure-websocket! server)
                                                       (add-gzip-handler! server))}
                                      config))))))
  (stop [this]
    (log/info! :stopping-component {:component this})
    (.stop ^Server server)
    (-> this
        (assoc :config nil)
        (assoc :handler-provider nil)
        (assoc :server nil))))

(defn make-server []
  (map->JettyHttpServer {}))
