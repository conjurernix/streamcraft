(ns streamcraft.system.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.http-middleware.api :as http-middleware]
            [streamcraft.http-electric-handler.api :as http-electric-handler]
            [streamcraft.http-handler.api :as http-handler]
            [streamcraft.http-router.api :as http-router]
            [streamcraft.http-server.api :as http-server]
            [taoensso.timbre :as log]))

(defn start-system! [{::keys [name] :as system}]
  (when system
    (log/info "Starting system " name)
    (component/start-system system)))

(defn stop-system! [{::keys [name] :as system}]
  (when system
    (log/info "Stopping system " name)
    (component/stop-system system)))

(defn make-system [{:keys [name entrypoint routes config]}]
  (log/info "Initializing system " name)
  (component/system-map
    ::name name
    :config config
    :http-middleware http-middleware/middleware
    :http-electric-handler (http-electric-handler/electric-handler entrypoint config)
    :http-routes routes
    :http-router (component/using
                   (http-router/make-router)
                   {:middleware       :http-middleware
                    :electric-handler :http-electric-handler
                    :routes           :http-routes
                    :config           :config})
    :http-handler (component/using
                    (http-handler/make-handler)
                    {:router-provider :http-router
                     :config          :config})
    :http-server (component/using
                   (http-server/make-server)
                   {:handler-provider :http-handler
                    :config           :config})))


