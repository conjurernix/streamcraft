(ns streamcraft.migration-datomic.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity-manager.api :as-alias entity]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.protocols.api.observability :as obs]
            [streamcraft.protocols.api.persistence :as-alias persistence]
            [streamcraft.protocols.api.transformer.schema :as ts]))

(defrecord DatomicMigration [obs entity-manager persistence-transformer]

  component/Lifecycle

  (start [this]
    (obs/info! obs :starting-component {:component DatomicMigration})
    this)

  (stop [this]
    (obs/info! obs :stopping-component {:component DatomicMigration})
    (-> this
        (assoc :entity-manager nil)))

  migration/IMigration
  (gen-migration [_]
    (let [schemas (em/get-entities entity-manager)]
      (transduce (comp (filter #(em/entity? entity-manager %))
                       (mapcat #(ts/transform persistence-transformer %)))
                 conj
                 (vals schemas)))))

(defn make-migration []
  (map->DatomicMigration {}))
