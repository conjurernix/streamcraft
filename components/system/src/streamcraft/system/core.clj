(ns streamcraft.system.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.domain.api :as domain]
            [streamcraft.entity.api :as entity]
            [streamcraft.http-electric-handler.api :as http-electric-handler]
            [streamcraft.http-handler.api :as http-handler]
            [streamcraft.http-middleware.api :as http-middleware]
            [streamcraft.http-router.api :as http-router]
            [streamcraft.http-server.api :as http-server]
            [streamcraft.persistence-datomic-pro.api :as datomic-pro]
            ;[streamcraft.persistence-xtdb.api :as xtdb]
            [taoensso.timbre :as log]))

(defn start-system! [{::keys [name] :as system}]
  (when system
    (log/info "Starting system...")
    (log/info "Starting system " name)
    (component/start-system system)))

(defn stop-system! [{::keys [name] :as system}]
  (when system
    (log/info "Stopping system...")
    (log/info "Stopping system " name)
    (component/stop-system system)))

(defn make-system [{:keys [name entrypoint routes config]}]
  (log/info "Initializing system " name)
  (let [{:keys [xtdb jetty datomic hyperfiddle]} config]
    (component/system-map
      ::name name
      ;:xtdb-config xtdb
      :jetty-config jetty
      :datomic-config datomic
      :schemas domain/schemas
      :registry (component/using
                  (entity/make-registry)
                  [:schemas])
      ;:xtdb (component/using
      ;        (xtdb/make-persistence)
      ;        {:registry :registry
      ;         :config   :xtdb-config})
      :datomic-pro (component/using
                     (datomic-pro/make-persistence)
                     {:registry :registry
                      :config   :datomic-config})
      :http-middleware http-middleware/middleware
      :http-electric-handler (http-electric-handler/electric-handler entrypoint {:jetty       jetty
                                                                                 :hyperfiddle hyperfiddle})
      :http-routes routes
      :http-router (component/using
                     (http-router/make-router)
                     {:middleware       :http-middleware
                      :electric-handler :http-electric-handler
                      :routes           :http-routes
                      :config           :jetty-config})
      :http-handler (component/using
                      (http-handler/make-handler)
                      {:router-provider :http-router})
      :http-server (component/using
                     (http-server/make-server)
                     {:handler-provider :http-handler
                      :config           :jetty-config}))))


