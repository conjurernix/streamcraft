(ns streamcraft.http-server.core
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [streamcraft.protocols.api :as protocols]
            [taoensso.timbre :as log])
  (:import (java.time Duration)
           (org.eclipse.jetty.server Server)
           (org.eclipse.jetty.server.handler.gzip GzipHandler)
           (org.eclipse.jetty.websocket.server.config JettyWebSocketServletContainerInitializer JettyWebSocketServletContainerInitializer$Configurator)
           (streamcraft.protocols.api IHttpHandlerProvider)))

(defn- add-gzip-handler!
  "Makes Jetty server compress responses. Optional but recommended."
  [server]
  (.setHandler server
               (doto (GzipHandler.)
                 #_(.setIncludedMimeTypes
                     (-> ["text/css" "text/plain" "text/javascript"
                          "application/javascript" "application/json"
                          "image/svg+xml"]
                         (into-array)))                     ; only compress these
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
  [config
   ^IHttpHandlerProvider handler-provider
   ^Server server]
  component/Lifecycle
  (start [this]
    (if server
      this
      (do (log/info "Starting JettyHttpServer")
          (let [{:keys [jetty]} config]
            (-> this
                (assoc :server
                       (-> (protocols/handler handler-provider)
                           (jetty/run-jetty (merge
                                              {:configurator (fn [server]
                                                               (configure-websocket! server)
                                                               (add-gzip-handler! server))}
                                              jetty)))))))))
  (stop [this]
    (if server
      (do
        (log/info "Stopping JettyHttpServer")
        (.stop ^Server server)
        (-> this
            (assoc :config nil)
            (assoc :handler-provider nil)
            (assoc :server nil)))
      this)))

(defn make-server []
  (map->JettyHttpServer {}))
