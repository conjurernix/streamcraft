(ns streamcraft.admin-base.main
  (:require [streamcraft.bootstrap-server.core :refer [bootstrap-server!]])
  (:gen-class))

(defn -main [& args]
  (bootstrap-server! "admin-base/config.edn"))
