(ns streamcraft.http-handler.core
  (:require [com.stuartsierra.component :as component]
            [reitit.ring :as ring]
            [streamcraft.protocols.api.observability :as obs]
            [streamcraft.protocols.api.provider.http-handler :as handler]
            [streamcraft.protocols.api.provider.http-router :as router]))

(defrecord ReititHandlerProvider
  [obs router-provider handler]

  component/Lifecycle
  (start [this]
    (obs/info! obs :starting-component {:component ReititHandlerProvider})
    (->> router-provider
         (router/get-router)
         (ring/ring-handler)
         (assoc this :handler)))

  (stop [this]
    (obs/info! obs :starting-component {:component ReititHandlerProvider})
    (assoc this :handler nil))

  handler/IHttpHandlerProvider
  (get-handler [_]
    handler))

(defn make-handler []
  (map->ReititHandlerProvider {}))