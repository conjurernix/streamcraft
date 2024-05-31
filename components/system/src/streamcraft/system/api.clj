(ns streamcraft.system.api
  (:require [streamcraft.system.core :as core]))

(defn start-system! [system]
  (core/start-system! system))

(defn stop--system! [system]
  (core/stop-system! system))

(defn make-system [config]
  (core/make-system config))
