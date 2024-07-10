(ns streamcraft.entity.core
  (:require [com.stuartsierra.component :as component]
            [malli.core :as m]
            [malli.generator :as mg]
            [malli.util :as mu]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.entity-registry :as er]
            [taoensso.timbre :as log]))

(defn as-entity [schema]
  (with-meta schema {::entity/entity? true}))

(defrecord MalliEntityRegistry [schemas]
  component/Lifecycle

  (start [this]
    (log/info "Starting MalliEntityRegistry")
    (-> this
        (update :schemas merge (m/default-schemas) (mu/schemas))))

  (stop [this]
    (log/info "Stopping MalliEntityRegistry")
    (-> this
        (assoc :schemas nil)))

  er/EntityRegistry

  (get-registry [this] schemas)

  (get-entities [this]
    (->> schemas
         (filter #(er/entity? this %))))

  (merge-registry [this new-schemas]
    (-> this
        (update :schemas merge new-schemas)))

  (validate [_this schema data]
    (m/validate schema data {:registry schemas}))

  (generate [_this schema]
    (mg/generate schema {:registry schemas}))

  (name [_this schema]
    (-> schema
        (m/properties {:registry schemas})
        ::entity/name))

  (properties [_this schema]
    (m/properties schema {:registry schemas}))

  (entries [_this schema]
    (m/entries schema {:registry schemas}))

  (of-type [_this schema]
    ; If a collection schema (vector, set, sequential), return the first child schema
    (if (contains? #{:vector :set :sequential} (m/type schema))
      (-> schema
          (m/children)
          (first)
          (m/type))
      (m/type schema)))

  (cardinality [this schema]
    (or (-> this
            (er/properties schema)
            ::entity/cardinality)
        (if (contains? #{:vector :set :sequential} (m/type schema))
          :many
          :one)))

  (entity? [_this schema]
    (-> schema
        (m/properties {:registry schemas})
        ::entity/entity?)))


(defn make-registry []
  (map->MalliEntityRegistry {}))