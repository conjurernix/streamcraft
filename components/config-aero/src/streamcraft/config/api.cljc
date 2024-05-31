(ns streamcraft.config.api
  (:require [streamcraft.config.core :as core]))

(defn load-config! [config-path]
  (core/load-config! config-path))


