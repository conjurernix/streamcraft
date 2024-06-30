(ns streamcraft.entity-persistence-schema-transformer-malli-datomic.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.schema :as schema]
            [taoensso.timbre :as log]))

(defn malli-schema->datomic-valuetype [schema]
  ; TODO: Add more types that would map to persistence types
  (case schema
    :string :db.type/string
    :boolean :db.type/boolean
    :long :db.type/long
    :double :db.type/double
    :bigdec :db.type/bigdec
    :bigint :db.type/bigint
    :uuid :db.type/uuid
    :uri :db.type/uri
    :keyword :db.type/keyword
    :symbol :db.type/symbol
    :instant :db.type/instant
    :bytes :db.type/bytes
    :ref :db.type/ref
    :enum :db.type/enum))

(defrecord MalliDatomicEntityPersistenceSchemaTransformer [registry]

  component/Lifecycle

  (start [this]
    (log/info "Starting EntityPersistenceSchemaTransformer")
    this)

  (stop [this]
    (log/info "Stopping EntityPersistenceSchemaTransformer")
    (-> this
        (assoc :registry nil)))

  schema/IEntityPersistenceSchemaTransformer
  (persistence-schema [_this schema]
    (or (malli-schema->datomic-valuetype (er/of-type registry schema))
        (throw (ex-info "Can't convert schema to Datomic schema" {:schema schema})))))

(defn make-entity-persistence-schema-transformer []
  (map->MalliDatomicEntityPersistenceSchemaTransformer {}))