(ns streamcraft.http-handler.core
  (:require [com.stuartsierra.component :as component]
            [reitit.ring :as ring]
            [streamcraft.protocols.api.provider.http-handler :as handler]
            [streamcraft.protocols.api.provider.http-router :as router]
            [streamcraft.logging.api :as log]))

(defrecord ReititHandlerProvider
  [router-provider handler]

  component/Lifecycle
  (start [this]
    (log/info! :starting-component {:component this})
    (->> router-provider
         (router/get-router)
         (ring/ring-handler)
         (assoc this :handler)))

  (stop [this]
    (log/info! :stopping-component {:component this})
    (assoc this :handler nil))

  handler/IHttpHandlerProvider
  (get-handler [_]
    handler))

(defn make-handler []
  (map->ReititHandlerProvider {}))