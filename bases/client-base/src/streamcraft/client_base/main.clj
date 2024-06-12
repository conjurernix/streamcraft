(ns streamcraft.client-base.main
  (:require [hyperfiddle.electric :as e]
            [streamcraft.bootstrap.core :refer [bootstrap-system!]]
            [streamcraft.client-base.app :as entrypoint])
  (:gen-class))

(defn server-entrypoint [req]
  (e/boot-server {} entrypoint/App req))

(defn start! []
  (bootstrap-system! {:name        "Client System"
                      :config-path "client-base/config.edn"
                      :routes      []
                      :entrypoint  (fn [ring-request] (e/boot-server {} entrypoint/App ring-request))}))

(defn -main [& args]
  (start!))
