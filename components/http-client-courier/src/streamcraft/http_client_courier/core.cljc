(ns streamcraft.http-client-courier.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.http-client :as http-client]
            [courier.client :as client]))

(defrecord CourierHttpClient []
  component/Lifecycle
  (start [this] this)

  (stop [this] this)

  http-client/IHttpClient
  (request [_ request]
    (client/request request)))

(defn make-http-client []
  (map->CourierHttpClient {}))
