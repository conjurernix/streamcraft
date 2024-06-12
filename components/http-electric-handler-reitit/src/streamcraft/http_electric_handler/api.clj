(ns streamcraft.http-electric-handler.api
  (:require [streamcraft.http-electric-handler.core :as core]))

(defn electric-handler [entrypoint config]
  (core/electric-handler entrypoint config))
