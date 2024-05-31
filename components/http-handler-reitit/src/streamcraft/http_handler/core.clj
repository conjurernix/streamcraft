(ns streamcraft.http-handler.core
  (:require [reitit.ring :as ring]
            [com.stuartsierra.component :as component]
            [streamcraft.protocols.api :as protocols]
            [taoensso.timbre :as log])
  (:import (streamcraft.protocols.api IHttpRouterProvider)))

(defrecord ReititHandlerProvider
  [^IHttpRouterProvider router-provider
   handler]
  component/Lifecycle
  (start [this]
    (if handler
      this
      (do (log/info "Starting ReititHandlerProvider")
          (->> router-provider
               (protocols/router)
               (ring/ring-handler)
               (assoc this :handler)))))
  (stop [this]
    (if handler
      (do (log/info "Stopping ReititHandlerProvider")
          (assoc this :handler nil))
      this))

  protocols/IHttpHandlerProvider
  (handler [_]
    handler))

(defn make-handler [_config]
  (map->ReititHandlerProvider {}))