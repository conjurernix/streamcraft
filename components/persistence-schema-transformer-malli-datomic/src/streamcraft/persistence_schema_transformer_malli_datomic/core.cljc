(ns streamcraft.persistence-schema-transformer-malli-datomic.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.transformer.schema :as ts]
            [taoensso.timbre :as log]))

(defn malli-schema->datomic-valuetype
  "Converts (almost) any built-in Malli schema to a Datomic schema"
  [schema]
  (condp contains? schema
    #{'number? 'integer? 'int? 'pos-int? 'neg-int? 'nat-int?
      number? integer? int? pos-int? neg-int? nat-int?
      :int} :db.type/long

    #{'number? 'float? 'double?
      number? float? double?
      :double} :db.type/double

    #{'keyword? 'simple-keyword? 'qualified-keyword? 'simple-ident? 'qualified-ident?
      keyword? simple-keyword? qualified-keyword? simple-ident? qualified-ident?
      :keyword :qualified-keyword} :db.type/keyword

    #{'symbol? 'simple-symbol? 'qualified-symbol?
      symbol? simple-symbol? qualified-symbol?
      :symbol :qualified-symbol} :db.type/symbol

    #{'string? string?
      :string :re} :db.type/string

    #{'boolean? boolean? :boolean} :db.type/boolean

    #{'decimal? decimal?} :db.type/bigdec

    #{'uuid? uuid? :uuid} :db.type/uuid

    #{'uri? uri?} :db.type/uri

    #{'inst? inst? :time/instant} :db.type/instant

    #{'bytes? bytes?} :db.type/bytes))

(defrecord MalliDatomicPersistenceSchemaTransformer [registry]

  component/Lifecycle

  (start [this]
    (log/info "Starting EntityPersistenceSchemaTransformer")
    this)

  (stop [this]
    (log/info "Stopping EntityPersistenceSchemaTransformer")
    (-> this
        (assoc :registry nil)))

  ts/ISchemaTransformer
  (transform [_this schema]
    (or (malli-schema->datomic-valuetype (er/of-type registry schema))
        (throw (ex-info "Can't convert schema to persistence schema" {:schema schema})))))

(defn make-persistence-schema-transformer []
  (map->MalliDatomicPersistenceSchemaTransformer {}))