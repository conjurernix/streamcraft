(ns streamcraft.http-router.api
  (:require [streamcraft.http-router.core :as core]))

(defn make-router [config]
  (core/make-router config))
