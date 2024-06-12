(ns streamcraft.admin-base.main
  (:require [hyperfiddle.electric :as e]
            [streamcraft.bootstrap.core :refer [bootstrap-system!]]
            [streamcraft.admin-base.app :as entrypoint])
  (:gen-class))

(defn server-entrypoint [req]
  (e/boot-server {} entrypoint/App req))

(defn start! []
  (bootstrap-system! {:name        "Admin System"
                      :config-path "admin-base/config.edn"
                      :routes      []
                      :entrypoint  (fn [ring-request] (e/boot-server {} entrypoint/App ring-request))}))

(defn -main [& args]
  (start!))
