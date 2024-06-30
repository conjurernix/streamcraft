(ns streamcraft.entity.api
  (:require [streamcraft.entity.core :as core]))

(defn as-entity [schema]
  (core/as-entity schema))

(defn make-registry []
  (core/make-registry))

