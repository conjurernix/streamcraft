(ns streamcraft.http-electric-handler.api
  (:require [streamcraft.http-electric-handler.core :as core]))

; TODO: maybe there should be a electric handler provided that's a component
(defn electric-handler [entrypoint config]
  (core/electric-handler entrypoint config))
