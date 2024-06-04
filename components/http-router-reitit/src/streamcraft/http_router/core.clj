(ns streamcraft.http-router.core
  (:require [com.stuartsierra.component :as component]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as rcm]
            [reitit.ring :as ring]
            [streamcraft.protocols.api :as protocols]
            [taoensso.timbre :as log]))

(defn- -make-reitit-router [routes middleware]
  (ring/router
    routes
    {:conflicts (constantly nil)
     :data      {:muuntaja   m/instance
                 :coercion   rcm/coercion
                 :middleware middleware}}))

(defrecord ReititRouter
  [middleware routes router]
  component/Lifecycle

  (start [this]
    (if router
      this
      (do (log/info "Starting ReititRouter")
          (assoc this :router (-make-reitit-router routes middleware)))))
  (stop [this]
    (if router
      (do (log/info "Stopping ReititRouter")
          (assoc this :router nil))
      this))


  protocols/IHttpRouterProvider
  (routes [_this] routes)
  (router [_this] router))

(defn make-router [_config]
  (map->ReititRouter {}))