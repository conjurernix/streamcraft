(ns streamcraft.redis-store-carmine.api
  (:require [streamcraft.redis-store-carmine.core :as core]))

(defn make-redis-store []
  (core/make-redis-store))
