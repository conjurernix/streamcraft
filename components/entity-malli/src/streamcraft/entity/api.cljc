(ns streamcraft.entity.api
  (:require [streamcraft.entity.core :as core]))

(defmacro defschema [name id schema]
  `(core/defschema ~name ~id ~schema))

(defmacro defentity [name id schema]
  `(core/defentity ~name ~id ~schema))

(defn make-registry []
  (core/make-registry))