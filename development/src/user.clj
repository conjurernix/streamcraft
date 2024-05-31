(ns user
  (:require [streamcraft.config.api :as config]
            [streamcraft.system.api :as system]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :as repl]
            [hashp.core]))

(defonce admin-config (config/load-config! "admin-base/config.edn"))

(defonce admin-system (system/make-system admin-config))

(defonce client-config (config/load-config! "client-base/config.edn"))

(defonce client-system (system/make-system client-config))

(defn start-admin! []
  (alter-var-root #'admin-system component/start))

(defn stop-admin! []
  (alter-var-root #'admin-system component/stop))

(defn start-client! []
  (alter-var-root #'client-system component/start))

(defn stop-client! []
  (alter-var-root #'client-system component/stop))

(defn go []
  (start-admin!)
  (start-client!))

(defn halt []
  (stop-admin!)
  (stop-client!))

(defn reset []
  (halt)
  (repl/refresh-all :after 'user/go))