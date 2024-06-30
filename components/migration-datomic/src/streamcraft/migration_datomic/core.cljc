(ns streamcraft.migration-datomic.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.protocols.api.schema :as schema]
            [taoensso.timbre :as log]))



(defrecord DatomicMigration [registry epst]

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

      (->> schemas
           vals
           (map #(er/entries registry %))
           (mapcat (fn [entry]
                     (let [{::persistence/keys [type cardinality]
                            ::entity/keys      [doc]
                            :as                _props} (er/properties registry entry)
                           key (er/entry-key registry entry)
                           schema (er/entry-schema registry entry)
                           cardinality (case (or cardinality
                                                 (er/cardinality registry schema))
                                         :one :db.cardinality/one
                                         :many :db.cardinality/many)
                           type (or type
                                    (-> registry
                                        (er/of-type schema)))]

                       (cond-> {:db/ident       key
                                :db/cardinality cardinality
                                :db/valueType   (schema/persistence-schema epst type)}

                         (some? doc) (assoc :db/doc doc)))))))))

(defn make-migration []
  (map->DatomicMigration {}))
