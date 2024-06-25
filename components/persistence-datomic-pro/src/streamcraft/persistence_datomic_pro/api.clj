(ns streamcraft.persistence-datomic-pro.api
  (:require [streamcraft.persistence-datomic-pro.core :as core]))

(defn make-persistence []
  (core/make-persistence))
