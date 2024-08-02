(ns streamcraft.http-handler.core
  (:require [com.stuartsierra.component :as component]
            [reitit.ring :as ring]
            [streamcraft.protocols.api.provider.http-handler :as handler]
            [streamcraft.protocols.api.provider.http-router :as router]
            [taoensso.timbre :as log])
  (:import (streamcraft.protocols.api.provider.http_router IHttpRouterProvider)))

(defrecord ReititHandlerProvider
  [^IHttpRouterProvider router-provider
   handler]
  component/Lifecycle
  (start [this]
    (if handler
      this
      (do (log/info "Starting ReititHandlerProvider")
          (->> router-provider
               (router/get-router)
               (ring/ring-handler)
               (assoc this :handler)))))
  (stop [this]
    (if handler
      (do (log/info "Stopping ReititHandlerProvider")
          (assoc this :handler nil))
      this))

  handler/IHttpHandlerProvider
  (get-handler [_]
    handler))

(defn make-handler []
  (map->ReititHandlerProvider {}))