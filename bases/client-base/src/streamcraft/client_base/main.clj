(ns streamcraft.client-base.main
  (:require [streamcraft.bootstrap-server.core :refer [bootstrap-server!]]
            [streamcraft.client-base.entrypoint :as entrypoint])
  (:gen-class))

(defn start! []
  (bootstrap-server! "client-base/config.edn" [] entrypoint/Main))

(defn -main [& args]
  (start!))
