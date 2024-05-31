(ns streamcraft.client-base.main
  (:require [streamcraft.bootstrap-server.core :refer [bootstrap-server!]])
  (:gen-class))

(defn -main [& args]
  (bootstrap-server! "client-base/config.edn"))
