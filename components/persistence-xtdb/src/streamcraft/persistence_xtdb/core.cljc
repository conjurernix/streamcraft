(ns streamcraft.persistence-xtdb.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as entity]
            [streamcraft.protocols.api.persistence :as persistence]
            [xtdb.api :as xt]
            [xtdb.node :as xtn]))

(defn- -fetch-q [table id]
  (xt/template (from ~table [* {:xt/id id}])))


(defrecord XtdbPersistence [config node]
  component/Lifecycle
  (start [this]
    (let [{:keys [xtdb]} config]
      (-> this
          (assoc :node (xtn/start-node xtdb)))))
  (stop [this]
    (when node
      (.close node))
    (-> this
        (assoc :node nil)))
  persistence/IPersistence
  (fetch [_this schema id]
    (xt/q node (-fetch-q (entity/entity-name schema) id)))
  (search [this schema])
  (search [this schema {:keys [pull where] :as opts}])
  (persist! [this schema data])
  (patch! [this schema id data])
  (delete! [this id])

  persistence/ITransactionalPersistence
  (persist [this schema data])
  (patch [this schema id data])
  (delete [this id])
  (clear-txs [this])
  (transact! [this]))

(defn make-persistence []
  (map->XtdbPersistence {}))