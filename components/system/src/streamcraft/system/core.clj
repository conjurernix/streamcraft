(ns streamcraft.system.core
  (:require [streamcraft.http-defaults.api :as http-defaults]
            [streamcraft.http-handler.api :as http-handler]
            [streamcraft.http-router.api :as http-router]
            [streamcraft.http-server.api :as http-server]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]))

(defn start-system! [system]
  (log/info "Starting system...")
  (component/start-system system))

(defn stop-system! [system]
  (log/info "Stopping system...")
  (component/stop-system system))

(defn make-system [config]
  (log/info "Initializing system...")
  (component/system-map
    :http-middleware http-defaults/default-middleware
    :http-routes (http-defaults/default-route-setup [])
    :http-router (component/using
                   (http-router/make-router config)
                   {:middleware :http-middleware
                    :routes     :http-routes})
    :http-handler (component/using
                    (http-handler/make-handler config)
                    {:router-provider :http-router})
    :http-server (component/using
                   (http-server/make-server config)
                   {:handler-provider :http-handler})))


