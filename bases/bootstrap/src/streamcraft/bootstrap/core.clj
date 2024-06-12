(ns streamcraft.bootstrap.core
  (:require [streamcraft.config.api :as config]
            [streamcraft.system.api :as system]
            [taoensso.timbre :as log]))

(defn bootstrap-system!
  [{:keys [name config-path routes entrypoint]}]
  (let [config (config/load-config! config-path)
        system (system/make-system {:name       name
                                    :entrypoint entrypoint
                                    :routes     routes
                                    :config     config})]
    (log/info (str "Starting system: " name))
    (system/start-system! system)))
