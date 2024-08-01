(ns streamcraft.persistence-datomic-pro.core
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [streamcraft.datalog-query-builder.api :refer [build-query]]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.protocols.api.persistence :as persistence]
            [taoensso.timbre :as log]))


(defrecord DatomicProPersistence [config migration registry conn txops]

  component/Lifecycle
  (start [this]
    (log/info "Starting DatomicProPersistence")
    (let [{:keys [uri]} config]
      (d/create-database uri)
      (let [conn (d/connect uri)
            txops []]
        (when migration
          (let [schema (migration/gen-migration migration)]
            (when-let [tx-data (seq schema)]
              (log/info "Transacting Datomic Schema")
              @(d/transact conn tx-data))))
        this (-> this
                 (assoc :conn conn)
                 (assoc :txops txops)))))

  (stop [this]
    (log/info "Stopping DatomicProPersistence")
    (let [{:keys [uri]} config]
      (d/delete-database uri)
      (-> this
          (assoc :config nil)
          (assoc :migration nil)
          (assoc :registry nil)
          (assoc :conn nil)
          (assoc :txops nil))))

  persistence/IPersistence
  (db-id-key [_this _schema] :db/id)

  (db-id [this schema data]
    (get data (persistence/db-id-key this schema)))

  (prepare [_this _schema data]
    data)

  (fetch [_this schema id]
    (let [entity-id-key (er/entity-id-key registry schema)
          db (d/db conn)
          entity (-> '[:find [(pull ?e [*])]
                       :in $ ?id-attr ?id
                       :where [?e ?id-attr ?id]]
                     (d/q db entity-id-key id)
                     (first))]
      (when-not entity
        (throw (ex-info "Entity not found" {:schema  schema
                                            :id-attr entity-id-key
                                            :id      id})))
      entity))

  (search [this schema]
    (persistence/search this schema {}))

  (search [this schema {:keys [keys where] :as opts}]
    (let [db (d/db conn)
          {:keys [query args]} (build-query {:keys  keys
                                             :where where}
                                            (er/entity-id-key registry schema))
          q-fn (partial d/q query db)
          res (-> q-fn
                  (apply args))]
      (apply concat res)))

  (persist! [this schema data]
    @(d/transact conn [data]))

  (patch! [this schema id data])

  (delete! [this id])

  persistence/ITransactionalPersistence
  (persist [this schema data])

  (patch [this schema id data])

  (delete [this id])

  (clear-txs [this])

  (transact! [this]))


(defn make-persistence []
  (map->DatomicProPersistence {}))