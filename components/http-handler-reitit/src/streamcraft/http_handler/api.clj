(ns streamcraft.http-handler.api
  (:require [streamcraft.http-handler.core :as core]))

(defn make-handler [config]
  (core/make-handler config))