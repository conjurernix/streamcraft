(ns streamcraft.http-defaults.api
  (:require [streamcraft.http-defaults.core :as core]))

(def default-middleware core/default-middleware)

(defn default-route-setup [routes config entrypoint]
  (core/default-route-setup routes config entrypoint))
