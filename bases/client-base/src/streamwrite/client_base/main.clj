(ns streamwrite.client-base.main
  (:require [hyperfiddle.electric :as e]
            [streamcraft.bootstrap.core :refer [bootstrap-system!]]
            [streamwrite.client-base.app :as entrypoint])
  (:gen-class))

(defn server-entrypoint [req]
  (e/boot-server {} entrypoint/App req))

(defn start! []
  (bootstrap-system! {:name        "Client System"
                      :config-path "client-base/config.edn"
                      :routes      []
                      :entrypoint  server-entrypoint}))

(defn -main [& args]
  (start!))
