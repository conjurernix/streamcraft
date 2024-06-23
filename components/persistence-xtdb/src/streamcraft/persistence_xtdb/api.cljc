(ns streamcraft.persistence-xtdb.api
  (:require [streamcraft.persistence-xtdb.core :as core]))

(defn make-persistence []
  (core/make-persistence))
