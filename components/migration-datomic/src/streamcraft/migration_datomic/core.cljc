(ns streamcraft.migration-datomic.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.protocols.api.persistence :as-alias persistence]
            [streamcraft.protocols.api.transformer.schema :as ts]
            [taoensso.timbre :as log]))

(defrecord DatomicMigration [registry persistence-transformer]

  component/Lifecycle

  (start [this]
    (log/info "Starting DatomicMigration")
    this)

  (stop [this]
    (log/info "Stopping DatomicMigration")
    (-> this
        (assoc :registry nil)))

  migration/IMigration
  (gen-migration [_]
    (let [schemas (er/get-entities registry)]
      (transduce (comp (filter #(er/entity? registry %))
                       (mapcat #(ts/transform persistence-transformer %)))
                 conj
                 (vals schemas)))))

(defn make-migration []
  (map->DatomicMigration {}))
