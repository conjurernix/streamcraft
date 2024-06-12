(ns user
  (:require [clojure.tools.namespace.repl :as tools.repl]
            [hashp.core]
            [shadow.cljs.devtools.api :as shadow.api]
            [shadow.cljs.devtools.server :as shadow.server]
            [streamcraft.admin-base.main :as admin]
            [streamcraft.client-base.main :as client]
            [streamcraft.repl.core :as repl]
            [streamcraft.system.api :as system]))

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
  (tools.repl/refresh-all :after 'user/go))

(defn start-shadow! []
  (shadow.server/start!)
  (shadow.api/watch :admin-dev)
  (shadow.api/watch :client-dev))

(defn stop-shadow! []
  (shadow.server/stop!))