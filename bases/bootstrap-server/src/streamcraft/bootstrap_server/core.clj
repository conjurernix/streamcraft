(ns streamcraft.bootstrap-server.core
  (:require [streamcraft.config.api :as config]
            [streamcraft.system.api :as system]
            [streamcraft.repl.core :as repl]
            [taoensso.timbre :as log]))

(defn bootstrap-server!
  ([]
   (bootstrap-server! (-> "SERVER_CONFIG_PATH"
                          (System/getenv)
                          (or "config.edn"))))
  ([config-path]
   (let [config (config/load-config! config-path)
         {:keys [config] :as system} (system/make-system config)]
     (system/start-system! system)
     (if-let [nrepl (get config :nrepl)]
       (repl/start-nrepl! nrepl)
       (log/info "nREPL config not found, skipping")))))
