(ns streamcraft.migration-datomic.api
  (:require [streamcraft.migration-datomic.core :as core]))

(defn make-migration []
  (core/make-migration))
