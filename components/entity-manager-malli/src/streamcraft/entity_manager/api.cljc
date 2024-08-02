(ns streamcraft.entity-manager.api
  (:require [streamcraft.entity-manager.core :as core]))

(defn make-entity-manager []
  (core/make-entity-manager))

