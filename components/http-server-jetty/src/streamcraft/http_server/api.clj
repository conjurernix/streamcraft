(ns streamcraft.http-server.api
  (:require [streamcraft.http-server.core :as core]))

(defn make-server [config]
  (core/make-server config))