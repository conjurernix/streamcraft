(ns streamcraft.persistence-datomic-pro.core
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [streamcraft.datalog-query-builder.api :refer [build-query]]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.protocols.api.persistence :as persistence]
            [taoensso.timbre :as log])
  (:import (clojure.lang ExceptionInfo)))


(defrecord DatomicProPersistence [config migration entity-manager conn txops]

  component/Lifecycle
  (start [this]
    (log/info "Starting DatomicProPersistence")
    (let [{:keys [uri]} config]
      (d/create-database uri)
      (let [conn (d/connect uri)]
        (when migration
          (let [schema (migration/gen-migration migration)]
            (when-let [tx-data (seq schema)]
              (log/info "Transacting Datomic Schema")
              @(d/transact conn tx-data))))
        this (-> this
                 (assoc :conn conn)
                 (persistence/clear-txs)))))

  (stop [this]
    (log/info "Stopping DatomicProPersistence")
    (let [{:keys [uri]} config]
      (d/delete-database uri)
      (-> this
          (assoc :config nil)
          (assoc :migration nil)
          (assoc :entity-manager nil)
          (assoc :conn nil)
          (assoc :txops nil))))

  persistence/IPersistence
  (db-id-key [_this _schema] :db/id)

  ; TODO: Not the most efficient
  (db-id [this schema entity-id]
    (let [entity (persistence/fetch this schema entity-id)]
      (get entity (persistence/db-id-key this schema))))

  (prepare [_this schema data]
    (try
      (em/validate entity-manager schema data)
      (catch ExceptionInfo e
        (let [entity-name (em/name entity-manager schema)]
          (throw (ex-info (str "Data is not a valid " entity-name)
                          {:entity-schema schema
                           :errors        (ex-data e)}
                          e)))))
    data)

  (fetch [_this schema id]
    (let [entity-id-key (em/entity-id-key entity-manager schema)
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

  (search [_this schema {:keys [keys where] :as _opts}]
    (let [db (d/db conn)
          {:keys [query args]} (build-query {:keys  keys
                                             :where where}
                                            (em/entity-id-key entity-manager schema))
          q-fn (partial d/q query db)
          res (-> q-fn
                  (apply args))]
      (apply concat res)))

  (persist! [this schema data]
    (let [data (persistence/prepare this schema data)]
      (-> this
          ;; TODO: Data is being prepared twice, but it's fine for now
          (persistence/persist schema data)
          (persistence/transact!))
      (persistence/fetch this schema (em/entity-id entity-manager schema data))))

  (patch! [this schema id data]
    (let [data (persistence/prepare this (em/optional-keys entity-manager schema) data)]
      (-> this
          ;; TODO: Data is being prepared twice, but it's fine for now
          (persistence/patch schema id data)
          (persistence/transact!))
      ; Not sure if this is a good idea
      (persistence/fetch this schema id)))

  (delete! [this schema id]
    (-> this
        (persistence/delete schema id)
        (persistence/transact!))
    id)

  persistence/ITransactionalPersistence
  (persist [this schema data]
    (-> this
        (update :txops conj
                {:tx-action     :persist
                 :entity-schema schema
                 :entity-data   (persistence/prepare this schema data)})))

  (patch [this schema id data]
    (-> this
        (update :txops conj
                {:tx-action     :patch
                 :entity-schema schema
                 :entity-id     id
                 :entity-data   (persistence/prepare this (em/optional-keys entity-manager schema) data)})))

  (delete [this schema id]
    (-> this
        (update :txops conj
                {:tx-action     :delete
                 :entity-schema schema
                 :entity-id     id})))

  (clear-txs [this]
    (assoc this :txops []))

  (transact! [this]
    (let [tx-data (->> txops
                          (map
                            (fn [{:keys [tx-action entity-id entity-schema entity-data]}]
                              (case tx-action
                                :persist entity-data
                                :patch (let [db-id (persistence/db-id this entity-schema entity-id)]
                                         (assoc entity-data :db/id db-id))
                                :delete (let [db-id (persistence/db-id this entity-schema entity-id)]
                                          [:db.fn/retractEntity db-id]))))
                          (into []))]
      @(d/transact conn tx-data)
      (-> this
          (persistence/clear-txs)))))


(defn make-persistence []
  (map->DatomicProPersistence {}))