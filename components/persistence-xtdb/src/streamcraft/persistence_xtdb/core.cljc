(ns streamcraft.persistence-xtdb.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.persistence :as persistence]
            [taoensso.timbre :as log]
            [xtdb.api :as xt]
            [xtdb.node :as xtn]))

(defn- -fetch-q [table id]
  (xt/template (from ~table [* {:xt/id ~id}])))


(defrecord XtdbPersistence [config entity-manager node]
  component/Lifecycle
  (start [this]
    (log/info "Starting XtdbPersistence")
    (-> this
        (assoc :node (xtn/start-node config))))
  (stop [this]
    (log/info "Stopping XtdbPersistence")
    (when node
      (.close node))
    (-> this
        (assoc :entity-manager nil)
        (assoc :node nil)))
  persistence/IPersistence
  (prepare [_this schema data]
    (when (not (em/validate entity-manager schema data))
      (throw (ex-info "Invalid data" {:schema schema
                                      :data   data})))
    (update data :xt/id (fn [id] (or id (random-uuid)))))
  (fetch [_this schema id]
    (or (-> node
            (xt/q (-fetch-q (em/name entity-manager schema) id))
            (first))
        (throw (ex-info "Entity not found" {:schema schema
                                            :id     id}))))
  (search [this schema])
  (search [this schema {:keys [pull where] :as opts}])
  (persist! [_this schema data]
    (xt/execute-tx node [[:put-docs (em/name entity-manager schema) data]]))
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