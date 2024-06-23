(ns streamcraft.entity.api
  (:require [streamcraft.entity.core :as core]))

(def registry core/registry)

(defn add-to-registry! [key schema]
  (core/add-to-registry! key schema))

(defmacro defschema [name id schema]
  `(core/defschema ~name ~id ~schema))

(defn as-entity [schema]
  (core/as-entity schema))

(defmacro defentity [name id schema]
  `(core/defentity ~name ~id ~schema))

(defn entity? [schema]
  (core/entity? schema))

(defn init-registry! []
  (core/init-registry!))

(defn entity-name [schema]
  (core/entity-name schema))