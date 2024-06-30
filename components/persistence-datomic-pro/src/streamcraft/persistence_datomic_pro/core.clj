(ns streamcraft.persistence-datomic-pro.core
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [streamcraft.protocols.api.persistence :as persistence]
            [taoensso.timbre :as log]))


(defrecord DatomicProPersistence [config migration registry conn txops]

  component/Lifecycle
  (start [this]
    (log/info "Starting DatomicProPersistence")
    (let [{:keys [uri]} config]
      (d/create-database uri)
      (-> this
          (assoc :conn (d/connect uri))
          (assoc :txops []))))

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
  (prepare [_this _schema data]
    (assoc data :db/id (d/tempid :db.part/user)))

  (fetch [this schema id]
    (d/q '[:find ?e
           :in $ ?id-attr ?id
           :where
           [?e ?id-attr ?id]]
         (d/db conn)
         :db/id id))

  (search [this schema])

  (search [this schema {:keys [pull where] :as opts}])

  (persist! [this schema data]
    (d/transact conn [data]))

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