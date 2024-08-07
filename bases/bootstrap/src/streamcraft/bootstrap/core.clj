(ns streamcraft.bootstrap.core
  (:require [streamcraft.config.api :as config]
            [streamcraft.system.api :as system]))

(defn bootstrap-system!
  [{:keys [name config-path routes entrypoint]}]
  (let [config (config/load-config! config-path)
        system (system/make-system {:name       name
                                    :entrypoint entrypoint
                                    :routes     routes
                                    :config     config})]
    (system/start-system! system)))
