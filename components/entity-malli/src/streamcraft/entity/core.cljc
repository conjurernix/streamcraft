(ns streamcraft.entity.core
  (:require [com.stuartsierra.component :as component]
            [malli.core :as m]
            [malli.generator :as mg]
            [malli.util :as mu]
            [potpuri.core :as pt]
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

  (get-registry [_] schemas)

  (get-entities [this]
    (->> schemas
         (pt/filter-vals #(er/entity? this %))))

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
    (::entity/entity? (meta schema)))

  (entity-id-key [this schema]
    (or (-> this
            (er/properties schema)
            ::entity/id)
        (throw (ex-info "Entity schema does not have an id key" {:schema schema}))))

  (entity-id [this schema value]
    (get value (er/entity-id-key this schema)))

  (select-entity-keys [_this schema entity]
    (-> schema
        (m/explicit-keys {:registry schemas})
        (->> (select-keys entity)))))


(defn make-registry []
  (map->MalliEntityRegistry {}))