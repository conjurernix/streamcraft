(ns streamcraft.bootstrap-server.core
  (:require [streamcraft.config.api :as config]
            [streamcraft.system.api :as system]
            [streamcraft.repl.core :as repl]
            [taoensso.timbre :as log]))

(defn bootstrap-server!
  [config-path routes entrypoint]
  (let [config (config/load-config! config-path)
        {:keys [config] :as system} (system/make-system config routes entrypoint)]
    (if-let [nrepl (get config :nrepl)]
      (repl/start-nrepl! nrepl)
      (log/info "nREPL config not found, skipping"))
    (system/start-system! system)))
