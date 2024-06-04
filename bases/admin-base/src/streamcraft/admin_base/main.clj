(ns streamcraft.admin-base.main
  (:require [streamcraft.bootstrap-server.core :refer [bootstrap-server!]]
            [streamcraft.admin-base.entrypoint :as entrypoint])
  (:gen-class))

(defn start! []
  (bootstrap-server! "admin-base/config.edn" [] entrypoint/Main))

(defn -main [& args]
  (start!))
