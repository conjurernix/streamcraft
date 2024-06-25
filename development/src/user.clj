(ns user
  (:require [clojure.tools.namespace.repl :as tools.repl]
    ;[clojure.repl.deps :as deps]
            [hashp.core]
            [shadow.cljs.devtools.api :as shadow.api]
            [shadow.cljs.devtools.server :as shadow.server]
            [streamcraft.admin-base.main :as admin]
            [streamcraft.client-base.main :as client]
            [streamcraft.repl.core :as repl]
            [streamcraft.system.api :as system]
            [datomic.api :as d]))

(repl/start-nrepl!)

(defonce admin-system (admin/start!))

(defonce client-system (client/start!))

(defn start-admin! []
  (alter-var-root #'admin-system system/start-system!))

(defn stop-admin! []
  (alter-var-root #'admin-system system/stop-system!))

(defn start-client! []
  (alter-var-root #'client-system system/start-system!))
;
(defn stop-client! []
  (alter-var-root #'client-system system/stop-system!))

(defn go []
  (start-admin!)
  (start-client!))

(defn halt []
  (stop-admin!)
  (stop-client!))

(defn reset []
  (halt)
  ;(deps/sync-deps)
  (tools.repl/refresh-all :after 'user/go))

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
  (def datomic (:datomic-pro admin-system))

  ;; add a person to datomic
  (let [{:keys [conn]} datomic
        person {:first-name "John" :last-name "Doe" :age 30}]
    (d/transact (:conn datomic) [{:streamcraft.protocols.api.persistence/schema :person
                                  :streamcraft.protocols.api.persistence/data person}]))
  )