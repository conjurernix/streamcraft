(ns streamcraft.migration-datomic.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity-manager.api :as-alias entity]
            [streamcraft.logging.api :as log]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.protocols.api.persistence :as-alias persistence]
            [streamcraft.protocols.api.transformer.schema :as ts]))

(defrecord DatomicMigration [entity-manager persistence-transformer]

  component/Lifecycle

  (start [this]
    (log/info! :starting-component {:component this})
    this)

  (stop [this]
    (log/info! :stopping-component {:component this})
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
