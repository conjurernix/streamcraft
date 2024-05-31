(ns streamcraft.http-server.core
  (:require [taoensso.timbre :as log]
            [streamcraft.protocols.api :as protocols]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty])
  (:import (streamcraft.protocols.api IHttpHandlerProvider)
           (org.eclipse.jetty.server Server)))

(defrecord JettyHttpServer
  [config
   ^IHttpHandlerProvider handler-provider
   ^Server server]
  component/Lifecycle
  (start [this]
    (if server
      this
      (do (log/info "Starting JettyHttpServer")
          (-> this
              (assoc :server
                     (-> handler-provider
                         (protocols/handler)
                         (jetty/run-jetty config)))))))
  (stop [this]
    (if server
      (do
        (log/info "Stopping JettyHttpServer")
        (.stop ^Server server)
        (-> this
            (assoc :handler-provider nil)
            (assoc :server nil)))
      this)))

(defn make-server [{:keys [jetty]}]
  (map->JettyHttpServer {:config jetty}))
