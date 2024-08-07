(ns streamcraft.system.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.domain.api :as domain]
            [streamcraft.entity-manager.api :as entity]
            [streamcraft.http-electric-handler.api :as http-electric-handler]
            [streamcraft.http-handler.api :as http-handler]
            [streamcraft.http-middleware.api :as http-middleware]
            [streamcraft.http-router.api :as http-router]
            [streamcraft.http-server.api :as http-server]
            [streamcraft.migration-datomic.api :as datomic.migration]
            [streamcraft.persistence-datomic-pro.api :as datomic-pro]
    ;[streamcraft.persistence-xtdb.api :as xtdb]
            [streamcraft.persistence-schema-transformer-malli-datomic.api :as m.d.persistence-schema-transformer]
            [streamcraft.protocols.api.observability :as obs]))

(defn start-system! [{:keys [obs] :as system}]
  (when system
    (obs/info! obs :starting-system {})
    (component/start-system system)))

(defn stop-system! [{:keys [obs] :as system}]
  (when system
    (obs/info! obs :stopping-system {})
    (component/stop-system system)))

(defn make-system [{:keys [name entrypoint routes config]}]
  (let [{:keys [jetty datomic hyperfiddle]} config]
    (component/system-map
      ::name name

      :jetty-config jetty
      :datomic-config datomic
      :schemas domain/schemas
      :entity-manager (component/using
                  (entity/make-entity-manager)
                  [:schemas])

      :datomic-persistence-schema-transformer (component/using
                                                (m.d.persistence-schema-transformer/make-persistence-schema-transformer)
                                                [:entity-manager])

      :datomic-migration (component/using
                           (datomic.migration/make-migration)
                           {:entity-manager                :entity-manager
                            :persistence-transformer :datomic-persistence-schema-transformer})

      :datomic-persistence (component/using
                             (datomic-pro/make-persistence)
                             {:entity-manager :entity-manager
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


