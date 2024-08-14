(ns user
  (:require [clojure.tools.namespace.repl :as tools.repl]
            [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [hashp.core]
            [shadow.cljs.devtools.api :as shadow.api]
            [shadow.cljs.devtools.server :as shadow.server]
            [streamcraft.admin-base.main :as admin]
            [streamcraft.client-base.main :as client]
            [streamcraft.domain.api :as domain]
            [streamcraft.email-client-mailgun.api :as mailgun.email]
            [streamcraft.entity-manager.api :as entity]
            [streamcraft.http-electric-handler.api :as http-electric-handler]
            [streamcraft.http-handler.api :as http-handler]
            [streamcraft.http-middleware.api :as http-middleware]
            [streamcraft.http-router.api :as http-router]
            [streamcraft.http-server.api :as http-server]
            [streamcraft.migration-datomic.api :as datomic.migration]
            [streamcraft.observability-mulog.api :as obs.mulog]
            [streamcraft.persistence-datomic-pro.api :as datomic-pro]
            [streamcraft.persistence-schema-transformer-malli-datomic.api :as m.d.persistence-schema-transformer]
            [streamcraft.repl.core :as repl]
            [streamcraft.system.api :as system]))

(repl/start-nrepl!)

(defonce system
         (let [admin-jetty-config {:port           8080
                                   :join?          false
                                   :resources-path "admin/public"
                                   :public-path    "/public"}
               client-jetty-config {:port           8081
                                    :join?          false
                                    :resources-path "client/public"
                                    :public-path    "/public"}
               admin-hyperfiddle {:manifest-path                     "admin/public/js/manifest.edn"
                                  :hyperfiddle.electric/user-version "dev"}
               client-hyperfiddle {:manifest-path                     "admin/public/js/manifest.edn"
                                   :hyperfiddle.electric/user-version "dev"}
               admin-routes []
               client-routes []]
           (component/system-map

             :xtdb-config {}
             :admin-jetty-config admin-jetty-config
             :client-jetty-config client-jetty-config
             :datomic-config {:uri "datomic:mem://streamcraft"}
             :obs-config {:publishers [{:type :console}]}
             :obs (component/using
                    (obs.mulog/make-observability)
                    {:config :obs-config})
             :schemas domain/schemas
             :email-client-config {}
             :email-client (component/using
                             (mailgun.email/make-email-client)
                             {:config :email-client-config
                              :obs    :obs})
             :entity-manager (component/using
                               (entity/make-entity-manager)
                               [:obs :schemas])

             :datomic-persistence-schema-transformer (component/using
                                                       (m.d.persistence-schema-transformer/make-persistence-schema-transformer)
                                                       [:obs :entity-manager])

             :datomic-migration (component/using
                                  (datomic.migration/make-migration)
                                  {:obs                     :obs
                                   :entity-manager          :entity-manager
                                   :persistence-transformer :datomic-persistence-schema-transformer})

             :datomic-persistence (component/using
                                    (datomic-pro/make-persistence)
                                    {:obs            :obs
                                     :entity-manager :entity-manager
                                     :config         :datomic-config})

             :http-middleware http-middleware/middleware

             :admin-electric-handler (http-electric-handler/electric-handler
                                       admin/server-entrypoint
                                       {:jetty       admin-jetty-config
                                        :hyperfiddle admin-hyperfiddle})

             :client-electric-handler (http-electric-handler/electric-handler
                                        client/server-entrypoint
                                        {:jetty       client-jetty-config
                                         :hyperfiddle client-hyperfiddle})

             :admin-routes admin-routes

             :client-routes client-routes

             :admin-router (component/using
                             (http-router/make-router)
                             {:obs              :obs
                              :middleware       :http-middleware
                              :electric-handler :admin-electric-handler
                              :routes           :admin-routes
                              :config           :admin-jetty-config})

             :client-router (component/using
                              (http-router/make-router)
                              {:obs              :obs
                               :middleware       :http-middleware
                               :electric-handler :client-electric-handler
                               :routes           :client-routes
                               :config           :client-jetty-config})

             :admin-handler (component/using
                              (http-handler/make-handler)
                              {:obs             :obs
                               :router-provider :admin-router})

             :client-handler (component/using
                               (http-handler/make-handler)
                               {:obs             :obs
                                :router-provider :client-router})

             :admin-server (component/using
                             (http-server/make-server)
                             {:obs              :obs
                              :handler-provider :admin-handler
                              :config           :admin-jetty-config})

             :client-server (component/using
                              (http-server/make-server)
                              {:obs              :obs
                               :handler-provider :client-handler
                               :config           :client-jetty-config}))))

(defn start! []
  (alter-var-root #'system system/start-system!))

(defn stop! []
  (alter-var-root #'system system/stop-system!))

(defn go []
  (start!))

(defn halt []
  (stop!))

(defn reset []
  (halt)
  ;(deps/sync-deps)
  (tools.repl/refresh-all :after 'user/go))

(defn refresh []
  (tools.repl/refresh-all))

(defn start-shadow! []
  (shadow.server/start!)
  (shadow.api/watch :admin-dev)
  (shadow.api/watch :client-dev))

(defn stop-shadow! []
  (shadow.server/stop!))

(defn restart-shadow! []
  (stop-shadow!)
  (start-shadow!))


(comment
  (def datomic (:datomic-persistence admin-system))

  ;; add a person to datomic
  (let [{:keys [conn]} datomic
        person {:first-name "John" :last-name "Doe" :age 30}]
    (d/transact (:conn datomic) [{:streamcraft.protocols.api.persistence/schema :person
                                  :streamcraft.protocols.api.persistence/data   person}]))
  )